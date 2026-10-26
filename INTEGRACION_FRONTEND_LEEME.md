# Saba — Integración del frontend del grupo con el proyecto principal

## Origen y prioridad

- **Base:** `WIPBEARGAMETWO(5).zip` — proyecto principal de Charlie, sus módulos, Room/SQLite, Cognito y flujos probados.
- **UI de origen:** `APP_MOVIL_SABA_FRONTEND_WIP.zip` — frontend grupal.
- **Login:** se conserva **íntegramente el del proyecto de Charlie** (`ui/login/LoginScreen.kt`, `LoginViewModel.kt`, `LoginUiState.kt`) con Cognito, login de prueba y logout real.
- **No se incorporó el login grupal antiguo** ni su `MainActivity`, `LoginViewModel`, `AuthRepository`, entidades o configuración Gradle.

## Cambios realizados

1. Se adaptó la navegación inferior del equipo (Inicio / Incidencias / Guías / Mantenimiento) mediante `ui/navigation/SabaMainScaffold.kt`.
2. Se integró la pantalla `GuidesScreen.kt` del equipo al paquete `ui/guides`, conservando sus tarjetas expandibles.
3. **Inicio:** mantiene el `HomeScreen` funcional del proyecto principal, que decodifica el ID Token y ofrece acceso a datos locales y logout Cognito.
4. **Incidencias:** abre la pestaña Incidencias de `SabaDataScreen` (`initialTab=2`), con ViewModel y persistencia Room reales.
5. **Mantenimiento:** abre la pestaña Equipos de `SabaDataScreen` (`initialTab=1`), en lugar de la pantalla temporal del equipo. Es una integración provisional, NO un módulo completo de órdenes de mantenimiento.
6. **Datos locales:** mantiene las cuatro pestañas de Room al entrar desde Inicio (`initialTab=0`).
7. Se adecuó `MainActivity` para envolver Home en `SabaMainScaffold`, preservando el login y la ejecución del logout real (en caso de sesión Cognito).
8. Se ajustó el parámetro `initialTab` de `ui/data/SabaDataScreen.kt`.
9. Se marcaron las guías técnicas como **borradores a validar** y se quitaron del panel de soporte los datos de contacto ficticios, para no lanzar llamadas a números de prueba.
10. Se actualizó `.env.example` para que su callback y logout coincidan con el esquema de la aplicación principal.

## Lo que NO se ha integrado ni inventado

- Los placeholders de Inicio, Incidencias, Mantenimiento y Detalle del primer ZIP (no aportaban una interfaz real adicional).
- El login viejo de los compañeros.
- Backend EC2 ni sincronización con servidor. Room sigue siendo almacenamiento LOCAL.
- Funcionalidad real de gestión de mantenimiento independiente del estado de los equipos.
- Validación de los procedimientos reales de seguridad en terreno o datos de contacto oficiales.

## Antes de compilar

1. En el computador local, copia `.env.example` a `.env` y reemplaza los valores por **tu configuración existente de Cognito**. Si ya tienes un `.env` que funciona, conserva ese archivo y no lo subas al repositorio.
2. Asegura que están instalados JDK y Android SDK adecuados a tu proyecto. El ZIP **no contiene el archivo `gradle-wrapper.jar`**, porque faltaba tanto en los ZIP originales como en las carpetas extraídas; si necesitas el wrapper por terminal, recupéralo desde un wrapper oficial de Gradle. No descargues `.jar` desconocidos.
3. Sincroniza Gradle desde Android Studio. Compila con **Build → Make Project** y ejecuta en Samsung si la sincronización tiene éxito.
4. Prueba primero login Cognito → Inicio → Guías → Incidencias → Mantenimiento → Datos locales → logout Cognito → login nuevo.
5. Verifica que el `applicationId` siga siendo `com.example.appsaba` y que Cognito acepte `com.example.appsaba://cognito/callback` y `.../logout`.

## Pruebas y límites

Esta integración fue hecha por revisión estática de Kotlin. **No pudo ejecutarse la compilación de Android ni un test en el Samsung desde este entorno.** Si aparece un error en Android Studio, compartir el primer error de Build Output para depurarlo.

## GitHub Desktop — rama segura

1. Conserva una copia del proyecto original.
2. Clona el repositorio del grupo, actualiza `main` y crea una rama nueva `feature/integracion-frontend`.
3. Copia estos archivos sobre esa **rama**, excluyendo `.git`, `.env`, `local.properties`, `build`, `.gradle`.
4. Revisa los cambios, commit, Publish branch. No fusiones con `main` hasta probar en el teléfono.

## Archivos añadidos o modificados

- **Añadidos:** `app/src/main/java/com/example/appsaba/ui/navigation/SabaMainScaffold.kt`
- **Implementados:** `app/src/main/java/com/example/appsaba/ui/guides/GuidesScreen.kt`
- **Modificados:** `app/src/main/java/com/example/appsaba/MainActivity.kt`, `app/src/main/java/com/example/appsaba/ui/data/SabaDataScreen.kt`, `.env.example`
- **Resto:** código de la base original conservado.
