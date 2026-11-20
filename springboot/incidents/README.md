# SABA — Microservicio de Incidencias (`incidents`)

Proyecto **Java 26 + Spring Boot 4.0.9 + Gradle Groovy** para abrir en **Apache NetBeans** desde `springboot/incidents/`. Su arquitectura sigue el capítulo **13** de `INFORME_SABA_1_1.md` y es independiente de Android, del microservicio de Estacionamientos y del BFF incorporado en `springboot/bff/`.

> **Estado:** código fuente preparado. No se ha confirmado compilación con JDK 26, PostgreSQL real ni Cognito real. **No** se ha integrado a Android, ni se han implementado cargas remotas de fotografías/vídeos.

## 1. Componentes y responsabilidades

| Archivo | Función | Llama a |
| --- | --- | --- |
| `controller/IncidentController.java` | Recibe las peticiones REST y entrega códigos/respuestas HTTP | `IncidentService` |
| `service/IncidentService.java` | Declara las operaciones de Incidencias | Interfaz implementada por `IncidentServiceImpl` |
| `serviceimpl/IncidentServiceImpl.java` | Valida datos, estados y resolución; transforma DTO/Entity y coordina persistencia | `IncidentRepository`, `ParkingClient` |
| `repository/IncidentRepository.java` | Ejecuta consultas de su propia base PostgreSQL con JPA | `IncidentEntity` |
| `model/IncidentEntity.java` | Define la tabla `incidents`, con `@Version` interno | JPA / PostgreSQL |
| `dto/IncidentDto.java` | Formato JSON de respuesta con nombres compatibles con `domain/model/Incident.kt` | Controller y ServiceImpl |
| `dto/IncidentCreateDto.java` | Formato JSON de creación | Controller y ServiceImpl |
| `dto/IncidentUpdateDto.java` | Formato JSON de actualización y cierre | Controller y ServiceImpl |
| `client/ParkingClient.java` | Interfaz para comprobar `parkingId` remoto | ServiceImpl |
| `client/ParkingClientImpl.java` | Implementa consulta `GET` con Spring `RestClient` | `GET /api/parking/{id}` del MS parking |
| `config/SecurityConfig.java` | Valida la firma, issuer y expiración del JWT mediante Spring Security | Cognito (JWKS públicos) |
| `config/CognitoAccessTokenValidator.java` | Exige claim `token_use=access` y, si se configura, `client_id` | Spring Security |
| `exception/` | Convierte errores en respuestas `400`, `404`, `409`, `503` | Controller/ServiceImpl/Client |
| `src/main/resources/db/migration/` | Migración Flyway de la BD de incidencias | PostgreSQL |
| `src/test/` | Pruebas de lógica, HTTP, Client, JPA y claims Cognito | JUnit / Mockito / Spring Test |

Flujo de creación:

```text
PREVISTO (Android pendiente): Android → Retrofit → BFF/IncidentClient → IncidentController
                                                       ↓
                                             IncidentService (interfaz)
                                                       ↓
                                             IncidentServiceImpl
                                               /              \
                                        ParkingClient     IncidentRepository
                                             ↓                 ↓
                                      REST MS parking     IncidentEntity
                                                               ↓
                                                          PostgreSQL
```

Al crear, Incidencias comprueba que el **ID remoto** del parking existe. Propaga el token del usuario a `parking`. Si `parking` está apagado, el registro devuelve **503**; no almacena silenciosamente una relación inválida. El ID de equipo/servicio (`serviceId`) se comprueba como positivo, pero **no se valida contra un MS de servicios** porque ese contrato todavía no existe.

## 2. Requisitos y bases de datos

- JDK **26** y Gradle Wrapper **9.9.1** incluidos en la configuración del proyecto (el JDK debe estar instalado).
- PostgreSQL ejecutándose (tu Supabase PostgreSQL actual se configura mediante `config/local.properties`). Si usas PostgreSQL local, crear solamente la base de datos; Flyway crea las tablas:

```sql
CREATE DATABASE saba_incidents;
```

- Para registrar incidencias, iniciar también el MS `parking`, en `http://localhost:8081` salvo reconfiguración, con su propia BD y Cognito.
- El inicio de sesión sigue en **Android / Cognito**, no aquí.

En PowerShell, desde `springboot\incidents` (sustituye los ejemplos por tu configuración real):

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/saba_incidents"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "<contrasena_local>"
$env:COGNITO_ISSUER_URI = "https://cognito-idp.<region>.amazonaws.com/<user_pool_id>"
$env:COGNITO_APP_CLIENT_ID = "<app_client_id>"
$env:PARKING_SERVICE_URL = "http://localhost:8081"
.\gradlew.bat clean test
.\gradlew.bat bootRun
```

Puerto por defecto: **8082**, configurable con `SERVER_PORT`. El proyecto `parking` conserva **8081**. El BFF ya incorporado utiliza **8080**.

**Seguridad:** sin `COGNITO_ISSUER_URI` se rechazan todas las solicitudes autenticadas; **no** existe un modo abierto de prueba. El microservicio verifica access tokens Cognito y no almacena contraseñas. El control de permisos por perfil y parking **está pendiente** del contrato de roles; por ahora un token válido da acceso a los endpoints implementados. No desplegar para datos sensibles sin definir y validar esos permisos.

## 3. Rutas REST internas

| HTTP | Ruta | Función | Respuesta |
| --- | --- | --- | --- |
| `GET` | `/api/incidents` | Lista de incidencias; filtros opcionales `parkingId`, `serviceId` | `200` + JSON array |
| `GET` | `/api/incidents/{id}` | Consultar una incidencia | `200` + `IncidentDto`, o `404` |
| `POST` | `/api/incidents` | Registrar incidencia. Verifica parking remoto | `201` + `IncidentDto`, o error controlado |
| `PUT` | `/api/incidents/{id}` | Reemplazar campos editables o registrar resolución | `200` + `IncidentDto`, o error |
| `DELETE` | **No implementado** | El capítulo 13 lo declara opcional; falta definir política de retención/trazabilidad | No se publicará hasta decidir política |

Las rutas **públicas** Android pasan por el BFF ya implementado en el puerto **8080**, una vez configurada la app. Este microservicio aún se puede probar directamente por HTTP con un access token válido.

### POST: ejemplo

```http
POST /api/incidents
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "ticketCode": "SABA-ABC12345",
  "parkingId": 15,
  "serviceId": 3,
  "reporterName": "Operador A",
  "failureType": "Barrera",
  "description": "La barrera de entrada no responde",
  "status": "fuera-servicio"
}
```

`parkingId` debe ser el **ID del servidor parking**, no `ParkingEntity.id` autogenerado por Room. Se conserva la distinción `id` / `remoteId` de Android. `serviceId` requiere en el futuro definir su equivalente remoto; hoy solo se verifica que sea positivo. El servidor asigna `id` y `reportedAt` (epoch en **milisegundos**).

`ticketCode` es obligatorio y único. `status` admite solo `degradado` o `fuera-servicio`, correspondientes a los valores actuales de `IncidentViewModel.kt`.

### PUT: ejemplo de resolución

```http
PUT /api/incidents/9
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "failureType": "Barrera",
  "description": "Sensor de entrada defectuoso",
  "status": "fuera-servicio",
  "isResolved": true,
  "resolvedByName": "Técnico A",
  "resolutionDescription": "Se cambió el sensor"
}
```

PUT requiere `failureType`, `description`, `status` e `isResolved`, aunque solo se quiera cerrar la incidencia. La resolución requiere nombre y descripción de la solución; `resolvedAt` lo asigna el servidor. No se permite reabrir ni modificar una incidencia resuelta **en esta versión**. Estas reglas son la implementación inicial propuesta, no reglas corporativas ya aprobadas.

Para actualizar un incidente **sin resolver**, usar `isResolved:false` y omitir `resolvedByName`/`resolutionDescription`.

### JSON de respuesta

`IncidentDto` conserva los nombres de campos de `Incident.kt`: `id`, `ticketCode`, `parkingId`, `serviceId`, `reporterUserId`, `reporterName`, `failureType`, `description`, `status`, `reportedAt`, `evidenceUri`, `evidenceFileName`, `evidenceMimeType`, `isResolved`, `resolvedByUserId`, `resolvedByName`, `resolutionDescription` y `resolvedAt`.

**Limitación deliberada:** `reporterUserId`, `resolvedByUserId` y los campos de evidencia se devuelven como `null`. Los IDs locales de Room no identifican usuarios remotos, y guardar `file://` / `content://` del teléfono en PostgreSQL **no permitiría a otros dispositivos acceder a la imagen**. Para evidencias se necesitará un endpoint de subida y almacenamiento de archivos/objetos. Actualmente `POST` devuelve **400** si se intenta enviar URI o metadatos de evidencia sin archivo.

## 4. Respuestas y fallos

- **400:** campos inválidos, estado desconocido, IDs no positivos, parking remoto inexistente, URI local de evidencia.
- **401:** sin token válido / token incorrecto para Cognito.
- **404:** ID de incidencia inexistente.
- **409:** código de ticket duplicado, intento de modificar una incidencia ya resuelta o conflicto JPA.
- **503:** Parking no responde o rechaza la verificación por un error de integración.
- **201:** creación correcta; **200:** lectura o actualización correcta.

Los errores no devuelven trazas internas. `RestClient` utiliza timeouts de conexión de 3 segundos y lectura de 5 segundos.

## 5. Pruebas y verificación

```powershell
.\gradlew.bat test
.\gradlew.bat jacocoTestReport
.\gradlew.bat build
```

Reporte JaCoCo: `build/reports/jacoco/test/html/index.html`.

La suite contiene pruebas unitarias para `IncidentServiceImpl`, pruebas HTTP para `IncidentController`, pruebas del Client hacia Parking mediante un servidor simulado, pruebas JPA con H2 y pruebas de claims Cognito. **No certifica todavía pruebas reales contra PostgreSQL ni el extremo a extremo Android → BFF → microservicios**.

### Pendiente para integrar Android

1. Confirmar los identificadores **remotos** de parking y servicio. No enviar IDs locales Room sin conversión.
2. Definir `IncidentRemoteDataSource` / Retrofit para Incidencias dentro de la arquitectura Android vigente (el contrato remoto aún no está implementado allí).
3. Construir BFF con `IncidentBffController`, `IncidentClient`, `IncidentClientImpl` usando `RestClient`.
4. Diseñar carga de archivos, identidad remota y permisos por perfil, y decidir `DELETE`/retención.
5. Probar ambos MS arrancados, la autorización por JWT y los escenarios de error antes de cambiar la URL Android.

Este ZIP **no modifica Android ni el MS Parking existente**. No incluye Auth separado, Swagger/OpenAPI, tracing ni componentes decorativos.
