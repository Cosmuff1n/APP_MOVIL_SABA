# 01 — Alcance, objetivos y estado actual

## Objetivo

Centralizar información del estado de servicios tecnológicos de estacionamientos y ayudar a **detectar, reportar, seguir y resolver incidencias**; ofrecer guías de primera respuesta y contactos de escalamiento.

## Alcance del caso y priorización

Del informe previo (levantamiento académico, **no aceptación contractual**):

| Módulo | Prioridad del informe | Presencia en Figma Make |
|---|---|---|
| Estado operacional de servicios | MVP | Home y detalle de servicio |
| Gestión de incidencias | MVP | Listado, formulario y resolución |
| Manuales/procedimientos | MVP | Cuatro guías desplegables |
| Contactos / escalamiento | MVP | Bloque de soporte con llamada |
| Alertas operacionales | Posterior | Ícono de notificación sin gestión real |
| Indicadores y reportes | Posterior | Contadores simples, sin analítica |
| Mantenimiento preventivo | Posterior | Vista informativa de equipos y próxima revisión |

**Exclusiones:** esta app no reemplaza sistemas de cobro, boletas ni el control físico de barreras. Tampoco hay pruebas de conexión efectiva al SAU, sensores o backend.

## Estado de trabajo al 23-10-2026

| Función | Estado | Evidencia / matiz |
|---|---|---|
| Login RUT + contraseña simulado en Android | ANDROID-CONFIRMADO | Usuario confirmó flujo de login funcional |
| Navegación Login → Home → Logout | ANDROID-CONFIRMADO | Usuario confirmó que sigue funcionando |
| Room/KSP: configuración | ANDROID-PREPARADO | Se entregó parche; el usuario reportó funcionamiento del login tras incorporarlo; no se audita aquí su proyecto actual |
| Room: Entity/DAO/Database/Repository incidencias | ANDROID-PREPARADO | Se entregó ZIP de etapa 2, **no consta instalación o compilación posterior** |
| Home visual Figma en Android | PENDIENTE DE VERIFICAR | Hubo diferentes parches, no asumir cuál está aplicado |
| Cognito sin Amplify | PENDIENTE | No autenticación real confirmada |
| Incidencias CRUD persistentes Android | PENDIENTE | Figma los gestiona en memoria solamente |
| Usuarios, parkings, guías y mantenimiento Android | PENDIENTE | Diseño existente, funcionalidad Android por verificar |
| Integración SAU / equipos reales / varios dispositivos | PENDIENTE | No implementado en Figma; API/backend aún por definir |

## Principios

1. **Primero navegación y requisitos; después persistencia.**
2. **Datos ficticios** para el MVP y pruebas; no cargar credenciales, RUT de personas reales o archivos sensibles en capturas o repositorios públicos.
3. **Una fuente de verdad por concepto:** Cognito autentica; Room guarda datos locales; para compartir datos entre celulares hará falta una API/backend autorizado.
4. La documentación usa roles genéricos, incluso si el prototipo contiene nombres propios de demostración.
