'use strict';
const express = require('express');
const { CognitoJwtVerifier } = require('aws-jwt-verify');
const db = require('./database');
const app = express();
const PORT = Number(process.env.PORT || 3000);

if (!process.env.COGNITO_USER_POOL_ID || !process.env.COGNITO_CLIENT_ID) {
  throw new Error('Configura COGNITO_USER_POOL_ID y COGNITO_CLIENT_ID en systemd');
}
const verifier = CognitoJwtVerifier.create({
  userPoolId: process.env.COGNITO_USER_POOL_ID,
  clientId: process.env.COGNITO_CLIENT_ID,
  tokenUse: 'access'
});
app.disable('x-powered-by');
app.use(express.json({limit:'20kb'}));
app.get('/health', (_req,res) => res.json({status:'OK',application:'Saba',message:'Backend funcionando en Amazon EC2'}));

// TODO para producción: restringir CRUD según cognito:groups, no solo autenticación.
async function auth(req,res,next) {
  const match = /^Bearer (\S+)$/i.exec(req.get('Authorization') || '');
  if (!match) return res.status(401).json({error:'Falta access token de Cognito'});
  try { req.cognito = await verifier.verify(match[1]); return next(); }
  catch (_error) { return res.status(401).json({error:'Token de acceso inválido o expirado'}); }
}
app.use('/api', auth);
const str = (required=false,max=250) => ({kind:'string',required,max});
const integer = (required=false) => ({kind:'integer',required});
const bool = () => ({kind:'boolean'});
const nullableString = (max=250) => ({kind:'string',nullable:true,max});
const nullableInteger = () => ({kind:'integer',nullable:true});
// Definiciones estáticas: jamás se concatenan nombres de tablas o columnas del usuario.
const resources = {
 parkings: {table:'parkings',fields:{
  name:str(true,100),address:str(false,240),commune:str(false,100),totalSpaces:integer(),freeSpaces:integer(),isActive:bool()
 }, defaults:{address:'',commune:'',totalSpaces:0,freeSpaces:0,isActive:true}},
 services: {table:'services',fields:{
  parkingId:integer(true),name:str(true,100),type:str(true,60),detail:str(false,500),
  status:str(false,40),problem:nullableString(500)
 },defaults:{detail:'',status:'operativo',problem:null}},
 incidents: {table:'incidents',fields:{
  ticketCode:str(true,70),parkingId:integer(true),serviceId:integer(true),
  reporterUserId:nullableInteger(),reporterName:str(false,100),failureType:str(true,100),
  description:str(true,240),status:str(false,40),reportedAt:integer(),
  evidenceUri:nullableString(500), evidenceFileName:nullableString(150), evidenceMimeType:nullableString(100),
  isResolved:bool(),resolvedByUserId:nullableInteger(),resolvedByName:nullableString(100),
  resolutionDescription:nullableString(1000),resolvedAt:nullableInteger()
 },defaults:{reporterName:'',status:'degradado',isResolved:false}},
 users: {table:'users',fields:{
  name:str(true,100),rut:nullableString(30),role:str(false,60),isActive:bool(),
  assignedParkingId:nullableInteger(),cognitoSub:nullableString(100)
 },defaults:{role:'operador',isActive:true}}
};
function validId(raw) {
 const n=Number(raw);return Number.isSafeInteger(n)&&n>0?n:null;
}
function normalize(field,spec,value) {
 if(value===null && spec.nullable)return null;
 if(spec.kind==='string' && typeof value==='string' && value.trim().length <= spec.max && (!spec.required || value.trim().length>0)) return value.trim();
 if(spec.kind==='integer' && Number.isSafeInteger(value) && value >= 0) return value;
 if(spec.kind==='boolean' && typeof value==='boolean') return value ? 1 : 0;
 throw new Error(`Campo ${field} inválido`);
}
function validate(spec,body,creation) {
 if(!body || typeof body !== 'object' || Array.isArray(body))throw new Error('Se requiere un objeto JSON');
 const obj = creation ? {...spec.defaults} : {};
 for(const [key,value] of Object.entries(body)) {
  if(key==='revision')continue;
  if(!(key in spec.fields)) throw new Error(`Campo desconocido: ${key}`);
  obj[key]=normalize(key,spec.fields[key],value);
 }
 if(creation) for(const [key,rule] of Object.entries(spec.fields)) {
  if(rule.required && (obj[key]===undefined || obj[key]===null || obj[key]===''))throw new Error(`Campo obligatorio: ${key}`);
 }
 if(spec.table==='parkings' && (creation || 'totalSpaces' in obj || 'freeSpaces' in obj)) {
  if(creation && obj.freeSpaces>obj.totalSpaces)throw new Error('freeSpaces no puede superar totalSpaces');
 }
 if(spec.table==='services' && 'status' in obj && !['operativo','degradado','fuera-servicio'].includes(obj.status))throw new Error('Estado del equipo no válido');
 if(spec.table==='incidents' && 'status' in obj && !['degradado','fuera-servicio'].includes(obj.status))throw new Error('Estado de la incidencia no válido');
 return obj;
}
// Una incidencia siempre debe pertenecer al mismo estacionamiento que su equipo.
function checkRelations(table, values, previous = {}) {
 if (table !== 'incidents') return;
 const serviceId = values.serviceId ?? previous.serviceId;
 const parkingId = values.parkingId ?? previous.parkingId;
 const service = db.prepare('SELECT parkingId FROM services WHERE id=?').get(serviceId);
 if (!service || service.parkingId !== parkingId) {
  throw new Error('La incidencia debe usar un equipo del mismo estacionamiento');
 }
}
function shape(row) {
 if(!row)return row;
 const out={...row};
 for(const key of ['isActive','isResolved']) if(key in out)out[key]=Boolean(out[key]);
 return out;
}
function dbError(res,e) {
 console.error('SQLite:',e.message);
 if(/constraint|FOREIGN KEY|UNIQUE/i.test(e.message))return res.status(409).json({error:'Conflicto: ID relacionado, duplicado o recurso en uso'});
 return res.status(500).json({error:'Error de base de datos'});
}
for(const [resource,spec] of Object.entries(resources)) {
 const table=spec.table;
 app.get(`/api/${resource}`,(_req,res)=>{
  try {res.json(db.prepare(`SELECT * FROM ${table} ORDER BY id LIMIT 500`).all().map(shape));}
  catch(e){dbError(res,e);}
 });
 app.get(`/api/${resource}/:id`,(req,res)=>{
  const id=validId(req.params.id);if(!id)return res.status(400).json({error:'ID inválido'});
  try {const found=db.prepare(`SELECT * FROM ${table} WHERE id=?`).get(id);return found?res.json(shape(found)):res.status(404).json({error:'No existe'});}
  catch(e){dbError(res,e);}
 });
 app.post(`/api/${resource}`,(req,res)=>{
  try {
   const values=validate(spec,req.body,true);checkRelations(table,values);values.updatedAt=Date.now();
   if(table==='services')values.lastUpdatedAt=values.updatedAt;
   if(table==='incidents')values.reportedAt=values.reportedAt||values.updatedAt;
   const keys=Object.keys(values);
   const query=`INSERT INTO ${table} (${keys.join(',')}) VALUES (${keys.map(()=>'?').join(',')})`;
   const created=db.prepare(query).run(...Object.values(values));
   const item=db.prepare(`SELECT * FROM ${table} WHERE id=?`).get(Number(created.lastInsertRowid));
   return res.status(201).json(shape(item));
  }catch(e){if(e.message.startsWith('Campo')||e.message.startsWith('Se requiere')||e.message.includes('freeSpaces')||e.message.includes('Estado')||e.message.startsWith('La incidencia'))return res.status(400).json({error:e.message});return dbError(res,e);}
 });
 // PATCH parcial, con número de revisión obligatorio. Evita sobreescrituras silenciosas.
 app.patch(`/api/${resource}/:id`,(req,res)=>{
  const id=validId(req.params.id);if(!id)return res.status(400).json({error:'ID inválido'});
  try {
   const revision=req.body?.revision;
   if(!Number.isSafeInteger(revision)||revision<1)return res.status(400).json({error:'Falta revision válida'});
   const current=db.prepare(`SELECT * FROM ${table} WHERE id=?`).get(id);
   if(!current)return res.status(404).json({error:'No existe'});
   if(current.revision!==revision)return res.status(409).json({error:'El registro cambió en el servidor; actualiza antes de modificar'});
   const values=validate(spec,req.body,false);
   checkRelations(table,values,current);
   if(!Object.keys(values).length)return res.status(400).json({error:'No hay cambios'});
   if(table==='parkings') {
    const total=values.totalSpaces??current.totalSpaces,free=values.freeSpaces??current.freeSpaces;
    if(free>total)return res.status(400).json({error:'freeSpaces no puede superar totalSpaces'});
   }
   if(table==='services')values.lastUpdatedAt=Date.now();
   values.updatedAt=Date.now();
   const keys=Object.keys(values);
   const set=keys.map(k=>`${k}=?`).join(',');
   const result=db.prepare(`UPDATE ${table} SET ${set}, revision=revision+1 WHERE id=? AND revision=?`).run(...Object.values(values),id,revision);
   if(!result.changes)return res.status(409).json({error:'Conflicto de revisión'});
   return res.json(shape(db.prepare(`SELECT * FROM ${table} WHERE id=?`).get(id)));
  }catch(e){if(e.message.startsWith('Campo')||e.message.includes('freeSpaces')||e.message.includes('Estado')||e.message.startsWith('La incidencia'))return res.status(400).json({error:e.message});return dbError(res,e);}
 });
 app.delete(`/api/${resource}/:id`,(req,res)=>{
  const id=validId(req.params.id);if(!id)return res.status(400).json({error:'ID inválido'});
  const revision=Number(req.get('If-Match'));
  if(!Number.isSafeInteger(revision)||revision<1)return res.status(428).json({error:'Se requiere If-Match con revisión actual'});
  try {
   const result=db.prepare(`DELETE FROM ${table} WHERE id=? AND revision=?`).run(id,revision);
   if(result.changes)return res.status(204).send();
   const exists=db.prepare(`SELECT id FROM ${table} WHERE id=?`).get(id);
   return res.status(exists?409:404).json({error:exists?'Revisión desactualizada':'No existe'});
  }catch(e){return dbError(res,e);}
 });
}
// Bloqueo de URLs desconocidas (nunca devolver detalles internos al cliente).
app.use((req,res)=>res.status(404).json({error:'Ruta no encontrada'}));
app.listen(PORT,'127.0.0.1',()=>console.log(`Saba backend listo en 127.0.0.1:${PORT}`));
