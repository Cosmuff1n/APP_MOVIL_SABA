# Contexto del proyecto — Saba

> Documento de contexto técnico y funcional para el MVP académico de Saba.
> No reemplaza una pauta oficial del ramo; resume las decisiones y alcance de **este proyecto** para mantener consistencia entre Android, backend y React.

## 1. Qué problema resuelve Saba

Saba se plantea como una aplicación de apoyo a la **continuidad operacional de estacionamientos**.

La aplicación permite que personal operativo y técnico pueda consultar rápidamente el estado de los servicios de un parking, registrar incidencias, consultar guías de soporte y mantener un registro de tareas de mantenimiento.

El foco del MVP no es reproducir todos los sistemas reales de una empresa de estacionamientos. El foco es demostrar correctamente las tecnologías y patrones exigidos en el proyecto académico.

---

## 2. Flujo principal del MVP

```text
Login
  ↓
selección/contexto de parking
  ↓
Home / Dashboard operacional
  ├── Barreras de entrada
  ├── Lectura LPR
  ├── Terminales POS
  └── Cajero automático #2
        ↓
     detalle
        ├── ver estado
        ├── consultar guía
        ├── reportar incidencia
        └── revisar mantenimiento
```

El parking utilizado actualmente en la interfaz de demostración es:

```text
Parking Plaza Italia
```

---

## 3. Usuarios y roles

El diseño considera cuatro roles:

| Rol | Responsabilidad en el MVP |
|---|---|
| `ADMIN` | Gestión amplia del sistema y usuarios. |
| `PARKING_MANAGER` | Supervisión del parking, servicios e incidencias. |
| `OPERATOR` | Consulta operacional y registro de incidencias. |
| `TECH_SUPPORT` | Atención técnica, seguimiento de incidencias y mantenimiento. |

La seguridad real debe aplicarse también en el backend. Ocultar un botón en Android **no es autorización suficiente**.

---

## 4. Módulos del sistema

### 4.1 Login

Datos visibles actualmente:

- RUT.
- Contraseña.
- Parking seleccionado.

Estado actual: **login local/simulado** para permitir avanzar con el proyecto.

Evolución objetivo:

```text
LoginScreen
    ↓
LoginViewModel
    ↓
AuthRepository
    ↓
Retrofit
    ↓
Spring Boot
```

### 4.2 Dashboard / Home

Debe mostrar el estado de servicios principales del parking actual:

- Barreras de entrada.
- Lectura LPR.
- Terminales POS.
- Cajero automático #2.

Estados simples del MVP:

- Operativo.
- Degradado.
- Fuera de servicio.

### 4.3 Incidencias

Será el **CRUD remoto** del proyecto.

Operaciones:

- Crear incidencia.
- Listar incidencias.
- Ver una incidencia.
- Editar incidencia.
- Eliminar incidencia, si la pauta exige DELETE real.

Arquitectura:

```text
Android / React
      ↓
Spring Boot REST
      ↓
PostgreSQL
```

### 4.4 Mantenimiento

Será el **CRUD local** del proyecto utilizando Room.

Operaciones:

- Crear tarea de mantenimiento.
- Listar tareas.
- Editar.
- Eliminar.

Arquitectura:

```text
Compose
   ↓
ViewModel
   ↓
MaintenanceRepository
   ↓
MaintenanceDao
   ↓
Room / SQLite
```

### 4.5 Guías / Soporte

Módulo de consulta para orientar al operador ante fallas frecuentes.

Para el MVP puede comenzar con contenido ficticio/local. No debe bloquear el avance de los CRUD obligatorios.

### 4.6 Usuarios / Administración

Módulo asociado principalmente a `ADMIN`.

No es necesario convertirlo en el centro del MVP mientras login, CRUD remoto, CRUD local y roles no estén terminados.

---

## 5. Entidades conceptuales

El diseño completo puede considerar:

```text
User
Parking
Service
Incident
Guide
Maintenance
```

Relaciones simplificadas:

```text
Parking 1 ─── N Service
Parking 1 ─── N Incident
Service 1 ─── N Incident
Parking 1 ─── N Maintenance
User    1 ─── N Incident
```

No todas las entidades tienen que transformarse inmediatamente en tablas locales de Room.

**Regla del MVP:**

- `Incident` vive principalmente en backend/API.
- `Maintenance` se utiliza para demostrar persistencia local con Room.

---

## 6. Stack objetivo

### Android

- Kotlin.
- Android Studio.
- Jetpack Compose.
- Material 3.
- MVVM.
- StateFlow.
- Navigation Compose.
- Retrofit + Gson.
- Room / SQLite.
- Corrutinas.

### Backend

- Java.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- Base de datos PostgreSQL.
- API REST.
- Despliegue en EC2 para la entrega final que lo requiera.

### Web

- React.
- Consumo de la misma API REST.

---

## 7. Lo que está hecho y lo que todavía no

### Ya existe

- Proyecto Android Compose.
- Login visual.
- `LoginUiState`.
- `LoginViewModel`.
- `LoginScreen`.
- `HomeScreen`.
- `AppNavigation`.
- Flujo `login → home`.
- Campos RUT, contraseña y parking.

### Pendiente

- Migración definitiva del estado del login a `StateFlow`, si aún no se hizo.
- Login contra API real.
- Sesión/token.
- Modelos operacionales del Home.
- CRUD Room de mantenimiento.
- CRUD REST de incidencias.
- Roles reales.
- Backend Spring Boot.
- PostgreSQL.
- EC2.
- React.

---

## 8. Principios para no perder el control del proyecto

1. **No borrar funcionalidades que ya sirven** solo para copiar una guía externa.
2. Primero dejar una capa funcionando antes de agregar la siguiente.
3. No hacer Retrofit directo desde un `Button`.
4. No hacer consultas Room directamente desde un Composable.
5. No guardar contraseñas en Room.
6. No poner credenciales de base de datos PostgreSQL/Supabase dentro de Android ni React.
7. Android y React hablan con Spring Boot; Spring Boot habla con la base de datos.
8. Los datos de pruebas deben ser ficticios.
9. Una vista distinta por rol no significa duplicar toda la app; significa habilitar acciones y destinos según permisos.
10. Para aprobar el ramo, priorizar primero los requisitos verificables del MVP antes de agregar extras.
