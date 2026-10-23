# 08 — Plan de pruebas y evidencia

## Registro mínimo

Por cada prueba, anotar: ID, fecha, dispositivo/AVD, versión de código o commit, pasos, esperado, real, resultado y, si falla, primera línea útil de Logcat/Build Output.

| ID | Flujo | Resultado esperado | Estado actual |
|---|---|---|---|
| PR-01 | Login demo válido Android | Login lleva a Home | Confirmado por usuario |
| PR-02 | Cerrar sesión Android | Vuelve a Login | Confirmado por usuario |
| PR-03 | Room: sync/compilación tras Etapa 2 | Gradle compila sin error KSP | Pendiente de verificación explícita |
| PR-04 | Crear incidencia | Se guarda registro local y reaparece tras cerrar/reabrir app | Pendiente |
| PR-05 | Consultar incidencias | Lista y contador corresponden a base local | Pendiente |
| PR-06 | Resolver ticket | Se conserva quién, fecha y descripción tras reiniciar | Pendiente |
| PR-07 | Selección parking | Ver solo los servicios/parkings asignados | Pendiente |
| PR-08 | Cognito login inválido | Error visible, sin entrar al Home | Pendiente |
| PR-09 | Roles | Usuario sin permiso no puede gestionar otros | Pendiente |
| PR-10 | Sin conexión | Mostrar error o modo local según decisión técnica | Pendiente |
| PR-11 | Navegar por las 4 pestañas | Sin crash, estado coherente, regresar | Pendiente de versión Android |

## Ejemplo de caso de prueba completo

**PR-04: crear incidencia persistente**

- **Precondición:** usuario demo ingresó; Room conectado a una pantalla real.
- **Pasos:** abrir Incidencias → Reportar → elegir cajero → ingresar descripción → Guardar → cerrar aplicación → volver a abrir → consultar listado.
- **Esperado:** mismo incidente con identificador, descripción, fecha y estado.
- **Prueba mínima:** screenshot o log de resultado antes y después de reiniciar app.
- **Estado:** No ejecutada. No marcarla correcta por haber ejecutado solo la simulación web.

## Evidencia sugerida para defensa

Guardar capturas **sin credenciales ni datos personales reales** en carpeta `docs/evidencias/` (no subir logs completos ni tokens). Registrar cambios por commit, por ejemplo: `docs: documentar flujo de incidencias` y `feat(room): guardar incidencia local`.
