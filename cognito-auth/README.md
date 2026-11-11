# cognito-auth — Login con Amazon Cognito (módulo reutilizable)

> Esta guía no contiene credenciales reales: solo nombres de variables y placeholders
> entre `<...>`. Los valores los aporta cada estudiante desde su propio User Pool.

Módulo Android autocontenido para iniciar sesión con el **Hosted UI / Managed login**
de Amazon Cognito usando **Authorization Code + PKCE** (vía AppAuth-Android).

- Sin Hilt, sin Compose obligatorio, sin valores en duro.
- La app anfitriona solo aporta: config + 1 launcher + 1 botón + 1 `intent-filter`.
- Pensado para compartir entre proyectos y estudiantes: copia esta carpeta y sigue el paso a paso.

```
cognito-auth/
  README.md                     // esta guía (fuente de verdad, viaja con el módulo)
  build.gradle.kts
  src/main/AndroidManifest.xml
  src/main/java/com/cognitoauth/
    CognitoConfig.kt            // issuerUri, clientId, redirectUri, logoutUri, scopes
    CognitoAuthResult.kt        // Success(tokens) | Failure(msg) | Cancelled
    CognitoAuthManager.kt       // discovery + AuthorizationRequest + intents + token exchange
    CognitoAuthViewModel.kt     // StateFlow<CognitoUiState> listo para Compose o Views
```

---

## Cómo usar esta guía con un asistente IA

Si otro modelo (u otro compañero) ayuda con la integración, el flujo es:

1. El estudiante entrega al asistente estos **5 datos** (nadie más los tiene):
   `PACKAGE_NAME`, `COGNITO_ISSUER`, `COGNITO_CLIENT_ID`,
   `COGNITO_REDIRECT_URI`, `COGNITO_LOGOUT_URI`. (`COGNITO_SCOPES` default: `email openid phone`).
2. El asistente ejecuta los pasos 1–7 en orden, **sin saltarse verificaciones**:
   cada paso termina con ✅ verificable antes de seguir.
3. Ante un error, el asistente va directo a **Troubleshooting** con el mensaje literal.

---

## Paso 1 — Dependencia AppAuth

En el proyecto anfitrión, `gradle/libs.versions.toml`:

```toml
[versions]
appauth = "0.11.1"

[libraries]
appauth = { group = "net.openid", name = "appauth", version.ref = "appauth" }
```

Este módulo ya declara `implementation(libs.appauth)`. La app **no** repite AppAuth,
solo agrega el módulo.

## Paso 2 — Registrar el módulo

`settings.gradle.kts` (raíz del proyecto anfitrión):

```kotlin
include(":cognito-auth")
```

`app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":cognito-auth"))
}
```

Y el scheme del redirect (reemplaza con tu package):

```kotlin
defaultConfig {
    manifestPlaceholders["appAuthRedirectScheme"] = "com.tuempresa.tuapp"
}
```

## Paso 3 — Configuración sin hardcodear (`.env` → `BuildConfig`)

`.env.example` (sí se commitea):

```dotenv
COGNITO_ISSUER=https://cognito-idp.<region>.amazonaws.com/<poolId>
COGNITO_CLIENT_ID=TU_APP_CLIENT_ID
COGNITO_REDIRECT_URI=com.tuempresa.tuapp://cognito/callback
COGNITO_LOGOUT_URI=com.tuempresa.tuapp://cognito/callback
COGNITO_SCOPES=email openid phone
```

`.env` (no se commitea): mismas claves con valores reales.

`app/build.gradle.kts` — leer y exponer como `BuildConfig`:

```kotlin
val envFile = rootProject.file(".env")
val envProps = java.util.Properties()
if (envFile.exists()) { envFile.inputStream().use { envProps.load(it) } }
fun envOr(key: String, fallback: String): String =
    envProps.getProperty(key) ?: System.getenv(key) ?: fallback

defaultConfig {
    buildConfigField("String", "COGNITO_ISSUER", "\"${envOr("COGNITO_ISSUER", "")}\"")
    buildConfigField("String", "COGNITO_CLIENT_ID", "\"${envOr("COGNITO_CLIENT_ID", "")}\"")
    buildConfigField(
        "String", "COGNITO_REDIRECT_URI",
        "\"${envOr("COGNITO_REDIRECT_URI", "com.tuempresa.tuapp://cognito/callback")}\""
    )
    buildConfigField(
        "String", "COGNITO_LOGOUT_URI",
        "\"${envOr("COGNITO_LOGOUT_URI", envOr("COGNITO_REDIRECT_URI", "com.tuempresa.tuapp://cognito/callback"))}\""
    )
    buildConfigField("String", "COGNITO_SCOPES", "\"${envOr("COGNITO_SCOPES", "email openid phone")}\"")
}
```

Un único punto de acceso en código (ej: `core/config/CognitoAppConfig.kt`):

```kotlin
object CognitoAppConfig {
    val config = CognitoConfig(
        issuerUri = BuildConfig.COGNITO_ISSUER,
        clientId = BuildConfig.COGNITO_CLIENT_ID,
        redirectUri = BuildConfig.COGNITO_REDIRECT_URI,
        logoutUri = BuildConfig.COGNITO_LOGOUT_URI,
        scopes = BuildConfig.COGNITO_SCOPES,
    )
}
```

Regla: si ves el issuer o el client ID literales en un `.kt`, está mal. Todo pasa por `BuildConfig`.

## Paso 4 — Registrar el callback en `AndroidManifest.xml`

El `scheme/host/path` deben calzar con `COGNITO_REDIRECT_URI` parte por parte:

```xml
<activity
    android:name="net.openid.appauth.RedirectUriReceiverActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:scheme="com.tuempresa.tuapp"
            android:host="cognito"
            android:path="/callback" />
    </intent-filter>
</activity>
```

## Paso 5 — Consola de AWS (Managed login pages)

Aquí es donde más se traban todos. En la consola: **User Pool → App clients →
tu client → Configuración de páginas de inicio de sesión administradas → Editar**.

Déjalo así (conserva las URL que ya existan, agrega las tuyas en líneas nuevas):

| Campo | Variable | Ejemplo (ficticio) |
|---|---|---|
| URL de devolución de llamadas permitidas | `COGNITO_REDIRECT_URI` de tu `.env` (+ las que ya había) | `<tu-package>://cognito/callback` |
| URL de redireccionamiento predeterminada | = tu `COGNITO_REDIRECT_URI` | `<tu-package>://cognito/callback` |
| URL de cierre de sesión permitidas | = tu `COGNITO_LOGOUT_URI` | `<tu-package>://cognito/callback` |
| Proveedores de identidad | Directorio del grupo de usuarios de Cognito | — |
| Tipos de concesión de OAuth | Concesión de código de autorización | — |
| Ámbitos de OpenID Connect | `email`, `openid`, `phone` | — |

> Orden importa: primero agrega la URI a la **lista de permitidas** y guarda;
> recién después podrás elegirla como **predeterminada**. Si la pides antes,
> la consola responde: *"no está en la lista de URI de devolución de llamada"*.
> Los schemes personalizados (`tuapp://`) sí son válidos para apps nativas.

### Paso 5b — Branding asignado a TU client (causa real de "Login pages unavailable")

Aunque el dominio exista y las URLs estén bien, el navegador puede mostrar
*"Login pages unavailable / Please contact an administrator"*. Eso significa que
tu app client no tiene estilo de inicio de sesión administrado asignado.
Verifícalo en **Creación de marca → Inicio de sesión administrado → Estilos**:

- La columna **Cliente de aplicación asignado** debe nombrar a **tu** client.
- Caso típico: el único estilo existente está asignado a **otro** client del mismo
  User Pool (ej: un client web/SPA) y tu client móvil no tiene ninguno → la app
  llega al navegador pero Cognito responde *"Login pages unavailable"*.
  Se corrige con **Crear un estilo** eligiendo tu client (los valores por defecto bastan).
- Sin esto, el `Estado` de las páginas administradas sale *No disponible*.

> Si la pantalla muestra **Estado: No disponible**, el login puede fallar aunque
> todo lo demás esté bien: falta el dominio del Hosted UI. Se corrige en
> **Domain / Dominio** del User Pool creando un prefijo de dominio, y luego
> reintentando. Si el error persiste, revisa Logcat (`AuthorizationException`).

## Paso 6 — Pegamento mínimo en la app (5 líneas efectivas)

Con `ViewModel` + `ActivityResultLauncher` (patrón recomendado):

```kotlin
val manager = CognitoAuthManager(this, CognitoAppConfig.config)
val vm: CognitoAuthViewModel = viewModels<CognitoAuthViewModel>(
    factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CognitoAuthViewModel(manager) as T
    }
)[CognitoAuthViewModel::class.java]

val authLauncher = registerForActivityResult(StartActivityForResult()) { res ->
    vm.onAuthResponse(res.data)   // intercambia código -> tokens
}
val logoutLauncher = registerForActivityResult(StartActivityForResult()) {
    vm.onLogoutCompleted()
}

// Intents pendientes del módulo: se lanzan una vez y se consumen.
vm.uiState.value.pendingAuthIntent?.let { authLauncher.launch(it); vm.consumeAuthIntent() }
vm.uiState.value.pendingLogoutIntent?.let { logoutLauncher.launch(it); vm.consumeLogoutIntent() }

// Disparar:
Button(onClick = { vm.startLogin() }) { Text("Ingresar con Cognito") }
Button(onClick = { vm.startLogout() }) { Text("Cerrar sesión") }
```

En Compose puro el launcher vive en el Composable:

```kotlin
val launcher = rememberLauncherForActivityResult(StartActivityForResult()) { res ->
    cognitoVm.onAuthResponse(res.data)
}
LaunchedEffect(pendingAuthIntent) {
    pendingAuthIntent?.let { launcher.launch(it); cognitoVm.consumeAuthIntent() }
}
```

El éxito llega como `CognitoUiState.tokens` (`accessToken` para tu backend,
`idToken` para logout y para identificar al usuario).

## Paso 7 — Probar

1. `Build > Rebuild`, instala en dispositivo con navegador (Chrome Custom Tabs).
2. Tap "Ingresar con Cognito" → abre el Hosted UI → login → vuelve sola a la app.
3. Esperado: `tokens.accessToken != null`.
4. Casos:
   - atrás/cancelar en el browser → `Cancelled` (normal, no es error, no muestres Snackbar);
   - sin red → `Failure` + Snackbar genérico;
   - `redirect_mismatch` → falta la URL en consola (repite el Paso 5);
   - vuelve pero sin volver a la app (se queda en el browser) → `intent-filter` no calza (repite el Paso 4).
5. Verifica que ningún `.kt` tenga valores en duro:
   `rg -n "cognito-idp" app/src cognito-auth/src --glob '*.kt'`
   Esperado: solo patrones genéricos en comentarios, nunca tu pool ID.

---

## Reutilizar en otro proyecto (checklist para estudiantes)

1. Copia la carpeta `cognito-auth/` a la raíz del otro proyecto.
2. `settings.gradle.kts`: `include(":cognito-auth")`.
3. `app/build.gradle.kts`: `implementation(project(":cognito-auth"))` + `manifestPlaceholders` con tu scheme.
4. Crea `.env` + `.env.example` con las 5 claves del Paso 3 (issuer/clientId/redirect de **tu** User Pool).
5. Agrega el `intent-filter` del Paso 4 con tu `scheme/host/path`.
6. En consola AWS repite el Paso 5 con tus URIs.
7. Pega el bloque del Paso 6 y un botón. Listo.

El módulo no referencia ningún package de app anfitriona: es agnóstico.

## Troubleshooting

| Síntoma | Causa probable | Fix |
|---|---|---|
| Consola: URI "no está en la lista" | Se pidió como predeterminada antes de agregarla | Paso 5: agregar a permitidas → guardar → elegirla |
| `redirect_mismatch` en el browser | URI no registrada o con typo | Paso 5: compara carácter por carácter |
| No vuelve a la app | `intent-filter` no calza con `redirectUri` | Paso 4: scheme/host/path parte por parte |
| Falla discovery / `Estado: No disponible` | Sin dominio de Hosted UI o issuer mal copiado | Domain → crear prefijo; revisar `.env` |
| Navegador muestra "Login pages unavailable" | El app client no tiene estilo de inicio de sesión administrado asignado (el estilo existe pero apunta a otro client) | Paso 5b: **Creación de marca → Inicio de sesión administrado → Estilos** → **Crear un estilo** eligiendo tu client |
| Vuelve pero `Cancelled` | Usuario cerró el browser | Normal: no mostrar error |
| Token null tras success | Falta scope `openid` | Agrega `openid` en `.env` y en consola |
