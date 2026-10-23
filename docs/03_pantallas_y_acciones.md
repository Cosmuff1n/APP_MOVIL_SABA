# 03 — Fichas de pantallas y acciones

Todas las conductas bajo 'Figma' se refieren al prototipo React + TypeScript, no a Android. Anclas de referencia: funciones en `src/App.tsx` de Figma Make.

## P-01 Login (`Login`, ~312)

- **Propósito:** acceder a la app.
- **Entradas visibles:** RUT, contraseña, estacionamiento (desplegable).
- **Figma:** `submit` hace `preventDefault`, activa loading, y llama `onLogin` a los 450 ms sin validar credenciales. El parking seleccionado no se propaga.
- **Android confirmado:** ingreso de prueba mediante credenciales ficticias y navegación a Home.
- **Pendiente:** autenticación Cognito sin Amplify, validación de usuario/parking y permisos, manejo de errores y sesiones.

## P-02 Home (`Dashboard`, ~412)

- **Propósito:** visualizar el estado de servicios y advertencias del parking.
- **Contenido:** resumen de operatividad, lista de barreras, LPR, terminales POS y cajero #2; botón crítico.
- **Acciones:** tocar servicio → detalle; reportar → formulario.
- **Figma:** computa estados a partir del arreglo local de incidencias. Muestra textos/fecha/clima de demostración.
- **Pendiente:** fuente real de estados, estacionamiento seleccionado, frecuencia de actualización.

## P-03 Detalle del servicio (`ServiceDetail`, ~561)

- **Contenido:** nombre, parking, estado, hora y problema.
- **Acciones cuando existe falla:** desplegar procedimiento, abrir formulario precargado, 'Contactar soporte'.
- **Figma:** 'Contactar soporte' muestra el aviso local «Solicitud enviada», pero no envía solicitudes de red.
- **Pendiente:** asociar cada incidencia a identificador de servicio/parking, soporte real, permisos de cambio de estado.

## P-04 Incidencias (`IncidentsPage`, ~1476)

- **Contenido:** contador de incidentes sin resolver, listado/historial de tickets.
- **Acciones:** abrir formulario, tocar ticket para ver detalle y resolución.
- **Figma:** los tickets existen solamente en `useState` mientras vive el prototipo.
- **Pendiente:** persistir y filtrar por parking/usuario/roles.

## P-05 Formulario de incidencia (`IncidentForm`, ~740)

- **Campos:** equipo, tipo de falla, gravedad `degradado/fuera-servicio`, observaciones, foto/video opcional.
- **Autogenerados (Figma):** código `SABA-` con los últimos dígitos de `Date.now()`, reportero fijo, parking fijo, fecha/hora.
- **Validación visible:** observación obligatoria; archivo debe ser imagen o video; descripción máximo 240 caracteres.
- **Acción:** publicar → inserta en arreglo local y cierra modal; **NO transmite al equipo por una API**.
- **Pendiente:** identificador robusto de ticket, fecha fiable, límites de archivos, sincronización/SAU, estado de publicación real y gestión de fallos.

## P-06 Resolución de ticket (`ResolutionForm`, ~1004)

- **Contenido:** ticket, descripción, reportero y estado; si está cerrado, muestra la resolución.
- **Campos al resolver:** persona que intervino, descripción del trabajo (máx. 300), fecha/hora.
- **Figma:** marcar solucionado fija `resolved=true` y `status=operativo` en el arreglo local.
- **Pendiente:** validación de resolución por jefatura (requisito del informe), bitácora auditable y evitar que un ticket cierre cualquier equipo sin aprobación.

## P-07 Guías (`GuidesPage`, ~1369)

- **Contenido:** cuatro guías de demostración (cajeros, barreras, POS, LPR), desplegables con instrucciones.
- **Soporte:** contacto SAU de muestra y botón con esquema `tel:`; **no significa que el número de ejemplo esté verificado**.
- **Pendiente:** aprobación operativa de procedimientos y contactos; posible búsqueda y actualización de guías mediante API.

## P-08 Mantenimiento (`MaintenancePage`, ~1585)

- **Contenido:** estado de los mismos cuatro equipos + próxima revisión preventiva fija.
- **Acciones:** tocar equipo → detalle del servicio.
- **Pendiente:** formularios de pauta, checklist, firma, evidencia y reportes. **No existen en el prototipo web**, aunque la presentación del caso los muestra como expectativa de alcance posterior.

## P-09 Estacionamientos (`ParkingPage`, ~1158)

- **Contenido:** cuatro parkings demo, plazas libres/ocupadas, buscador y filtros `todos/con plazas/completos`.
- **Entrada:** menú de perfil.
- **Figma:** filtrado local de la lista estática; no selecciona realmente un parking para cambiar Home.
- **Pendiente:** selección de sitio, permisos por parking, integración de ocupación y origen de datos.

## P-10 Equipo y usuarios (`UsersPage`, ~1262)

- **Contenido:** cuatro perfiles estáticos, rol y disponibilidad activo/ausente.
- **Entrada:** menú de perfil.
- **Figma:** botón «Gestionar» aparece sin callback, y el texto de acceso por roles no se traduce en comprobación real de permisos.
- **Pendiente:** fuente de perfiles, roles reales, asignaciones y operaciones administrativas autorizadas.

## P-11 Perfil y menú (`ProfileMenu`, ~1654)

- **Acciones:** abrir usuarios, abrir estacionamientos y cerrar sesión.
- **Figma:** el cierre del demo pone `loggedIn=false`.
- **Pendiente:** sesión real, invalidación y comprobación de permisos en rutas administrativas.

## Regla para nuevas fichas

Cada pantalla nueva debe indicar **ID, finalidad, actor, acceso, datos de entrada, datos de salida, estados de carga/error/vacío, acciones, validaciones, navegación, pruebas y estado de implementación**.
