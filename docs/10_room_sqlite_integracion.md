# 10 — Integración completa de Room + SQLite (MVP local)

## Qué se agregó

Partiendo de la versión que ya compilaba y conservando `SabaDatabase` en versión **1**, se agregó:

- `ui/data/SabaDataViewModel.kt`: coordina los 4 Repositories, observa `Flow` mediante `StateFlow`, usa `viewModelScope`, valida entradas y muestra errores sin bloquear la UI.
- `ui/data/SabaDataScreen.kt`: sección accesible desde **Home → Administrar datos locales (Room)**.
- `ui/data/ParkingsSection.kt`: crear, listar, editar, activar/desactivar y eliminar parkings.
- `ui/data/ServicesSection.kt`: crear, listar, editar, cambiar estado operativo y eliminar equipos asociados al parking seleccionado.
- `ui/data/UsersSection.kt`: crear, listar, editar, activar/desactivar y eliminar perfiles locales; opcionalmente asociar al parking seleccionado.
- `ui/data/IncidentsSection.kt`: crear tickets únicos, listarlos por parking, resolverlos, eliminarlos y mostrar el contador de abiertos.
- `ui/data/DataWidgets.kt`: selector de parking y confirmación para borrados.
- `MainActivity.kt` y `HomeScreen.kt`: botón y navegación simple para acceder a estas pantallas sin modificar el login existente.
- `app/src/androidTest/.../SabaRoomIntegrationTest.kt`: pruebas instrumentadas de operaciones de Room, relaciones y persistencia tras cerrar y volver a abrir una base de prueba.

No se cambió ninguna **Entity, DAO, Repository ni `SabaDatabase`**, por lo que no se necesita migración de esquema al aplicar este parche.

## Prueba manual de extremo a extremo (en Samsung)

1. Copia el parche haciendo respaldo previo. **No borres los datos de la aplicación ni desinstales Saba.**
2. Compila e instala con el teléfono físico seleccionado. No abras el emulador.
3. Usa el login de prueba actual. Ve a **Administrar datos locales (Room)**.
4. **Estacionamientos**: crea `Parking demo`, dirección `Av. Ejemplo 100`, 100 plazas, 80 disponibles. Debe aparecer abajo sin refrescar manualmente.
5. **Equipos**: selecciona `Parking demo`, crea `Barrera principal`, tipo `barriers`. Prueba cambiar su estado a `degradado`.
6. **Usuarios**: crea `Operador demo`, asignándolo opcionalmente a `Parking demo`. No hace falta para reportar (el nombre del reportero se puede escribir libremente).
7. **Incidencias**: selecciona `Barrera principal`, describe `No abre` con estado `fuera-servicio`. Presiona **Registrar ticket local**; debe aparecer con código `SABA-...` y sumarse a los abiertos.
8. Presiona **Resolver**, indica persona y trabajo realizado y guarda. Debe mostrarse **Resuelto** y bajar el contador de abiertos. La salud del equipo se cambia por separado desde la pestaña **Equipos**.
9. **Cierra la app completamente** y ábrela de nuevo, repitiendo el login de prueba. Revisa que estacionamientos, equipos, usuarios y tickets sigan apareciendo.
10. Prueba **Editar** los datos y borrar un registro sin relaciones. Si intentas borrar un parking que aún tiene equipos vinculados, SQLite puede rechazarlo por integridad referencial (comportamiento esperado).

## Prueba instrumental automatizada (opcional)

En Android Studio abre `SabaRoomIntegrationTest.kt` y ejecuta las pruebas en el Samsung. Usan bases de datos independientes de la DB de Saba y verifican operaciones y persistencia.

## Cómo circulan los datos

```text
SabaDataScreen (Compose)
    | clic, texto
    v
SabaDataViewModel (StateFlow, viewModelScope)
    | operaciones suspend
    v
SabaRepositories (Parking/Service/Incident/User)
    |
    v
DAO (Room, @Insert / @Update / @Delete / @Query)
    |
    v
saba_database (SQLite en almacenamiento privado de la app)
    |
    +---- Flow -> StateFlow -> collectAsState() -> interfaz recompuesta
```

## Limitaciones importantes

- **Solo almacenamiento local en este dispositivo**: no hay sincronización con servidor, API de soporte ni Cognito integrados al flujo activo.
- Los perfiles de `User` **no autentican** ni almacenan contraseñas. El login existente sigue separado.
- No se adjuntan fotos/videos al ticket en este MVP. `evidenceUri` queda preparado en la Entity para una siguiente etapa.
- Resolver un ticket **no marca automáticamente el equipo como operativo**, para evitar cerrar una falla cuando hay otras abiertas.
- Se usan tickets UUID de 8 caracteres (muy improbables colisiones, pero no garantizados globalmente). La unicidad también está protegida por el índice SQLite.
- Guardar una incidencia y cambiar un equipo de estado serían dos operaciones distintas si se automatizara; para garantizar atomicidad habría que implementar una transacción Room.
- No implementamos permisos por rol ni auditoría de modificaciones. Esto es un MVP para la entrega, no un sistema listo para producción.
- La versión de esquema sigue en **1**. Si modificas Entities en el futuro, necesitarás plan de migración en Room. Evita `fallbackToDestructiveMigration()` con datos que quieres conservar.
- La aplicación necesita compilarse y ejecutarse en tu Android Studio/Samsung para confirmar que funciona. Un ZIP de fuentes por sí solo no demuestra compilación ni pruebas exitosas.

## Rendimiento en un PC limitado

Mantén cerrado el emulador y utiliza el teléfono físico. Deja `org.gradle.workers.max=2` como está. No aumentes a ciegas `org.gradle.jvmargs` ni detengas procesos Java mientras compila.
