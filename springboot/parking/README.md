# SABA — Microservicio de Estacionamientos (`parking`)

**Alcance:** proyecto Java + Spring Boot + Gradle/Groovy para abrir con **Apache NetBeans**, en `springboot/parking/`. Implementa exclusivamente el MS de Estacionamientos del capítulo 13 del informe SABA 1.1. No cambia Android, no incluye un BFF ni otro MS.

## Arquitectura

`ParkingController → ParkingService → ParkingServiceImpl → ParkingRepository (JPA) → ParkingEntity → PostgreSQL`.

- `dto/`: define exactamente el JSON que intercambia con Android; no contiene Entities de Room.
- `config/SecurityConfig`: valida JWT Cognito; **sin issuer configurado no admite solicitudes**.
- `exception/`: traduce errores de negocio a códigos HTTP sin revelar trazas internas.
- `db/migration/`: primera migración Flyway de la base de datos propia.
- `src/test/`: unitarias de ServiceImpl y Controller, validación de claims Cognito e integración JPA con H2 (para desarrollo; falta probar contra PostgreSQL real).

## Requisitos

- JDK **26**, como decidiste para el proyecto de NetBeans.
- Gradle Wrapper **9.9.1** (incluido). Internet disponible la primera vez para descargar Gradle y dependencias.
- PostgreSQL instalado y ejecutándose, con BD `saba_parking`.
- Un User Pool Cognito existente para probar rutas protegidas.

## Preparar PostgreSQL

Crear la base de datos (con `psql`, como usuario con permisos):

```sql
CREATE DATABASE saba_parking;
```

La migración `V1__create_parkings.sql` crea la tabla automáticamente; **no crees la tabla a mano**.

## Variables y arranque en PowerShell

En una terminal nueva, desde la carpeta `springboot\parking`:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/saba_parking"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "<tu_contrasena_local>"
$env:COGNITO_ISSUER_URI = "https://cognito-idp.<region>.amazonaws.com/<user_pool_id>"
$env:COGNITO_APP_CLIENT_ID = "<app_client_id>"
.\gradlew.bat clean test
.\gradlew.bat bootRun
```

**Sustituye los marcadores**, sin escribir las credenciales en un commit ni en `application.properties`.

El servidor escucha en **http://localhost:8081** por defecto (`SERVER_PORT` permite cambiarlo). `saba_parking` es la BD de este MS, no la base Room ni la del futuro servicio `incidents`.

`COGNITO_ISSUER_URI` debe ser exactamente el emisor del User Pool (termina en `/<user_pool_id>`). El JWK público se obtiene en `.../.well-known/jwks.json`. El access token debe tener `token_use=access` y, si configuras el App Client ID, un `client_id` coincidente. La firma, expiración y emisor son comprobados por Spring Security. **No se implementa login en el backend.**

Sin `COGNITO_ISSUER_URI`, el servidor puede iniciar para diagnóstico pero todas las rutas seguirán requiriendo autenticación y **ningún token será aceptado**; no hay un modo inseguro sin credenciales.

## Contrato HTTP

| Método | Ruta interna del MS | Contenido | Respuesta |
| --- | --- | --- | --- |
| GET | `/api/parkings` | Authorization: Bearer | 200 + lista `ParkingDto` |
| GET | `/api/parkings/{id}` | Authorization: Bearer | 200 + `ParkingDto` (uso interno futuro) |
| POST | `/api/parkings` | `ParkingCreateDto` | 201 + `ParkingDto` |
| PATCH | `/api/parkings/{id}` | `ParkingPatchDto` | 200 + `ParkingDto` |
| DELETE | `/api/parkings/{id}` | `If-Match: <revision>` | 204 sin cuerpo |

**Importante:** el informe contiene una mención a `PUT`, pero el Retrofit real `Ec2ParkingApiService.kt` define **PATCH**. La implementación sigue el contrato efectivo para no romper Android. La ruta `/api/parkings` aquí es *interna* mientras no exista BFF: Android no se redirige a este MS todavía. El futuro BFF publicará la ruta a Android y utilizará `RestClient` para llamar este microservicio.

### JSON de respuesta (campos reales del cliente Kotlin)

```json
{
  "id": 15,
  "name": "Central",
  "address": "Calle 1",
  "commune": "Santiago",
  "totalSpaces": 120,
  "freeSpaces": 42,
  "isActive": true,
  "revision": 0,
  "updatedAt": 1795000000000,
  "status": "Activo"
}
```

- `updatedAt` es **Long (epoch milisegundos)**, no ISO string.
- `revision` es un `Long` controlado por JPA `@Version`; el primer registro comienza en **0** y luego incrementa. Android siempre debe enviar el valor **recibido** del servidor.
- `status` indica "Activo" / "Inactivo" según `isActive`: no equivale al estado operativo de barreras ni cajeros.
- El ID de PostgreSQL es el **id remoto**. Android mantiene separadamente `id` de Room y `remoteId`.

### Ejemplos de solicitudes

```http
POST /api/parkings
Authorization: Bearer <access_token>
Content-Type: application/json

{"name":"Central","address":"Calle 1","commune":"Santiago","totalSpaces":120,"freeSpaces":42,"isActive":true}
```

```http
PATCH /api/parkings/15
Authorization: Bearer <access_token>
Content-Type: application/json

{"revision":0,"name":"Central Renovado"}
```

```http
DELETE /api/parkings/15
Authorization: Bearer <access_token>
If-Match: 1
```

## Validaciones y códigos

- `400` cuerpo/encabezado incorrecto, nombre vacío, plazas negativas o plazas libres mayores que el total.
- `401` token ausente o inválido (por firma, emisor, expiración, `token_use` y `client_id`, según configuración).
- `404` estacionamiento no existe.
- `409` revisión desactualizada, incluida concurrencia JPA.
- `201` creado; `204` eliminado correctamente.

**Pendiente:** definición de roles/permisos específicos por operación (capítulo 13.19). Aquí se exige JWT válido a todos los endpoints, pero no se inventan autorizaciones por perfil sin especificación. El BFF también tendrá su propia validación JWT.

## Pruebas y alcance real

```powershell
.\gradlew.bat test
.\gradlew.bat jacocoTestReport
.\gradlew.bat build
```

Informe JaCoCo: `build/reports/jacoco/test/html/index.html`.

**Nota de verificación:** los archivos fuente se han preparado conforme al contrato, pero **la compilación completa con Gradle/JDK 26 y una prueba de PostgreSQL/Cognito reales deben efectuarse en tu entorno**. La creación de los archivos por sí sola no demuestra un build exitoso.

## Integración posterior

1. Crear BFF independiente con `ParkingBffController`, `ParkingClient` y `ParkingClientImpl` (`RestClient`), transmitiendo JWT y `If-Match`.
2. Configurar la URL base Retrofit de Android al BFF **solo cuando esté funcionando**.
3. Validar CRUD completo desde el dispositivo y caché Room; luego implementar el MS de Incidencias.

No se incorporan Swagger, tracing, dashboard de monitoreo, módulo Auth ni Clients innecesarios dentro de `parking`.
