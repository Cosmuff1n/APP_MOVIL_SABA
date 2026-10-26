# Saba — Explicación completa del proyecto Android

> **Estado al 23 de octubre de 2026**  
> Proyecto: **Saba** — Aplicación Android con Kotlin, Jetpack Compose, MVVM, Room y Repositories.  
> **Objetivo de este documento:** explicar qué construimos, cómo se relacionan sus componentes y qué queda pendiente antes de la entrega.

## Índice

1. [¿Qué es Saba y qué estamos construyendo?](#1-qué-es-saba-y-qué-estamos-construyendo)
2. [Nuestra arquitectura: MVVM + Repository + Room](#2-nuestra-arquitectura-mvvm--repository--room)
3. [Las cuatro Entities: modelos de datos](#3-las-cuatro-entities-modelos-de-datos)
4. [Los DAOs: acceso a la base de datos](#4-los-daos-acceso-a-la-base-de-datos)
5. [SabaDatabase.kt: la base de datos Room](#5-sabadatabasekt-la-base-de-datos-room)
6. [Los Repositories: lo último que agregamos](#6-los-repositories-lo-último-que-agregamos)
7. [Los ViewModels: lógica y estado de pantalla](#7-los-viewmodels-lógica-y-estado-de-pantalla)
8. [Jetpack Compose: nuestras pantallas](#8-jetpack-compose-nuestras-pantallas)
9. [El funcionamiento del login](#9-el-funcionamiento-del-login)
10. [Amazon Cognito, Retrofit y la API](#10-amazon-cognito-retrofit-y-la-api)
11. [Estado actual del proyecto](#11-estado-actual-del-proyecto)
12. [Nuestro próximo paso](#12-nuestro-próximo-paso)

---

## 1. ¿Qué es Saba y qué estamos construyendo?

Saba es una aplicación Android destinada a administrar y monitorear estacionamientos y sus equipos tecnológicos.

Un estacionamiento puede tener barreras automáticas, lectores de patentes (LPR), terminales de pago (POS), cajeros y otros dispositivos. Si uno falla, Saba debería permitir **registrar una incidencia, asociarla al equipo correspondiente y consultar su estado**.

Hasta este momento hemos construido dos grandes partes:

- **Inicio de sesión simulado:** ya lo ejecutamos correctamente en el celular Samsung.
- **Infraestructura de datos locales:** cuatro Entities, cuatro DAOs, la base de datos Room y cuatro Repositories. Los Repositories compilan, pero aún debemos conectarlos a las pantallas para probar las operaciones reales de almacenamiento.

### Tres conceptos que no debemos confundir

| Concepto | Significado |
| --- | --- |
| **Compila** | El código supera la compilación y sus comprobaciones correspondientes. |
| **Funciona** | Las funciones hacen lo esperado cuando se ejecutan. |
| **Persiste información** | Los datos se guardan y pueden recuperarse posteriormente, incluso después de cerrar la aplicación. |

La compilación exitosa de los Repositories **no prueba por sí sola** que el flujo de creación y consulta de datos ya funcione desde la interfaz.

---

## 2. Nuestra arquitectura: MVVM + Repository + Room

La estructura que estamos utilizando es:

```text
┌─────────────────────────────────────┐
│ VIEW: Jetpack Compose              │
│ Pantallas e interacción            │
└──────────────────┬──────────────────┘
                   ↕
┌─────────────────────────────────────┐
│ VIEWMODEL                           │
│ Estado y lógica de presentación     │
└──────────────────┬──────────────────┘
                   ↕
┌─────────────────────────────────────┐
│ REPOSITORY                          │
│ Acceso a datos y reglas del negocio│
└──────────────────┬──────────────────┘
                   ↕
┌─────────────────────────────────────┐
│ DAO: Room                           │
│ Consultas y operaciones SQLite      │
└──────────────────┬──────────────────┘
                   ↕
┌─────────────────────────────────────┐
│ SQLite: base de datos local         │
│ Almacenamiento en el dispositivo    │
└─────────────────────────────────────┘
```

**MVVM** significa **Model–View–ViewModel**. Además, estamos incorporando una capa **Repository** para organizar el acceso a los datos.

Imagina que un usuario desea reportar una barrera averiada:

| Capa | ¿Qué hace? |
| --- | --- |
| **View** | Muestra un formulario y el botón **Reportar**. |
| **ViewModel** | Recibe la información y controla los estados de la pantalla. |
| **Repository** | Coordina el guardado y comprueba reglas del negocio. |
| **DAO** | Ejecuta la operación SQL correspondiente. |
| **Room / SQLite** | Almacena el registro en la base de datos local. |

### ¿Por qué dividir el proyecto en capas?

Por el principio de **separación de responsabilidades**. Una pantalla no debería encargarse de escribir SQL; un DAO no debería controlar la navegación; y una Entity no debería decidir cómo se dibuja una pantalla.

Separar estas tareas facilita el mantenimiento, las pruebas y la incorporación de nuevas funcionalidades.

> **Importante:** el login actual también usa View y ViewModel, pero su `AuthRepository` se comunica con un servicio de autenticación simulado en vez de utilizar los DAOs de Room.

---

## 3. Las cuatro Entities: modelos de datos

Una **Entity** representa una tabla de la base de datos. Sus propiedades representan las columnas de esa tabla.

Tenemos cuatro clases principales:

| Entity | Tabla / función |
| --- | --- |
| `Parking` | Estacionamientos administrados por Saba. |
| `Service` | Equipos y servicios tecnológicos vinculados a estacionamientos. |
| `User` | Perfiles de usuarios o trabajadores. |
| `Incident` | Reportes o tickets de incidencias. |

### Relaciones conceptuales

```text
                      ┌───────────────┐
                      │    Parking    │
                      │ Estacionamiento│
                      └──────┬────────┘
                             │
               ┌─────────────┴─────────────┐
               │                           │
               ▼                           ▼
        ┌──────────────┐            ┌────────────┐
        │   Service    │            │    User    │
        │    Equipos   │            │Trabajadores│
        └──────┬───────┘            └──────┬─────┘
               │                           │
               └────────────┬──────────────┘
                            ▼
                   ┌────────────────┐
                   │    Incident    │
                   │ Ticket de falla│
                   └────────────────┘
```

Este dibujo resume las asociaciones lógicas; las columnas y claves foráneas concretas se definen en los archivos Entity.

### `Parking.kt` — Estacionamientos

La tabla `parkings` almacena información de lugares que administra Saba, como nombre, dirección, comuna, número de plazas, plazas disponibles y estado de actividad.

**Ejemplo ficticio:**

| Campo | Valor |
| --- | --- |
| ID | `1` |
| Nombre | `Parking Plaza Italia` |
| Comuna | Providencia |
| Plazas totales | `200` |
| Plazas libres | `65` |

Estos datos son solo ilustrativos; no son registros que ya existan en el teléfono.

### `Service.kt` — Equipos tecnológicos

Un servicio representa un equipo o dispositivo: por ejemplo, una barrera de salida o un lector de patentes.

Cada servicio puede asociarse a un estacionamiento mediante `parkingId`.

```kotlin
Service(
    parkingId = 1,
    name = "Barrera de salida",
    type = "barriers",
    status = "operativo"
)
```

Fragmento ilustrativo: el constructor completo puede requerir otras propiedades según la Entity real.

Estados posibles mencionados en nuestro modelo: `"operativo"`, `"degradado"` y `"fuera-servicio"`.

### `User.kt` — Trabajadores

Guarda el **perfil local** de una persona, con campos como:

- `name`: nombre.
- `rut`: RUT.
- `role`: rol del usuario.
- `assignedParkingId`: estacionamiento asignado.
- `cognitoSub`: identificador que podría vincular el perfil con Cognito.

**Importante:** esta tabla **no almacena contraseñas ni autentica personas**. Tener un perfil local y validar credenciales son responsabilidades diferentes.

### `Incident.kt` — Incidencias

Es una Entity más compleja porque representa una falla o problema que debe registrarse y atenderse.

Puede incluir código de ticket, estacionamiento, servicio afectado, tipo de falla, descripción, responsable, fecha, estado, evidencia y resolución.

```kotlin
Incident(
    ticketCode = "INC-001",
    parkingId = 1,
    serviceId = 3,
    failureType = "Barrera bloqueada",
    description = "La barrera no se levanta",
    status = "fuera-servicio"
)
```

Este ejemplo es conceptual: no reproduce obligatoriamente todos los parámetros del constructor real.

Aquí `parkingId = 1` identifica el estacionamiento y `serviceId = 3` identifica el dispositivo afectado.

### Anotaciones de Room

```kotlin
@Entity(tableName = "parkings")
data class Parking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val address: String
)
```

Este ejemplo está simplificado con fines educativos.

- **`@Entity`**: declara que la clase representa una tabla.
- **`@PrimaryKey`**: define la clave primaria de cada registro.
- **`autoGenerate = true`**: solicita la generación automática del identificador.
- **`data class`**: proporciona funciones útiles como `copy()` y comparación por contenido.
- **`ForeignKey`**: permite establecer referencias entre tablas.
- **`Index`**: puede mejorar búsquedas o exigir unicidad de ciertos valores.

En nuestras relaciones se emplea `onDelete = RESTRICT` en las referencias correspondientes: evita eliminar un registro padre mientras existan registros relacionados que dependan de él.

---

## 4. Los DAOs: acceso a la base de datos

**DAO** significa **Data Access Object**, u **Objeto de Acceso a Datos**.

Las Entities definen **qué información existe**. Los DAOs definen **cómo consultarla o modificarla**.

Ejemplo de operaciones de `ParkingDao`:

```kotlin
@Dao
interface ParkingDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(parking: Parking): Long

    @Query("SELECT * FROM parkings ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<Parking>>

    @Query("SELECT * FROM parkings WHERE id = :parkingId LIMIT 1")
    suspend fun getById(parkingId: Long): Parking?
}
```

### Anotaciones habituales

| Anotación | Para qué sirve |
| --- | --- |
| `@Insert` | Insertar un nuevo registro. |
| `@Update` | Modificar un registro existente. |
| `@Delete` | Eliminar un registro. |
| `@Query` | Definir consultas SQL personalizadas. |

Estas operaciones forman parte de **CRUD**: *Create, Read, Update, Delete*.

### ¿Qué significa `suspend`?

```kotlin
suspend fun getById(parkingId: Long): Parking?
```

` suspend ` marca una función que puede suspender su ejecución sin bloquear el hilo mientras espera una operación. Suele utilizarse junto con **corrutinas**.

La función devuelve:

- Un objeto `Parking` si encuentra el registro.
- `null` si el identificador no corresponde a ninguno.

### ¿Qué significa `Flow`?

```kotlin
fun observeAll(): Flow<List<Parking>>
```

`Flow` representa una secuencia de resultados emitidos a lo largo del tiempo. Si una consulta Room observable detecta cambios relevantes en la tabla, puede emitir una lista actualizada.

**Ejemplo conceptual:**

```text
Estado inicial de parkings:
- Parking Plaza Italia
- Parking Central

Se inserta «Parking Los Leones».

Nueva emisión de observeAll():
- Parking Central
- Parking Los Leones
- Parking Plaza Italia
```

Así una pantalla que observe los resultados puede actualizarse sin tener que consultar manualmente toda la base de datos cada vez.

---

## 5. `SabaDatabase.kt`: la base de datos Room

`SabaDatabase.kt` conecta las cuatro Entities con el sistema Room.

```kotlin
@Database(
    entities = [
        User::class,
        Parking::class,
        Service::class,
        Incident::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SabaDatabase : RoomDatabase()
```

### Room y SQLite no son lo mismo

- **SQLite**: es el motor que guarda y consulta los datos físicamente.
- **Room**: es una biblioteca de Android que facilita trabajar con SQLite desde clases y anotaciones de Kotlin.

Room genera código para implementar nuestros DAOs a partir de sus definiciones.

### ¿Para qué sirve `getInstance()`?

```kotlin
fun getInstance(context: Context): SabaDatabase {
    return INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
            context.applicationContext,
            SabaDatabase::class.java,
            "saba_database"
        ).build().also { database -> INSTANCE = database }
    }
}
```

El objetivo es reutilizar **una sola instancia compartida** de la base de datos: patrón **Singleton**.

- `INSTANCE`: conserva la referencia compartida.
- `synchronized(this)`: evita que varios hilos inicialicen simultáneamente la misma instancia.
- `applicationContext`: permite que la base de datos no dependa de una pantalla concreta.
- `"saba_database"`: nombre del archivo de base de datos.
- `@Volatile`: ayuda a que la referencia compartida sea visible correctamente entre hilos.

### ¿Qué es KSP?

**KSP** (*Kotlin Symbol Processing*) procesa anotaciones del código Kotlin durante la compilación.

Gracias a KSP, Room puede generar las implementaciones de interfaces marcadas con `@Dao` y el código necesario de la base de datos.

Por eso no necesitamos escribir manualmente todas las clases que ejecutan SQL.

---

## 6. Los Repositories: lo último que agregamos

Un **Repository** organiza el acceso a los datos. Puede:

- Delegar consultas en un DAO.
- Combinar diferentes fuentes de información.
- Centralizar reglas del negocio.
- Ocultar detalles de almacenamiento al ViewModel.

### `ParkingRepository.kt`

```kotlin
class ParkingRepository(
    private val parkingDao: ParkingDao
) {

    fun observeAll(): Flow<List<Parking>> =
        parkingDao.observeAll()

    suspend fun getById(parkingId: Long): Parking? =
        parkingDao.getById(parkingId)

    suspend fun insert(parking: Parking): Long =
        parkingDao.insert(parking)
}
```

Este fragmento representa las operaciones principales utilizadas en la explicación.

`private val parkingDao: ParkingDao` indica que el Repository **recibe** el DAO por constructor en vez de crearlo por su cuenta.

Por ejemplo:

```kotlin
parkingRepository.insert(parking)
```

Dentro del Repository esa llamada se delega al DAO:

```kotlin
parkingDao.insert(parking)
```

Room se encarga de completar la operación con SQLite.

Algunos métodos del Repository parecen sencillos porque delegan directamente en un DAO. Eso es válido: también permiten mantener desacoplado el ViewModel de los detalles de almacenamiento.

### `IncidentRepository.kt`: una regla de negocio real

En nuestro Repository de incidencias validamos que el servicio involucrado pertenezca al estacionamiento indicado.

Ejemplo de la lógica:

```kotlin
suspend fun insert(incident: Incident): Long {
    validateServiceParking(incident)
    return incidentDao.insert(incident)
}
```

La validación sigue esta idea:

```kotlin
private suspend fun validateServiceParking(
    incident: Incident
) {
    val service = serviceDao.getById(incident.serviceId)
        ?: throw IllegalArgumentException(
            "No existe el servicio."
        )

    require(service.parkingId == incident.parkingId) {
        "El servicio no pertenece al estacionamiento."
    }
}
```

**Ejemplo:** si el servicio `12` pertenece al estacionamiento `2`, no tiene sentido guardar una incidencia que lo vincule con el estacionamiento `5`.

El Repository detecta la contradicción y rechaza el registro. Esta es una **regla del negocio**, no una decisión de interfaz.

### Responsabilidades de nuestros Repositories

| Repository | Responsabilidad |
| --- | --- |
| `UserRepository` | Administrar y consultar perfiles locales. |
| `ParkingRepository` | Consultar y administrar estacionamientos. |
| `ServiceRepository` | Administrar equipos y sus estados. |
| `IncidentRepository` | Gestionar incidencias y validar su asociación con servicios. |
| `AuthRepository` | Realizar las solicitudes de autenticación. |

`AuthRepository` es distinto de los otros cuatro: se conecta con un servicio de autenticación, no con Room.

### ¿Para qué sirve `SabaRepositories.kt`?

```kotlin
class SabaRepositories(context: Context) {
    private val database =
        SabaDatabase.getInstance(context.applicationContext)

    val userRepository =
        UserRepository(database.userDao())

    val parkingRepository =
        ParkingRepository(database.parkingDao())

    val serviceRepository =
        ServiceRepository(database.serviceDao())

    val incidentRepository = IncidentRepository(
        database.incidentDao(),
        database.serviceDao()
    )
}
```

Este archivo **centraliza la creación** de los Repositories usando la misma instancia de Room. Es una solución sencilla de organización de dependencias, sin incorporar todavía frameworks como **Hilt**.

**Estado actual:** todavía no tenemos conectados estos nuevos Repositories a ViewModels y pantallas reales. Los archivos compilan, pero la interfaz aún no hace uso de ellos.

---

## 7. Los ViewModels: lógica y estado de pantalla

En este punto de nuestro proyecto existen ViewModels para la parte de autenticación y Home:

- `LoginViewModel`
- `HomeViewModel`

Un **ViewModel** contiene el estado que la interfaz necesita y gestiona los eventos del usuario, sin encargarse de dibujar directamente las vistas.

### `StateFlow` y `MutableStateFlow`

En nuestro `LoginViewModel.kt` tenemos el patrón:

```kotlin
private val _uiState =
    MutableStateFlow(LoginUiState())

val uiState: StateFlow<LoginUiState> =
    _uiState.asStateFlow()
```

- `_uiState`: privado y **modificable** por el ViewModel.
- `uiState`: público, **observable y de solo lectura** para la pantalla.

Así Compose puede leer el estado, pero no cambiarlo de manera arbitraria.

### ¿Qué contiene `LoginUiState`?

| Propiedad | Significado |
| --- | --- |
| `rut` | RUT ingresado. |
| `password` | Contraseña ingresada. |
| `isPasswordVisible` | Controla si la contraseña se muestra. |
| `isLoading` | Indica si una operación está en curso. |
| `rutError` | Error de validación del RUT. |
| `passwordError` | Error del campo contraseña. |
| `loginError` | Error general durante la autenticación. |
| `isSuccess` | Indica que el inicio de sesión fue exitoso. |
| `authToken` | Token obtenido después del login. |

### ¿Cómo funciona `copy()`?

```kotlin
fun onRutChange(value: String) {
    _uiState.update {
        it.copy(
            rut = value,
            rutError = null,
            loginError = null
        )
    }
}
```

Cuando se modifica el RUT, el ViewModel crea una **nueva versión del estado**, cambiando solamente las propiedades indicadas.

`copy()` conserva las demás propiedades originales.

### Flujo unidireccional de datos

```text
Usuario escribe en el campo RUT
              ↓
onRutChange(nuevoValor)
              ↓
LoginViewModel actualiza uiState
              ↓
Compose observa el nuevo estado
              ↓
La pantalla muestra el valor actualizado
```

Este mismo patrón será útil para mostrar una lista de estacionamientos, servicios o incidencias.

---

## 8. Jetpack Compose: nuestras pantallas

**Jetpack Compose** es el sistema de construcción de interfaces declarativas que estamos utilizando.

En lugar de ordenar manualmente cada modificación de un componente gráfico, describimos **cómo debe verse la pantalla dado un estado**.

En `LoginScreen.kt` observamos el `StateFlow` del ViewModel así:

```kotlin
val state by viewModel.uiState.collectAsState()
```

Cuando cambia un valor que Compose está observando, se pueden recomponer las partes afectadas de la interfaz.

Por ejemplo, si `isLoading` cambia de `false` a `true`, la pantalla puede mostrar un indicador de carga y deshabilitar el botón de inicio de sesión.

### Diferencia entre `LoginScreen` y `LoginContent`

**`LoginScreen`**:

- Obtiene el ViewModel.
- Observa el estado.
- Conecta los eventos con el ViewModel.

**`LoginContent`**:

- Dibuja el formulario.
- Recibe valores y callbacks.
- Puede utilizarse con `@Preview` sin necesitar todo el sistema de autenticación.

También existe `AuthCard.kt`, un componente visual reutilizable que se utiliza en las pantallas de Login y Home.

---

## 9. El funcionamiento del login

Este es el flujo de autenticación que ya ejecutamos en tu Samsung:

```text
┌─────────────────────────────────────┐
│ LoginScreen                         │
│ Introducir RUT y contraseña         │
└──────────────────┬──────────────────┘
                   ↓
┌─────────────────────────────────────┐
│ LoginViewModel                      │
│ Validar campos, controlar loading   │
└──────────────────┬──────────────────┘
                   ↓
┌─────────────────────────────────────┐
│ AuthRepository                      │
│ Ejecutar login() y obtener Result   │
└──────────────────┬──────────────────┘
                   ↓
┌─────────────────────────────────────┐
│ FakeAuthApiService                  │
│ Simular servicio y token de prueba  │
└──────────────────┬──────────────────┘
                   ↓
┌─────────────────────────────────────┐
│ MainActivity → HomeScreen           │
│ Mostrar datos de la sesión          │
└─────────────────────────────────────┘
```

### ¿Por qué tenemos `FakeAuthApiService` y `RetrofitClient`?

La selección actual en `LoginViewModel` sigue la idea:

```kotlin
private val repository: AuthRepository =
    AuthRepository(
        if (BuildConfig.DEBUG)
            FakeAuthApiService()
        else
            RetrofitClient.authApi
    )
```

Cuando ejecutamos una compilación **Debug**, se utiliza el servicio falso.

Eso nos permite probar formularios y navegación **sin depender de un backend disponible**.

La API falsa utiliza credenciales de prueba definidas en su propia implementación y devuelve un token simulado de aspecto JWT.

> **Advertencia de seguridad:** esto **no es autenticación real**. No comprueba credenciales contra un proveedor de identidad ni valida criptográficamente el usuario.

### ¿Qué es un JWT?

**JWT** (*JSON Web Token*) es un formato de token que suele presentarse como:

```text
HEADER.PAYLOAD.SIGNATURE
```

Nuestro `JwtDecoder.kt` separa el token y decodifica el **payload**.

`HomeViewModel` usa esos datos para construir un `SessionUser` y mostrar información como RUT, roles, identificador, emisor y fecha de expiración.

**Ojo:** decodificar el contenido de un JWT **no equivale a validar su firma**. No debemos confiar en claims de un token arbitrario sin verificación adecuada.

### ¿Quién decide si mostramos Login o Home?

En `MainActivity.kt` utilizamos:

```kotlin
var token: String? by rememberSaveable {
    mutableStateOf<String?>(null)
}
```

El control básico de navegación es:

```kotlin
if (currentToken == null) {
    LoginScreen(
        onLoginSuccess = { t -> token = t }
    )
} else {
    HomeScreen(
        accessToken = currentToken,
        onLogout = { token = null }
    )
}
```

- **Sin token:** se ve Login.
- **Con token:** se ve Home.
- **Al cerrar sesión:** el token vuelve a `null` y aparece Login.

`rememberSaveable` puede conservar ciertos datos durante recreaciones de la Activity, pero **no equivale a una sesión persistente** implementada con DataStore.

También hay un archivo `AppNavigation.kt` que define un `NavHost`, pero en el estado explicado aquí todavía no controla la navegación que estamos ejecutando; su integración en `MainActivity` está comentada.

---

## 10. Amazon Cognito, Retrofit y la API

Actualmente existe un módulo denominado `cognito-auth` con trabajo preparatorio relacionado con Cognito y AppAuth.

Sin embargo, en el estado revisado de `settings.gradle.kts`, solamente se incluye `:app`. Por tanto, **Cognito todavía no está integrado en el flujo activo de la aplicación**.

### ¿Qué hace Retrofit?

**Retrofit** facilita declarar y ejecutar solicitudes HTTP desde Android. En nuestro caso, `RetrofitClient` está preparado para construir un cliente con tecnologías como:

- Retrofit: llamadas HTTP a endpoints.
- OkHttp: cliente HTTP subyacente.
- Gson: conversión entre JSON y objetos de Kotlin/Java.

En Debug nuestro login usa `FakeAuthApiService`, por lo que **no pasa por una autenticación remota real**.

Antes de conectar un backend tendremos que revisar, entre otras cosas:

1. Configuración real del endpoint y autenticación.
2. Declaración del permiso de Internet si es necesario y aún no está presente.
3. Gestión segura de tokens y estados de sesión.
4. Errores de red y respuestas HTTP.

No debemos confundir *tener clases preparadas para Retrofit/Cognito* con *tener el sistema autenticando contra el backend*.

---

## 11. Estado actual del proyecto

### Lo que tenemos y lo que falta

| Componente | Estado al 23/10/2026 | Comentario |
| --- | --- | --- |
| 4 Entities | ✅ Hechas | `User`, `Parking`, `Service`, `Incident`. |
| 4 DAOs | ✅ Implementados | Consultas y operaciones Room. |
| `SabaDatabase.kt` | ✅ Configurada | Base local con las cuatro Entities. |
| `AuthRepository.kt` | ✅ Funcional para login de prueba | Trabaja con la API falsa en Debug. |
| `UserRepository.kt` | ✅ Agregado | Estaba vacío antes del parche. |
| `ParkingRepository.kt` | ✅ Agregado | No existía antes del parche. |
| `ServiceRepository.kt` | ✅ Agregado | No existía antes del parche. |
| `IncidentRepository.kt` | ✅ Agregado | Estaba vacío antes del parche. |
| `SabaRepositories.kt` | ✅ Agregado | Agrupa dependencias locales; pendiente de conexión. |
| Login → Home → Logout | ✅ Probado | Funciona en el Samsung con autenticación simulada. |
| Integración Room → ViewModel → UI | ⏳ Pendiente | Falta crear o adaptar ViewModels y pantallas. |
| Operaciones CRUD desde la app | ⏳ Pendiente de pruebas | Compilar Repositories no verifica aún los flujos completos. |
| Pantallas de incidencias/mantenimiento/guías | ⏳ Incompletas | Estaban prácticamente vacías en la versión revisada. |
| Navegación completa | ⏳ Pendiente | `AppNavigation.kt` no está activo en el flujo principal. |
| Autenticación real con Cognito | ⏳ Pendiente | No está integrada en el login activo. |
| Persistencia de sesión con DataStore | ⏳ Pendiente | `rememberSaveable` no la reemplaza. |

### ¿Qué hemos conseguido realmente?

Tenemos una base organizada con responsabilidades separadas y un flujo de login de prueba funcional. **Los archivos de datos y Repositories compilan**; lo siguiente es comprobar que esas capas trabajan juntas cuando el usuario guarda, consulta y modifica información desde una pantalla.

---

## 12. Nuestro próximo paso

Recomiendo comenzar con **ParkingViewModel** antes de avanzar hacia la autenticación real o una pantalla compleja de incidencias.

El motivo es sencillo: `Parking` es más fácil de probar y `Service` depende de estacionamientos existentes.

### Primer flujo completo que debemos construir

```text
ParkingScreen (Compose)
          ↓
ParkingViewModel (StateFlow)
          ↓
ParkingRepository
          ↓
ParkingDao
          ↓
SabaDatabase (Room / SQLite)
```

### Plan de prueba

1. Crear un `ParkingViewModel` que reciba `ParkingRepository`.
2. Obtener la lista de estacionamientos como estado observable.
3. Crear una pantalla Compose que muestre esa lista.
4. Añadir una acción para crear un estacionamiento de prueba.
5. Ejecutar Saba en el Samsung.
6. Guardar el estacionamiento, cerrar la app y volver a abrirla.
7. Comprobar que el registro sigue almacenado.
8. Repetir progresivamente el patrón con `Service` y `Incident`.

### Prioridad de trabajo

Nuestra entrega está prevista para el **martes 27 de octubre de 2026**. Por eso, lo más sensato es **conseguir primero un flujo mínimo completo y comprobable**, antes de agregar funcionalidades complejas que podrían quedar a medias.

---

## Resumen final

Las **Entities** definen las tablas; los **DAOs** indican qué consultas podemos hacer; `SabaDatabase` conecta esas piezas mediante Room; y los **Repositories** organizan el acceso a los datos y aplican las reglas de negocio.

Luego, los **ViewModels** convierten la información y los eventos en estados observables, mientras **Jetpack Compose** muestra esos estados en pantalla.

En paralelo, el **login simulado** ya funciona gracias a `LoginViewModel`, `AuthRepository` y `FakeAuthApiService`. La autenticación real con Cognito y la integración completa con las pantallas todavía están pendientes.

**En una frase:** ya construimos la infraestructura para administrar la información de Saba; el próximo paso es conectarla a la interfaz y demostrar que podemos guardar y recuperar datos reales en el teléfono.
