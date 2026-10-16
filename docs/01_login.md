# 01 — Evolucionar el login actual de Saba (Compose + MVVM + StateFlow + Retrofit)

> Punto de partida real: Saba **ya tiene un login simple funcionando** con `RUT`, `contraseña`, selección de parking y navegación a Home.
> Objetivo: mejorar la arquitectura sin destruir lo que ya funciona.

## 0. A dónde queremos llegar

### Hoy

```text
LoginScreen
    ↓
LoginViewModel
    ↓
validación local sencilla
    ↓
onLoginSuccess()
    ↓
Home
```

### Objetivo

```text
LoginScreen
    ↓ eventos
LoginViewModel + LoginUiState/StateFlow
    ↓
AuthRepository
    ↓
Retrofit / AuthApiService
    ↓
Spring Boot
    ↓
resultado
    ↓
LoginViewModel
    ↓ isSuccess
LoginScreen
    ↓
AppNavigation
    ↓
Home
```

La migración debe hacerse en dos etapas:

1. **StateFlow manteniendo el login falso.**
2. **Retrofit reemplazando el login falso.**

Si haces ambas cosas a la vez y falla, no sabrás qué capa rompió el proyecto.

---

## Paso 1 — Mantén la estructura actual

No reemplaces `MainActivity` por un `LoginScreen()` directo. Saba ya tiene navegación.

Estructura esperada:

```text
com.example.appsaba/
  MainActivity.kt
  navigation/
    AppNavigation.kt
  ui/
    screens/
      LoginScreen.kt
      HomeScreen.kt
    state/
      LoginUiState.kt
    viewmodel/
      LoginViewModel.kt
```

Más adelante agregaremos:

```text
  core/config/
    AppConfig.kt
  data/remote/
    AuthDtos.kt
    AuthApiService.kt
    RetrofitClient.kt
  data/repository/
    AuthRepository.kt
```

---

## Paso 2 — `LoginUiState`: conservar los datos de Saba

No copies `username` de otro proyecto. En Saba la UI usa RUT.

Archivo: `ui/state/LoginUiState.kt`

```kotlin
package com.example.appsaba.ui.state

data class LoginUiState(
    val rut: String = "",
    val password: String = "",
    val selectedParking: String = "Parking Plaza Italia",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val rutError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isSuccess: Boolean = false
) {
    val canSubmit: Boolean =
        rut.isNotBlank() && password.isNotBlank() && !isLoading
}
```

### Por qué `selectedParking` sigue aquí

Porque forma parte del contexto actual de la pantalla.

Esto **no significa** que tenga que enviarse al endpoint de login. Esa decisión depende del contrato de la API.

---

## Paso 3 — Migrar el ViewModel a StateFlow

Archivo: `ui/viewmodel/LoginViewModel.kt`

Primero hacemos la versión **sin Retrofit**.

```kotlin
package com.example.appsaba.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appsaba.ui.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onRutChange(value: String) {
        _uiState.update {
            it.copy(
                rut = value,
                rutError = null,
                loginError = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordError = null,
                loginError = null
            )
        }
    }

    fun onParkingChange(value: String) {
        _uiState.update {
            it.copy(selectedParking = value)
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun onLoginClick() {
        val current = _uiState.value

        val rutError =
            if (current.rut.isBlank()) "Ingresa tu RUT" else null

        val passwordError =
            if (current.password.isBlank()) "Ingresa tu contraseña" else null

        if (rutError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    rutError = rutError,
                    passwordError = passwordError
                )
            }
            return
        }

        // ETAPA TEMPORAL:
        // conserva el comportamiento local que ya tenía Saba.
        _uiState.update {
            it.copy(isSuccess = true)
        }
    }
}
```

### Qué ganamos aunque todavía sea un login falso

El flujo ya queda correctamente organizado:

```text
UI -> evento -> ViewModel -> StateFlow -> UI
```

Y la siguiente etapa cambia **solo la implementación interna de `onLoginClick()`**, no toda la pantalla.

---

## Paso 4 — LoginScreen observa StateFlow

En `LoginScreen.kt`:

```kotlin
val state by viewModel.uiState.collectAsState()
```

Los campos deben usar el estado:

```kotlin
OutlinedTextField(
    value = state.rut,
    onValueChange = viewModel::onRutChange,
    label = { Text("RUT") }
)
```

Contraseña:

```kotlin
OutlinedTextField(
    value = state.password,
    onValueChange = viewModel::onPasswordChange,
    label = { Text("Contraseña") }
)
```

La selección de parking sigue llamando:

```kotlin
viewModel.onParkingChange(...)
```

---

## Paso 5 — Navegar por `isSuccess`

La navegación no debe ejecutarse dentro del ViewModel.

En `LoginScreen`:

```kotlin
LaunchedEffect(state.isSuccess) {
    if (state.isSuccess) {
        onLoginSuccess()
    }
}
```

Luego `AppNavigation` sigue siendo responsable de:

```text
login → home
```

Ejemplo conceptual:

```kotlin
LoginScreen(
    onLoginSuccess = {
        navController.navigate("home") {
            popUpTo("login") { inclusive = true }
        }
    }
)
```

### Por qué así

`LoginViewModel` sabe que la autenticación terminó correctamente.

`AppNavigation` sabe qué pantalla debe abrir.

Cada pieza tiene una responsabilidad distinta.

---

## Paso 6 — Prueba ANTES de agregar Retrofit

Prueba:

1. Abrir la app.
2. Dejar RUT vacío → debe mostrar error.
3. Dejar contraseña vacía → debe mostrar error.
4. Completar ambos con datos ficticios.
5. Seleccionar `Parking Plaza Italia`.
6. Pulsar ingresar.
7. Debe navegar a Home exactamente como antes.
8. Rotar el emulador y comprobar que el estado del ViewModel no se reinicia durante un cambio de configuración normal.

Si esto no funciona, **no agregues Retrofit todavía**.

---

# Parte B — Reemplazar el login falso por login real

## Paso 7 — Dependencias

Agrega únicamente las dependencias que no tengas ya.

`gradle/libs.versions.toml`:

```toml
[versions]
retrofit = "3.0.0"
okhttpLogging = "5.5.0"
gsonConverter = "3.0.0"
coroutines = "1.11.0"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "gsonConverter" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttpLogging" }
coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
```

`app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
```

No borres Navigation Compose ni las dependencias que Saba ya utiliza.

---

## Paso 8 — `.env` para la URL

Raíz del proyecto:

```text
Saba/
  .env
  .env.example
  app/
  gradle/
```

`.env` local:

```dotenv
API_BASE_URL=http://10.0.2.2:8080/
```

`.env.example`:

```dotenv
API_BASE_URL=http://10.0.2.2:8080/
```

`.gitignore`:

```gitignore
.env
```

Cuando la API esté en EC2, solo cambia el valor del entorno.

---

## Paso 9 — `BuildConfig` + `AppConfig`

En `app/build.gradle.kts`, habilita BuildConfig si no está habilitado:

```kotlin
android {
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
```

Lee la variable antes de `android {}`:

```kotlin
import java.util.Properties

val envFile = rootProject.file(".env")
val envProps = Properties()

if (envFile.exists()) {
    envFile.inputStream().use { envProps.load(it) }
}

val apiBaseUrl =
    envProps.getProperty("API_BASE_URL")
        ?: System.getenv("API_BASE_URL")
        ?: "http://10.0.2.2:8080/"
```

Dentro de `defaultConfig`:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
```

Nuevo archivo: `core/config/AppConfig.kt`

```kotlin
package com.example.appsaba.core.config

import com.example.appsaba.BuildConfig

object AppConfig {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL.trim().let {
        if (it.endsWith("/")) it else "$it/"
    }
}
```

---

## Paso 10 — permiso de Internet

`AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Paso 11 — DTOs

Nuevo archivo: `data/remote/AuthDtos.kt`

```kotlin
package com.example.appsaba.data.remote

data class LoginRequest(
    val rut: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val user: UserDto
)

data class UserDto(
    val id: Long,
    val rut: String,
    val name: String,
    val role: String
)
```

> Estos nombres corresponden al contrato objetivo documentado en `api.md`. Si Spring Boot termina exponiendo nombres distintos, se modifican los DTOs para que ambos lados coincidan.

---

## Paso 12 — AuthApiService

`data/remote/AuthApiService.kt`:

```kotlin
package com.example.appsaba.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}
```

---

## Paso 13 — RetrofitClient

`data/remote/RetrofitClient.kt`:

```kotlin
package com.example.appsaba.data.remote

import com.example.appsaba.BuildConfig
import com.example.appsaba.core.config.AppConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val authApi: AuthApiService by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BASIC
            else
                HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(AppConfig.apiBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}
```

No uses `BODY` para autenticación si eso implica imprimir contraseñas en Logcat.

---

## Paso 14 — Repository

`data/repository/AuthRepository.kt`:

```kotlin
package com.example.appsaba.data.repository

import com.example.appsaba.data.remote.AuthApiService
import com.example.appsaba.data.remote.LoginRequest
import com.example.appsaba.data.remote.LoginResponse

class AuthRepository(
    private val api: AuthApiService
) {
    suspend fun login(
        rut: String,
        password: String
    ): Result<LoginResponse> = runCatching {
        api.login(
            LoginRequest(
                rut = rut.trim(),
                password = password
            )
        )
    }
}
```

---

## Paso 15 — Reemplazar SOLO el interior del login

Ahora `LoginViewModel` recibe el Repository:

```kotlin
class LoginViewModel(
    private val repository: AuthRepository =
        AuthRepository(RetrofitClient.authApi)
) : ViewModel() {
```

Luego `onLoginClick()` mantiene la validación y cambia la parte final:

```kotlin
_uiState.update {
    it.copy(isLoading = true, loginError = null)
}

viewModelScope.launch {
    repository.login(current.rut, current.password)
        .onSuccess { response ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true
                )
            }

            // El manejo del token/sesión se documentará aparte.
        }
        .onFailure {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    loginError = "No se pudo iniciar sesión. Revisa tus datos o la conexión."
                )
            }
        }
}
```

---

## Checklist 01

- [ ] El login antiguo no fue borrado antes de migrar.
- [ ] `rut`, `password` y `selectedParking` siguen representando la UI de Saba.
- [ ] `LoginUiState` es inmutable.
- [ ] `MutableStateFlow` es privado.
- [ ] La UI observa `StateFlow`.
- [ ] El ViewModel no navega directamente.
- [ ] `AppNavigation` conserva `login → home`.
- [ ] Primero se probó StateFlow con login local.
- [ ] Luego se agregó Retrofit.
- [ ] La URL no está repetida por archivos Kotlin.
- [ ] El Manifest tiene permiso de Internet.
- [ ] La contraseña no se imprime en logs.
- [ ] No se asumió que `selectedParking` forma parte del request de login.

## Troubleshooting rápido

### `Unresolved reference: retrofit2`

Faltan dependencias Retrofit en `app/build.gradle.kts` o no se sincronizó Gradle.

### `CLEARTEXT communication not permitted`

Si pruebas una API local con `http://`, Android puede bloquear tráfico no HTTPS según configuración. Para desarrollo local se puede habilitar cleartext de forma controlada; para despliegue usa HTTPS.

### El emulador no llega a `localhost:8080`

Dentro del emulador, `localhost` es el propio emulador. Para llegar al PC usa:

```text
http://10.0.2.2:8080/
```

### Navega dos veces a Home

Asegúrate de que el efecto dependa de `state.isSuccess` y que la ruta `login` se elimine con `popUpTo(...){ inclusive = true }` cuando corresponda.
