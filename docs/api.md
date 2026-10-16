# API Saba — contrato objetivo del MVP

> Estado: **contrato de trabajo del proyecto**, no evidencia de que el backend ya esté implementado.
> No copies rutas de otros proyectos. Cuando Spring Boot quede creado, este archivo debe convertirse en la fuente de verdad compartida entre Android y React.

## 1. Base URL

### Desarrollo local con emulador Android

```text
http://10.0.2.2:8080/
```

`10.0.2.2` permite que el emulador Android llegue al `localhost` del computador anfitrión.

### Entrega desplegada

```text
https://TU_DOMINIO_O_HOST_EC2/
```

La URL debe configurarse fuera del código Kotlin cuando se implemente la etapa de Retrofit.

---

## 2. Autenticación

### POST `/api/auth/login`

Contrato propuesto para Saba:

```json
{
  "rut": "11111111-1",
  "password": "Demo1234"
}
```

Respuesta objetivo:

```json
{
  "accessToken": "TOKEN_DEMO",
  "user": {
    "id": 1,
    "rut": "11111111-1",
    "name": "Usuario Demo",
    "role": "OPERATOR"
  }
}
```

> Importante: si el backend finalmente usa otros nombres (`username`, `token`, etc.), se modifica **este documento y los DTOs**. No se adapta Android a ciegas antes de conocer el contrato real.

El parking seleccionado puede mantenerse inicialmente como estado de UI/contexto de trabajo. No debe enviarse dentro del login salvo que el backend realmente lo requiera.

---

## 3. Parkings

### GET `/api/parkings`

Respuesta de ejemplo:

```json
[
  {
    "id": 1,
    "name": "Parking Plaza Italia"
  }
]
```

---

## 4. Servicios operacionales

### GET `/api/parkings/{parkingId}/services`

```json
[
  {
    "id": 1,
    "name": "Barreras de entrada",
    "status": "OPERATIVO"
  },
  {
    "id": 2,
    "name": "Lectura LPR",
    "status": "DEGRADADO"
  }
]
```

Estados permitidos para el MVP:

```text
OPERATIVO
DEGRADADO
FUERA_DE_SERVICIO
```

---

## 5. Incidencias — CRUD remoto

### GET `/api/incidents`

Lista incidencias.

Filtros opcionales futuros:

```text
/api/incidents?parkingId=1
/api/incidents?status=OPEN
```

### GET `/api/incidents/{id}`

Obtiene una incidencia.

### POST `/api/incidents`

```json
{
  "parkingId": 1,
  "serviceId": 2,
  "title": "Lectura LPR intermitente",
  "description": "El lector falla de forma intermitente con patentes de prueba.",
  "priority": "MEDIUM"
}
```

Respuesta:

```json
{
  "id": 10,
  "parkingId": 1,
  "serviceId": 2,
  "title": "Lectura LPR intermitente",
  "description": "El lector falla de forma intermitente con patentes de prueba.",
  "priority": "MEDIUM",
  "status": "OPEN",
  "createdAt": "2026-10-16T20:00:00"
}
```

### PUT `/api/incidents/{id}`

Ejemplo:

```json
{
  "title": "Lectura LPR intermitente",
  "description": "Revisado por soporte técnico.",
  "priority": "HIGH",
  "status": "IN_PROGRESS"
}
```

### DELETE `/api/incidents/{id}`

Usar solo si el CRUD de la evaluación exige borrado físico. En un sistema real podría preferirse cierre/archivo, pero el MVP académico puede necesitar demostrar `DELETE`.

---

## 6. Errores HTTP esperados

| Código | Significado para la app |
|---|---|
| `200` | Solicitud correcta. |
| `201` | Recurso creado. |
| `400` | Datos inválidos. |
| `401` | Credenciales/token inválidos. |
| `403` | Usuario autenticado sin permiso. |
| `404` | Recurso no encontrado. |
| `500` | Error del servidor. |

Android no debería mostrar al usuario `HTTP 500` como mensaje crudo. El Repository/ViewModel debe traducirlo a mensajes simples.

---

## 7. Autorización

Cuando exista token:

```http
Authorization: Bearer <accessToken>
```

La comprobación de rol debe hacerse en backend además de la UI.

---

## 8. Regla de arquitectura

```text
Android / React
      │
      │ HTTP/JSON
      ▼
Spring Boot API
      │
      │ JPA
      ▼
PostgreSQL
```

**Nunca:**

```text
Android ─────► PostgreSQL directo
React   ─────► PostgreSQL directo
```

Las credenciales de la base de datos pertenecen únicamente al backend.
