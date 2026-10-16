# Saba — documentación técnica del MVP

> Esta carpeta documenta **el proyecto Saba actual**, no un proyecto de ejemplo.
> Punto de partida real: aplicación Android en Jetpack Compose con un login local sencillo, selección de parking y navegación `login → home`.

## Estado actual del proyecto

Actualmente Saba ya tiene:

- `MainActivity.kt` levantando la aplicación con Compose.
- `navigation/AppNavigation.kt` con las rutas `login` y `home`.
- `ui/screens/LoginScreen.kt`.
- `ui/screens/HomeScreen.kt`.
- `ui/state/LoginUiState.kt`.
- `ui/viewmodel/LoginViewModel.kt`.
- Login con **RUT + contraseña + parking**.
- Parking inicial de demostración: **Parking Plaza Italia**.
- Validación local simple: si los campos requeridos están completos, se permite avanzar al Home.
- Navegación funcionando al Home.

Todavía **NO** se considera terminado:

- Login contra backend real.
- `StateFlow` como flujo definitivo de estado, si el proyecto aún conserva `mutableStateOf`.
- Persistencia de sesión.
- API REST de incidencias.
- CRUD local con Room para mantenimiento.
- Control de permisos por roles.
- Despliegue del backend en EC2.
- Cliente React.

## Objetivo académico del proyecto

Saba debe evolucionar a un sistema de continuidad operacional para estacionamientos. El MVP contempla:

1. Login y selección/contexto de parking.
2. Dashboard de estado operacional.
3. Incidencias con CRUD remoto consumido desde Android mediante Retrofit.
4. Mantenimiento con CRUD local mediante Room/SQLite.
5. Cuatro roles con vistas/permisos diferenciados.
6. Backend propio Spring Boot desplegado en EC2.
7. Base de datos relacional para el backend.
8. Una versión web React consumiendo la misma API.

Los cuatro roles definidos para el diseño son:

- `ADMIN`
- `PARKING_MANAGER`
- `OPERATOR`
- `TECH_SUPPORT`

Los servicios principales del dashboard son:

- Barreras de entrada.
- Lectura LPR.
- Terminales POS.
- Cajero automático #2.

Estados operacionales sugeridos para el MVP:

- `OPERATIVO`
- `DEGRADADO`
- `FUERA_DE_SERVICIO`

## Arquitectura objetivo

```text
Android Saba (Compose)
        │
        │ eventos
        ▼
ViewModel + UiState/StateFlow
        │
        ▼
Repository
   ┌────┴───────────┐
   │                │
   ▼                ▼
Room local       Retrofit
(Mantenimiento)     │
                    ▼
               Spring Boot API
                    │
                    ▼
                PostgreSQL

React web ───────► misma Spring Boot API
```

## Orden recomendado de los documentos

| Orden | Documento | Para qué sirve |
|---|---|---|
| 0 | `caso.md` | Contexto y alcance de Saba. |
| 1 | `01_login.md` | Evolucionar el login local existente a MVVM + StateFlow y dejarlo preparado para Retrofit. |
| 2 | `02_home.md` | Construir el dashboard operacional sin romper la navegación ya existente. |
| 3 | `03_mantenimiento_room.md` | Implementar el CRUD local obligatorio con Room. |
| 4 | `04_incidencias_api.md` | Implementar el CRUD remoto con Spring Boot + Retrofit. |
| 5 | `05_roles.md` | Incorporar los cuatro roles y restringir acciones/vistas. |
| 6 | `06_ec2_y_react.md` | Llevar la API a EC2 y conectar el cliente React. |
| 7 | `api.md` | Contrato REST objetivo del MVP. |
| 8 | `roadmap.md` | Orden de implementación completo y checklist del proyecto. |

## Regla de oro de esta carpeta

No reconstruir Saba desde cero cada vez que aparece una guía nueva.

El enfoque es incremental:

```text
lo que ya funciona
      ↓
refactor pequeño
      ↓
probar
      ↓
agregar una sola capa nueva
      ↓
probar otra vez
```

Si una etapa nueva rompe el proyecto, vuelve al último punto funcional antes de seguir.
