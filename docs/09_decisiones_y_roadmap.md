# 09 — Decisiones pendientes y plan de trabajo

## Decisiones funcionales (por confirmar)

| ID | Pregunta | Impacto |
|---|---|---|
| D-01 | ¿Qué roles existen y quién valida el cierre? | Permisos, roles Cognito, pantallas |
| D-02 | ¿El usuario elige parking solo al entrar o puede cambiarlo en sesión? | Navegación y fuente de datos |
| D-03 | ¿Cuál es la fuente de estados operacionales: sensores, backend, manual? | Arquitectura y semáforo |
| D-04 | ¿Qué sucede al escalar al SAU? ¿Hay API disponible? | Ticketing y conectividad |
| D-05 | ¿Cómo diferenciar estado del ticket y del equipo? | Room/entidades y flujo |
| D-06 | ¿Hay requisito de funcionamiento sin Internet? | Room y sincronización |
| D-07 | ¿Qué datos de usuario puede almacenar la app y cuánto tiempo? | Privacidad y seguridad |
| D-08 | ¿Cuáles son las validaciones de foto/video? | Almacenamiento y UX |
| D-09 | ¿Mantenimiento se incluye en el MVP o solo vista informativa? | Alcance académico |
| D-10 | ¿Cuál es la API real de autenticación y es Cognito directo o backend mediador? | Diseño de AuthRepository |

## Recomendación de prioridades para entrega académica

**No reimplementar toda Figma Make.** Seleccionar un flujo que pueda demostrarse sin backend real, y separar demos de integraciones.

| Orden | Entrega verificable | Dependencias |
|---|---|---|
| 0 | Respaldar baseline funcional + dejar docs en repo | Login que ya funciona |
| 1 | Documentar pantalla, navegación y flujo ticket | Este paquete |
| 2 | Room Entity + DAO + Database + Repository, compilar | Gradle/KSP |
| 3 | Incidencias: UI de crear y observar datos locales | Room y ViewModel |
| 4 | Abrir detalles / resolver y conservar historial local | Incidencias |
| 5 | Home resumido que lea servicios/tickets de fuente coherente | Modelo de datos |
| 6 | Cognito sin Amplify, **solo si hay configuración válida y tiempo** | Credenciales demo, integración AWS; no romper login funcional |
| 7 | Guías / perfiles / mantenimiento según alcance y tiempo | Priorización acordada |

**Fechas declaradas por el usuario:** entrega app 27-10-2026 y presentación/defensa 03-11-2026. Por proximidad, conviene proteger el baseline antes de integración riesgosa de Cognito.

## Plantilla de decisión

```markdown
### D-XX — Título
- Contexto:
- Opciones:
- Decisión:
- Consecuencias:
- Estado: pendiente | aprobada | descartada
- Fecha:
```

## Rutina de documentación

1. Antes de programar: actualizar ruta y campos de la pantalla.
2. Durante: añadir cambios de contrato (Entity / DTO / Repository).
3. Después: registrar prueba real y evidencia.
4. En cada revisión: marcar las propuestas que el docente aprobó; mantener historial de cambios en Git.
