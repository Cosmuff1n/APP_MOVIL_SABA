'use strict';
const path = require('node:path');
const { DatabaseSync } = require('node:sqlite');
// El archivo anterior saba.db se reutiliza; NUNCA se borra al actualizar el backend.
const db = new DatabaseSync(path.join(__dirname, 'saba.db'));
db.exec('PRAGMA foreign_keys = ON; PRAGMA journal_mode = WAL; PRAGMA busy_timeout = 5000;');

db.exec(`
 CREATE TABLE IF NOT EXISTS parkings (
   id INTEGER PRIMARY KEY AUTOINCREMENT,
   name TEXT NOT NULL,
   status TEXT NOT NULL DEFAULT 'Activo'
 );
`);
function ensureColumn(table, column, ddl) {
  const exists = db.prepare(`PRAGMA table_info(${table})`).all().some(c => c.name === column);
  if (!exists) db.exec(`ALTER TABLE ${table} ADD COLUMN ${column} ${ddl}`);
}
// Migración no destructiva de nuestra primera tabla de pruebas, que tenía solo id/name/status.
ensureColumn('parkings', 'address', "TEXT NOT NULL DEFAULT ''");
ensureColumn('parkings', 'commune', "TEXT NOT NULL DEFAULT ''");
ensureColumn('parkings', 'totalSpaces', 'INTEGER NOT NULL DEFAULT 0');
ensureColumn('parkings', 'freeSpaces', 'INTEGER NOT NULL DEFAULT 0');
ensureColumn('parkings', 'isActive', 'INTEGER NOT NULL DEFAULT 1');
ensureColumn('parkings', 'revision', 'INTEGER NOT NULL DEFAULT 1');
ensureColumn('parkings', 'updatedAt', 'INTEGER NOT NULL DEFAULT 0');
// Nuestros datos de prueba pueden decir Inactivo en texto.
db.exec("UPDATE parkings SET isActive = 0 WHERE LOWER(status) = 'inactivo' AND isActive = 1;");

db.exec(`
 CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  rut TEXT UNIQUE,
  role TEXT NOT NULL DEFAULT 'operador',
  isActive INTEGER NOT NULL DEFAULT 1,
  assignedParkingId INTEGER,
  cognitoSub TEXT UNIQUE,
  revision INTEGER NOT NULL DEFAULT 1,
  updatedAt INTEGER NOT NULL DEFAULT 0,
  FOREIGN KEY (assignedParkingId) REFERENCES parkings(id) ON DELETE RESTRICT
 );
 CREATE TABLE IF NOT EXISTS services (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  parkingId INTEGER NOT NULL,
  name TEXT NOT NULL,
  type TEXT NOT NULL,
  detail TEXT NOT NULL DEFAULT '',
  status TEXT NOT NULL DEFAULT 'operativo',
  lastUpdatedAt INTEGER NOT NULL DEFAULT 0,
  problem TEXT,
  revision INTEGER NOT NULL DEFAULT 1,
  updatedAt INTEGER NOT NULL DEFAULT 0,
  FOREIGN KEY (parkingId) REFERENCES parkings(id) ON DELETE RESTRICT
 );
 CREATE TABLE IF NOT EXISTS incidents (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  ticketCode TEXT NOT NULL UNIQUE,
  parkingId INTEGER NOT NULL,
  serviceId INTEGER NOT NULL,
  reporterUserId INTEGER,
  reporterName TEXT NOT NULL DEFAULT '',
  failureType TEXT NOT NULL,
  description TEXT NOT NULL,
  status TEXT NOT NULL DEFAULT 'degradado',
  reportedAt INTEGER NOT NULL DEFAULT 0,
  evidenceUri TEXT,
  evidenceFileName TEXT,
  evidenceMimeType TEXT,
  isResolved INTEGER NOT NULL DEFAULT 0,
  resolvedByUserId INTEGER,
  resolvedByName TEXT,
  resolutionDescription TEXT,
  resolvedAt INTEGER,
  revision INTEGER NOT NULL DEFAULT 1,
  updatedAt INTEGER NOT NULL DEFAULT 0,
  FOREIGN KEY (parkingId) REFERENCES parkings(id) ON DELETE RESTRICT,
  FOREIGN KEY (serviceId) REFERENCES services(id) ON DELETE RESTRICT,
  FOREIGN KEY (reporterUserId) REFERENCES users(id) ON DELETE RESTRICT,
  FOREIGN KEY (resolvedByUserId) REFERENCES users(id) ON DELETE RESTRICT
 );
 CREATE INDEX IF NOT EXISTS idx_services_parking ON services(parkingId);
 CREATE INDEX IF NOT EXISTS idx_incidents_parking ON incidents(parkingId);
 CREATE INDEX IF NOT EXISTS idx_incidents_service ON incidents(serviceId);
`);
module.exports = db;
