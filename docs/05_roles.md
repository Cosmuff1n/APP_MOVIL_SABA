# 05 — Roles y navegación por permisos

> Objetivo: integrar los cuatro roles del proyecto sin duplicar cuatro aplicaciones distintas.

## Roles

```text
ADMIN
PARKING_MANAGER
OPERATOR
TECH_SUPPORT
```

## 1. Qué NO hacer

No copies toda la pantalla Home cuatro veces:

```text
AdminHomeScreen
ManagerHomeScreen
OperatorHomeScreen
TechHomeScreen
```

si el 80% del contenido es el mismo.

Es mejor compartir componentes y controlar qué acciones aparecen según permisos.

---

## 2. Modelo de sesión

Ejemplo:

```kotlin
enum class UserRole {
    ADMIN,
    PARKING_MANAGER,
    OPERATOR,
    TECH_SUPPORT
}

data class SessionUser(
    val id: Long,
    val rut: String,
    val name: String,
    val role: UserRole
)
```

El rol debe provenir del login/backend cuando esa etapa exista.

---

## 3. Matriz mínima de permisos

Propuesta para el MVP:

| Acción | ADMIN | PARKING_MANAGER | OPERATOR | TECH_SUPPORT |
|---|:---:|:---:|:---:|:---:|
| Ver dashboard | ✅ | ✅ | ✅ | ✅ |
| Crear incidencia | ✅ | ✅ | ✅ | ✅ |
| Cambiar estado incidencia | ✅ | ✅ | ❌ | ✅ |
| Ver mantenimiento | ✅ | ✅ | ✅ | ✅ |
| Editar mantenimiento | ✅ | ✅ | ❌ | ✅ |
| Gestión de usuarios | ✅ | ❌ | ❌ | ❌ |

La matriz puede ajustarse a la pauta/docente. Lo importante es que exista una regla única y no condiciones contradictorias repartidas por la UI.

---

## 4. UI condicional

Ejemplo:

```kotlin
if (sessionUser.role == UserRole.ADMIN) {
    Button(onClick = onUsersClick) {
        Text("Usuarios")
    }
}
```

Para múltiples permisos, conviene encapsular reglas:

```kotlin
fun UserRole.canManageUsers(): Boolean =
    this == UserRole.ADMIN
```

---

## 5. Seguridad real: backend

Ocultar el botón sirve para UX, pero no protege la API.

Ejemplo de problema:

```text
OPERATOR no ve botón "Eliminar"
pero llama DELETE manualmente con Postman
```

Si el backend no verifica permisos, la restricción Android no vale nada.

Por eso:

```text
UI decide qué mostrar
Backend decide qué autorizar
```

---

## 6. Navegación

El `NavHost` puede mantener rutas compartidas:

```text
login
home
incidents
maintenance
guides
users
```

Antes de navegar a una ruta administrativa, la UI valida permiso.

El backend vuelve a validar cuando se hace una operación sensible.

---

## 7. Cuatro vistas exigidas

Si la evaluación habla de "cuatro vistas por rol", no significa necesariamente cuatro copias completas del dashboard.

Puedes demostrar diferencias claras mediante:

- acciones disponibles;
- accesos visibles;
- contenido administrativo;
- formularios editables;
- capacidad de cambiar estados.

Documenta capturas de cada rol para que la diferencia sea visible en la presentación.

---

## Checklist 05

- [ ] Existen cuatro roles.
- [ ] El usuario autenticado tiene un rol.
- [ ] La app puede decidir permisos a partir del rol.
- [ ] Las pantallas comunes se reutilizan.
- [ ] Las acciones administrativas no aparecen a roles sin permiso.
- [ ] El backend también verifica permisos antes de operaciones sensibles.
