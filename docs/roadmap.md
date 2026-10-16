# Roadmap de implementación — Saba

> Meta: terminar el MVP sin saltar entre cinco tecnologías a la vez.

## Estado 0 — Lo que ya está

```text
[✓] Proyecto Android Compose
[✓] Login simple
[✓] RUT
[✓] Contraseña
[✓] Parking Plaza Italia
[✓] LoginUiState
[✓] LoginViewModel
[✓] LoginScreen
[✓] HomeScreen
[✓] AppNavigation
[✓] login → home
```

---

## Fase 1 — Estabilizar Android base

### 1.1 Login con StateFlow

Documento: `01_login.md`

```text
[ ] MutableStateFlow privado
[ ] StateFlow público
[ ] collectAsState
[ ] errores por campo
[ ] isLoading
[ ] isSuccess
[ ] navegación sigue funcionando
```

**Criterio para avanzar:** el login local sigue entrando a Home.

### 1.2 Home operacional

Documento: `02_home.md`

```text
[ ] Parking visible
[ ] Barreras
[ ] LPR
[ ] POS
[ ] Cajero #2
[ ] estados operacionales
[ ] componentes reutilizables
```

**Criterio para avanzar:** Home muestra datos fake de forma consistente.

---

## Fase 2 — CRUD local obligatorio

Documento: `03_mantenimiento_room.md`

```text
[ ] Room
[ ] Entity
[ ] DAO
[ ] Database
[ ] Repository
[ ] ViewModel
[ ] Create
[ ] Read
[ ] Update
[ ] Delete
[ ] persistencia tras reinicio
```

**Criterio para avanzar:** puedes demostrar CRUD sin internet.

---

## Fase 3 — Backend/API

### 3.1 Spring Boot + PostgreSQL

```text
[ ] proyecto backend
[ ] conexión BD
[ ] Incident entity
[ ] Repository
[ ] Service
[ ] Controller
```

### 3.2 CRUD Incidents

Documento: `04_incidencias_api.md`

```text
[ ] GET all
[ ] GET id
[ ] POST
[ ] PUT
[ ] DELETE si pauta lo exige
```

**Criterio para avanzar:** todo funciona con Postman/curl.

---

## Fase 4 — Android + Retrofit

Volver a `01_login.md` y `04_incidencias_api.md`.

```text
[ ] .env / BuildConfig
[ ] RetrofitClient
[ ] AuthApiService
[ ] AuthRepository
[ ] login real
[ ] IncidentApiService
[ ] IncidentRepository
[ ] lista remota
[ ] create/update/delete remoto
```

**Criterio para avanzar:** Android usa Spring Boot y no datos falsos para Incidents.

---

## Fase 5 — Roles

Documento: `05_roles.md`

```text
[ ] ADMIN
[ ] PARKING_MANAGER
[ ] OPERATOR
[ ] TECH_SUPPORT
[ ] sesión conoce rol
[ ] UI adapta acciones
[ ] backend protege operaciones
```

---

## Fase 6 — EC2

Documento: `06_ec2_y_react.md`

```text
[ ] JAR ejecuta en servidor
[ ] variables de entorno
[ ] puerto/red configurados
[ ] Android apunta a API desplegada
```

---

## Fase 7 — React

```text
[ ] proyecto React
[ ] VITE_API_URL
[ ] login
[ ] dashboard básico
[ ] incidents list
[ ] create/update según alcance
```

---

# Orden para cuando algo falla

## Problema de Android/Compose

```text
UI
↓
UiState
↓
ViewModel
```

## Problema de API

```text
BD
↓
Spring Boot
↓
Postman/curl
↓
Retrofit
↓
Repository Android
↓
ViewModel
↓
Compose
```

No empieces depurando un `TextField` si Postman ya demuestra que el backend devuelve 500.

---

# Prioridad para aprobar

Prioridad alta:

```text
Login funcional
Home funcional
CRUD local Room
CRUD remoto API
4 roles/vistas
API EC2
React conectado
```

Prioridad secundaria, solo cuando lo anterior esté estable:

```text
animaciones
microinteracciones
arquitecturas excesivamente complejas
features no pedidas
refactor cosmético grande
```

El proyecto debe ser entendible y demostrable antes que “perfecto”.
