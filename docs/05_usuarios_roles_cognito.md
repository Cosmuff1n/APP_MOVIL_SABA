# 05 — Usuarios, roles y Amazon Cognito

## Qué existe en Figma [FIGMA-IMPLEMENTADO]

- Login con RUT, contraseña y selector de parking, pero **sin validación de credenciales** (el handler llama `onLogin` tras una espera).
- `UsersPage` presenta cuatro usuarios **ficticios/estáticos**, con roles y estado activo/ausente.
- `ProfileMenu` da acceso a «Equipo y usuarios» y «Estacionamientos».
- Hay mensajes visuales sobre permisos RBAC; **el prototipo no aplica esos permisos**.

## Responsabilidades propuestas

| Componente | Función prevista | Qué NO debería hacer |
|---|---|---|
| Amazon Cognito User Pool | Autenticar credenciales, emitir tokens y gestionar usuarios de identidad | Mantener tickets operacionales por sí solo |
| Backend/API autorizado | Verificar tokens y aplicar permisos por recurso (usuario, parking, ticket) | Confiar solo en el rol mostrado por la UI |
| Room local | Guardar una caché limitada de perfiles/asignaciones y datos operativos permitidos | Guardar contraseñas o asumir que es fuente global de usuarios |
| Android / Compose | Mostrar perfil, validar formularios y ocultar acciones no disponibles | Ser autoridad única de seguridad |

## Identidad en Cognito y uso de RUT [PENDIENTE]

En Saba el campo visible se llama **RUT**, no `username`. Antes de integrar Cognito hay que decidir si RUT será el nombre de usuario en el User Pool o si se mapeará de forma segura a un identificador interno. No asumir que basta con renombrar la propiedad de un DTO.

Posible modelo local **no implementado**:

| Campo | Propósito |
|---|---|
| `idCognito` | Identificador estable (por ejemplo `sub` del token) |
| `rutNormalizado` | RUT para visualización/búsqueda, si es necesario |
| `nombreMostrado` | Nombre del perfil |
| `rol` | Rol visible; el backend debe verificar permisos |
| `parkingId` | Asignación o selección local por sitio |

**Seguridad:** no guardar contraseñas ni tokens de acceso/renovación en Room. No habilitar métodos de Cognito que requieran un secreto de cliente embebido en una app móvil. La verificación de permisos debe ocurrir en la API real, aunque Compose también oculte opciones.

## Matriz inicial de permisos [PROPUESTA POR VALIDAR]

| Acción | Operador | Jefatura | Soporte TI | Supervisión |
|---|---|---|---|---|
| Ver estado de parking asignado | Sí | Sí | Sí | Sí |
| Reportar incidente | Sí | Sí | Según política | Según política |
| Documentar intervención | No definido | Según política | Sí | No definido |
| Validar cierre | No definido | Sí | No definido | No definido |
| Gestionar usuarios | No definido | No definido | No definido | No definido |
| Ver varios parkings | Según asignación | Según asignación | Según asignación | Sí |

Las autorizaciones son un **borrador**, no una afirmación sobre los roles reales de Saba.
