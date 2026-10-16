# 03 — Login con Cognito (índice del proyecto)

> La guía completa vive en el módulo reutilizable: [`../cognito-auth/README.md`](../cognito-auth/README.md).
> Ese README viaja con el módulo para compartirlo entre proyectos y estudiantes.
> Aquí solo quedan los valores y el wiring propios de **SyncPass**.

## Valores de este proyecto (origen: consola AWS, viven en `.env`)

| Clave | Valor |
|---|---|
| `COGNITO_ISSUER` | `https://cognito-idp.us-east-2.amazonaws.com/us-east-2_ZhTULhf5B` |
| `COGNITO_CLIENT_ID` | `7jlu3v32pqfn5nimh84m9ga64o` |
| `COGNITO_REDIRECT_URI` | `com.rojas.syncpass://cognito/callback` |
| `COGNITO_LOGOUT_URI` | `com.rojas.syncpass://cognito/callback` |
| `COGNITO_SCOPES` | `email openid phone` |

## Wiring en SyncPass (ya aplicado, ver `cognito-auth/README.md` pasos 2–4 y 6)

- `settings.gradle.kts`: `include(":cognito-auth")`.
- `app/build.gradle.kts`: `implementation(project(":cognito-auth"))`, 5 `buildConfigField
  COGNITO_*` desde `.env`, `manifestPlaceholders["appAuthRedirectScheme"] = "com.rojas.syncpass"`.
- `core/config/CognitoAppConfig.kt`: único punto de acceso vía `BuildConfig`.
- `app/src/main/AndroidManifest.xml`: `RedirectUriReceiverActivity` (`scheme=com.rojas.syncpass,
  host=cognito, path=/callback`) + `MainActivity` con `launchMode="singleTask"`.
- `MainActivity.kt`: manager + `CognitoAuthViewModel` + launchers de login/logout.
- `ui/login/LoginScreen.kt`: pantalla Cognito-only (botón "Continuar con Cognito" en `AuthCard`).
  El formulario clásico (`LoginViewModel`/`LoginUiState`) se eliminó; la capa de red
  (`RetrofitClient`, `AuthRepository`, DTOs) se conserva para las llamadas autenticadas al backend.
- `ui/login/CognitoViewModelFactory.kt`: factory sin Hilt.

## Estado de consola AWS (verificado funcionando)

Managed login pages del client `7jlu…`: llamadas permitidas =
`com.rojas.syncpass://cognito/callback` + `https://d84l1y8p4kdic.cloudfront.net`;
predeterminada y cierre de sesión = `com.rojas.syncpass://cognito/callback`;
code grant + `email openid phone`. Dominio `https://cloud.auth.us-east-2.amazoncognito.com`
ya existía; la falla real era el estilo de branding asignado a otro client
(`react-spa-client`): se creó un estilo para el client Android y el login abrió.
Detalle del porqué en `cognito-auth/README.md` pasos 5 y 5b.
