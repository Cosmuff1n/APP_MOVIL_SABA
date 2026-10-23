# Saba — Documentación viva

**Versión documental:** 0.3 · **Fecha:** 23 de octubre de 2026  
**Proyecto:** Saba — aplicación Android para continuidad operacional en estacionamientos  
**Tecnologías objetivo:** Kotlin, Jetpack Compose, MVVM, Room/SQLite, Cognito (sin Amplify)

> Este paquete documenta el prototipo de **Figma Make** y propone la estructura de la aplicación Android. **No modifica el proyecto** ni demuestra que las funciones de Figma ya estén programadas en Android.

## Comienza por aquí

1. [01 — Alcance, fuentes y estado real](01_alcance_y_estado.md)
2. [02 — Mapa de navegación](02_mapa_navegacion.md) — contiene Mermaid
3. [03 — Fichas de pantallas y acciones](03_pantallas_y_acciones.md)
4. [04 — Flujo de incidencias](04_flujo_incidencias.md) — contiene Mermaid
5. [05 — Usuarios, roles y Cognito](05_usuarios_roles_cognito.md)
6. [06 — Modelo de datos y Room](06_modelo_datos_room.md) — contiene Mermaid
7. [07 — Arquitectura Android y límites](07_arquitectura_android.md)
8. [08 — Pruebas y criterios de aceptación](08_pruebas_aceptacion.md)
9. [09 — Decisiones pendientes y orden de trabajo](09_decisiones_y_roadmap.md)

## Convención de evidencia

- **[FIGMA-IMPLEMENTADO]** La interacción está programada en el prototipo web de Figma Make. NO equivale a implementada en Android ni garantiza backend.
- **[ANDROID-CONFIRMADO]** El usuario comprobó que esta acción funciona en su aplicación Android.
- **[ANDROID-PREPARADO]** Se entregó un parche/configuración; no se ha comprobado aquí la compilación o instalación del conjunto final.
- **[PROPUESTA]** Diseño recomendado que **no** está confirmado por Figma ni por los requisitos originales.
- **[PENDIENTE]** Especificación o integración no realizada o no validada.

No cambiar una etiqueta a `ANDROID-CONFIRMADO` sin registrar la prueba en `08_pruebas_aceptacion.md`.

## Fuentes de referencia

- **Figma Make — prototipo de Saba:** https://www.figma.com/make/2sIynCi1e7J62AysloIzsW/prototipo-de-saba
- **Código fuente consultado:** `src/App.tsx` y `src/index.css` obtenidos desde Figma Make, el 23-10-2026. Las referencias `App.tsx:XXX` aluden a esta versión, no a Kotlin.
- **Presentación de caso asociada a Figma:** `MVP_App_M_vil_Saba_-_Prototipos_y_Requerimientos.pptx` (septiembre 2026, uso educativo).
- **Informe previo:** `Informe_Requerimientos_Estacionamientos.docx`, levantamiento preliminar del 29-09-2026; requerimientos/prioridad aún por validar.
- **Croquis:** diagrama manuscrito compartido por el usuario, octubre 2026.
- **Proyecto Android de trabajo:** WIPBEARGAMETWO; el login simulado y la transición al Home fueron probados por el usuario. No se consideró probada ninguna funcionalidad no informada explícitamente.

## Cómo mantener al día la documentación

Cuando hagas un cambio funcional: actualiza la ficha de pantalla → el flujo Mermaid → el modelo de datos (si cambió) → el resultado de prueba. Añade el cambio al mismo commit de Git. Evita escribir que se integra con SAU/Cognito o que se actualiza 'en tiempo real' hasta comprobar la conexión real.
