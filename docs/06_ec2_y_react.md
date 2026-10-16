# 06 — Backend en EC2 + cliente React

> Objetivo: completar la arquitectura final exigida: API propia desplegada y un segundo cliente React consumiendo la misma API.

## 0. La arquitectura final

```text
┌──────────────────┐
│ Android Saba     │
│ Compose/Retrofit │
└────────┬─────────┘
         │
         │ HTTPS JSON
         ▼
┌──────────────────┐
│ Spring Boot API  │
│      EC2         │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ PostgreSQL       │
└──────────────────┘
         ▲
         │
         │ misma API
┌────────┴─────────┐
│ React Saba       │
└──────────────────┘
```

Room continúa local en Android para el CRUD de mantenimiento.

---

# Parte A — Antes de EC2

No despliegues un backend que todavía no funciona localmente.

Checklist local:

```text
Spring Boot inicia
↓
BD conecta
↓
GET incidents funciona
↓
POST funciona
↓
PUT funciona
↓
DELETE funciona
↓
Android consume localhost mediante 10.0.2.2
```

Solo después sube a EC2.

---

# Parte B — EC2

## 1. Qué sube a EC2

La API Spring Boot.

No subas:

- código Android como si fuera servidor;
- contraseña de base de datos dentro del repositorio;
- `.env` con secretos reales al Git público.

## 2. Variables de entorno del backend

Ejemplo conceptual:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Spring Boot debe leerlas desde el entorno/configuración segura.

## 3. Puerto y Security Group

Si Spring Boot escucha en `8080`, el Security Group debe permitir el acceso que corresponda durante la demostración.

En una entrega más seria conviene exponer HTTPS mediante un proxy/dominio en lugar de dejar `8080` abierto globalmente.

## 4. Android cambia solo la BASE_URL

Local:

```dotenv
API_BASE_URL=http://10.0.2.2:8080/
```

Desplegado:

```dotenv
API_BASE_URL=https://TU_HOST/
```

El resto de Retrofit no debería necesitar reescritura.

---

# Parte C — React

## 5. React no necesita una API distinta

La gracia del backend REST es:

```text
Android ─┐
         ├─► /api/incidents
React ───┘
```

Los dos clientes comparten las mismas reglas.

## 6. MVP React recomendado

No intentes portar toda la app Android.

Para demostrar integración basta con priorizar:

1. Login.
2. Dashboard/resumen.
3. Lista de incidencias.
4. Crear/editar incidencia según rol.

## 7. Servicio API en React

Centraliza la URL:

```js
const API_URL = import.meta.env.VITE_API_URL;
```

Y las llamadas en módulos separados, por ejemplo:

```text
src/api/auth.js
src/api/incidents.js
```

No escribas la URL en cada componente.

---

## 8. CORS

Cuando React llama a Spring Boot desde otro origen, puede aparecer un error CORS.

Eso se resuelve en backend configurando explícitamente los orígenes permitidos.

No deshabilites seguridad globalmente solo para esconder el error.

---

## Checklist 06

- [ ] API funciona localmente antes de EC2.
- [ ] API está desplegada en EC2.
- [ ] Secrets no están hardcodeados en Git.
- [ ] Android cambia de entorno mediante `API_BASE_URL`.
- [ ] React usa la misma API.
- [ ] React tiene su URL en variable de entorno.
- [ ] CORS está configurado de forma explícita.
- [ ] Android no contiene credenciales de PostgreSQL.
- [ ] React no contiene credenciales de PostgreSQL.
