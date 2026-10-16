# 04 — Incidencias: CRUD remoto con Spring Boot + Retrofit

> Objetivo: utilizar Incidencias como el CRUD remoto obligatorio del MVP.
> Flujo final: Android y React consumen la misma API Spring Boot.

## 0. Arquitectura

```text
Android Compose                    React
      │                              │
      └────────── HTTP/JSON ─────────┘
                     ↓
               Spring Boot
                     ↓
               Spring Data JPA
                     ↓
                PostgreSQL
```

La app Android **no** se conecta directamente a PostgreSQL.

---

# Parte A — Backend Spring Boot

## Paso 1 — Modelo Incident

Modelo mínimo sugerido:

```java
@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parkingId;
    private Long serviceId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
}
```

No agregues relaciones JPA complejas antes de tener el CRUD básico funcionando.

---

## Paso 2 — Repository Spring

```java
public interface IncidentRepository
        extends JpaRepository<Incident, Long> {
}
```

Con eso Spring Data entrega las operaciones básicas de persistencia.

---

## Paso 3 — Service

Aunque un Controller podría hablar directo con Repository, para el proyecto conviene mantener capas:

```text
Controller
   ↓
Service
   ↓
Repository
```

Responsabilidades del Service:

- Crear estado inicial de la incidencia.
- Validar que los datos mínimos existan.
- Buscar por id.
- Actualizar.
- Eliminar.

---

## Paso 4 — Controller REST

Rutas del contrato `api.md`:

```text
GET    /api/incidents
GET    /api/incidents/{id}
POST   /api/incidents
PUT    /api/incidents/{id}
DELETE /api/incidents/{id}
```

Prueba primero con Postman/curl antes de culpar a Android.

Orden correcto de depuración:

```text
1. BD
2. Spring Boot
3. Postman/curl
4. Retrofit
5. Compose
```

---

# Parte B — Android Retrofit

## Paso 5 — DTOs

`data/remote/IncidentDtos.kt`:

```kotlin
data class IncidentDto(
    val id: Long,
    val parkingId: Long,
    val serviceId: Long,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val createdAt: String
)

data class CreateIncidentRequest(
    val parkingId: Long,
    val serviceId: Long,
    val title: String,
    val description: String,
    val priority: String
)

data class UpdateIncidentRequest(
    val title: String,
    val description: String,
    val priority: String,
    val status: String
)
```

---

## Paso 6 — IncidentApiService

```kotlin
interface IncidentApiService {

    @GET("api/incidents")
    suspend fun getAll(): List<IncidentDto>

    @GET("api/incidents/{id}")
    suspend fun getById(
        @Path("id") id: Long
    ): IncidentDto

    @POST("api/incidents")
    suspend fun create(
        @Body request: CreateIncidentRequest
    ): IncidentDto

    @PUT("api/incidents/{id}")
    suspend fun update(
        @Path("id") id: Long,
        @Body request: UpdateIncidentRequest
    ): IncidentDto

    @DELETE("api/incidents/{id}")
    suspend fun delete(
        @Path("id") id: Long
    )
}
```

---

## Paso 7 — Repository Android

`IncidentRepository` será la capa que el ViewModel utiliza.

```text
IncidentsViewModel
       ↓
IncidentRepository
       ↓
IncidentApiService
```

El Repository puede convertir excepciones HTTP/red a `Result`.

---

## Paso 8 — UiState

Ejemplo:

```kotlin
data class IncidentsUiState(
    val items: List<IncidentDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
```

El Composable muestra `state.items`; no ejecuta Retrofit directamente.

---

## Paso 9 — Casos a demostrar

### Create

Desde Android:

```text
Nueva incidencia
Parking Plaza Italia
Servicio: Lectura LPR
Prioridad: Media
```

Pulsar guardar → POST → aparece en lista.

### Read

Cerrar/reabrir la pantalla → GET → la incidencia sigue en backend.

### Update

Cambiar:

```text
OPEN → IN_PROGRESS
```

→ PUT → recargar → cambio persiste.

### Delete

Eliminar → DELETE → recargar → no aparece.

---

## Paso 10 — Diferencia con Room

No confundas los dos CRUD:

```text
Mantenimiento
→ Room local
→ persiste en el teléfono

Incidencias
→ Spring Boot API
→ persiste en PostgreSQL
```

Eso demuestra dos fuentes de datos distintas, que es precisamente lo útil para la evaluación.

---

## Checklist 04

- [ ] Spring Boot arranca sin Android.
- [ ] PostgreSQL guarda incidencias.
- [ ] GET funciona con Postman/curl.
- [ ] POST funciona con Postman/curl.
- [ ] PUT funciona con Postman/curl.
- [ ] DELETE funciona con Postman/curl si se exige.
- [ ] Retrofit usa el mismo contrato de `api.md`.
- [ ] Android usa Repository.
- [ ] El Composable no hace HTTP directo.
- [ ] React podrá consumir la misma API después.
