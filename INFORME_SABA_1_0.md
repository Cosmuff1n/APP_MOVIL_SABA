## Informe preliminar de análisis y levantamiento de requerimientos ##

- [Resumen ejecutivo](#resumen-ejecutivo)
- [1. Contexto, problema y objetivos](#1-contexto-problema-y-objetivos)
- [2. MVP: alcance y flujo principal](#2-mvp-alcance-y-flujo-principal)
- [3. Módulos funcionales identificados](#3-módulos-funcionales-identificados)
- [4. Actores y perfiles preliminares](#4-actores-y-perfiles-preliminares)
- [5. Historias de usuario y criterios de aceptación](#5-historias-de-usuario-y-criterios-de-aceptación)
- [6. Requerimientos funcionales](#6-requerimientos-funcionales)
- [7. Requerimientos no funcionales propuestos](#7-requerimientos-no-funcionales-propuestos)
- [8. Tecnologías y herramientas](#8-tecnologías-y-herramientas)
- [9. Arquitectura y Estructura del Proyecto](#9-arquitectura-y-estructura-del-proyecto)
- [10. Funcionalidades y pantallas](#10-funcionalidades-y-pantallas)
- [11. Persistencia de datos](#11-persistencia-de-datos)
- [12. Autenticación y seguridad](#12-autenticación-y-seguridad)
- [13.Integraciones y servicios](#13integraciones-y-servicios)
- [14.Pruebas y verificación](#14pruebas-y-verificación)


# Resumen ejecutivo

El proyecto plantea una aplicación móvil que reúna información operacional actualmente dispersa entre sistemas, correos y personas. Su propósito es facilitar la detección y el reporte y consulta de incidentes de carácter técnico de los estacionamientos, y contactos de escalamiento.
Se proponen nueve módulos potenciales.

| Elemento                                 | Resultado preliminar                                                       |
| ---------------------------------------- | -------------------------------------------------------------------------- |
| Módulos propuestos                       | 9                                                                          |
| Historias de usuario                     | 14: siete derivadas de ejemplos referenciales y siete complementarias      |
| Requerimientos funcionales               | 28                                                                         |
| Requerimientos no funcionales propuestos | 21                                                                         |
| Alcance de MVP recomendado               | 4 módulos centrales; 8 historias de usuario (HU-01 a HU-06, HU-08 y HU-09) |

# 1. Contexto, problema y objetivos

## 1.1 Problema de negocio

La información sobre el estado de múltiples aspectos genera mucha confusión para los encargados de la empresa. 
Los servicios críticos, incidentes, procedimientos, canales de soporte y actividades ejecutadas están repartidos entre diferentes canales y responsables.
Esto dificulta la coordinación, el seguimiento y la trazabilidad de estos.

## 1.2 Objetivo general

Desarrollar una aplicación móvil que centralice información operacional y apoye la detección, reporte, seguimiento y resolución de incidentes tecnológicos en estacionamientos.

## 1.3 Objetivos específicos

- Agregar un sistema de acceso de encargados seguro.
- Mejorar los tiempos de respuesta ante incidentes.
- Aumentar la visibilidad de servicios críticos y estacionamientos afectados.
- Facilitar el acceso a procedimientos de primera atención y contactos de soporte.
- Reducir la dependencia del conocimiento individual.
- Mejorar la trazabilidad y conservar evidencia de intervenciones.

## 1.4 Contexto tecnológico y límites

La empresa opera una varieda de equipos:
- Barreras
- Cajeros
- Dispositivos de entrada/salida
- Lectura de patentes
- TAG
- Cámaras
- Servidores de boleta electrónica
- Sistemas POS y enlaces.  

Estos sistemas constituyen el entorno supervisado; mencionarlos no implica que la nueva aplicación deba reimplementar el cobro, la emisión de boletas o el control directo de barreras.

# 2. MVP: alcance y flujo principal

Definición propuesta: permitir a un trabajador identificar una afectación, consultar las primeras acciones, reportarla con evidencia, encontrar el canal adecuado de soporte, documentar su seguimiento y verificar la resolución. La empresa indicó que la continuidad operacional debe ser el foco principal del MVP.

1\. Acceder (o registrar primero) a la cuenta de empleado conectada a internet.

2\. Consultar el estado operacional e identificar el servicio afectado.

3\. Consultar el procedimiento de diagnóstico inicial.

4\. Registrar y reportar el incidente con evidencia.

5\. Identificar el contacto y canal de escalamiento apropiados.

6\. Registrar las actividades de atención y su evidencia.

7\. Validar la resolución y actualizar el estado del incidente.

# 3. Módulos funcionales identificados

| N.º | Módulo                       | Función central                                       | Prioridad sugerida |
| --- | ---------------------------- | ----------------------------------------------------- | ------------------ |
| 1   | Estado operacional           | Consultar estado por parking y servicio crítico.      | MVP                |
| 2   | Gestión de incidentes        | Registrar, seguir, documentar y cerrar incidentes.    | MVP                |
| 3   | Manuales y procedimientos    | Consultar guías para diagnóstico y primera actuación. | MVP                |
| 4   | Directorio y guía de soporte | Encontrar responsables y rutas de escalamiento.       | MVP                |
| 5   | Alertas operacionales        | Recibir avisos ante afectaciones críticas.            | Posterior          |
| 6   | Reportes e indicadores       | Consultar disponibilidad y métricas operacionales.    | Posterior          |
| 7   | Mantenimiento preventivo     | Registrar intervenciones, cumplimiento y evidencias.  | Posterior          |
| 8   | Login y Registro             | Acceso a la cuenta ya sea local (Room) o Api (Cognito)| MVP                |
| 9   | Selector de Usuario          | Seleccionar usuarios ya logeados en esquina superior  | Posterior          |

La asignación a MVP o fase posterior es una propuesta de priorización; los nueve módulos aparecen en la presentación, pero su entrega simultánea no fue confirmada.

# 4. Actores y perfiles preliminares

| Actor                        | Responsabilidad esperada                                                                       |
| ---------------------------- | ---------------------------------------------------------------------------------------------- |
| Operador de parking          | Detecta y reporta incidentes; consulta procedimientos.                                         |
| Jefe o subjefe de parking    | Supervisa el parking y coordina contingencias.                                                 |
| Supervisor o coordinador     | Consulta estados de distintos estacionamientos.                                                |
| Especialista TI              | Atiende incidentes y documenta acciones.                                                       |
| Responsable de mantenimiento | Registra actividades preventivas y evidencias.                                                 |
| Gerencia                     | Consulta indicadores e información consolidada.                                                |
| Proveedor externo            | Participa por medio de los canales de soporte definidos; su acceso a la app queda por validar. |

No todos los actores requieren necesariamente una cuenta en el MVP; un proveedor externo podría figurar solo como contacto.

# 5. Historias de usuario y criterios de aceptación

Formato: Como \[rol\], quiero \[funcionalidad\], para \[beneficio\]. HU-01 a HU-07 se basan en ejemplos referenciales de la presentación, normalizados para documentación. HU-08 a HU-12 son complementarias. Los criterios de aceptación son propuestas para validación.

### HU-01 · Estado operacional \[MVP\]

Como jefe de parking, quiero consultar el estado de los servicios tecnológicos críticos de mi estacionamiento, para identificar rápidamente afectaciones.

- Criterio propuesto: Ver servicios asociados al estacionamiento.
- Criterio propuesto: Distinguir estados disponible, degradado y fuera de servicio.

### HU-02 · Estado operacional \[MVP\]

Como supervisor, quiero visualizar los estacionamientos con problemas operacionales, para priorizar la coordinación de contingencias.

- Criterio propuesto: Consultar la lista de estacionamientos.
- Criterio propuesto: Identificar y filtrar aquellos afectados.

### HU-03 · Incidentes \[MVP\]

Como operador, quiero reportar incidentes adjuntando fotografías o videos desde galeria, para entregar evidencia al soporte.

- Criterio propuesto: Seleccionar parking y servicio afectado.
- Criterio propuesto: Describir el problema, adjuntar evidencia y registrar el incidente.

### HU-04 · Incidentes \[MVP\]

Como especialista de soporte, quiero registrar acciones realizadas y su evidencia, para mantener trazabilidad de la atención.

- Criterio propuesto: Consultar incidente.
- Criterio propuesto: Registrar actividades, adjuntos y actualización de estado.

### HU-05 · Procedimientos \[MVP\]

Como operador, quiero buscar procedimientos por problema tecnológico, para ejecutar un diagnóstico inicial sin depender de otra persona.

- Criterio propuesto: Buscar por servicio o tipo de falla.
- Criterio propuesto: Visualizar instrucciones iniciales ordenadas.

## HU-06 · Directorio \[MVP\]

Como jefe de parking, quiero identificar el contacto y canal de escalamiento asociado al servicio, para solicitar ayuda al responsable adecuado.

- Criterio propuesto: Consultar contactos por servicio.
- Criterio propuesto: Distinguir soporte interno y proveedor externo.

### HU-07 · Mantenimiento \[Posterior\]

Como responsable de mantenimiento, quiero registrar tareas realizadas y sus evidencias, para demostrar el cumplimiento del plan preventivo.

- Criterio propuesto: Seleccionar equipo intervenido.
- Criterio propuesto: Registrar actividad, fecha y evidencia; consultar historial.

### HU-08 · Incidentes \[MVP\]

Como operador, quiero consultar los incidentes reportados, para conocer su estado de atención.

- Criterio propuesto: Visualizar incidentes propios y estados.

### HU-09 · Incidentes \[MVP\]

Como jefe de parking, quiero validar que un incidente fue resuelto, para confirmar la recuperación del servicio.

- Criterio propuesto: Consultar evidencia de resolución y registrar validación.

### HU-10 · Alertas \[Posterior\]

Como responsable tecnológico, quiero recibir alertas de fallas críticas, para conocer interrupciones oportunamente.

- Criterio propuesto: Visualizar alerta y servicio afectado.

### HU-11 · Indicadores \[Posterior\]

Como supervisor, quiero consultar indicadores de disponibilidad, para analizar comportamiento operacional.

- Criterio propuesto: Consultar indicadores definidos con el cliente.

### HU-12 · Mantenimiento \[Posterior\]

Como responsable de mantenimiento, quiero generar reportes de tareas ejecutadas, para respaldar el cumplimiento ante auditorías.

- Criterio propuesto: Obtener reporte con fechas y evidencia asociada.

### HU-13 · Login y Registro \[MVP\]

Como [cualquier empleado], quiero acceder a la aplicación exitosamente en una pantalla de acceso segura.

- Criterio propuesto: Poder acceder (logearse) a la app mediante Cognito (si es en internet) o Room (si es local).

### HU-14 · Selector de Usuario  \[Posterior\]

Como [cualquier empleado], quiero poder cambiar facilmente de usuario en caso de tener más de una cuenta en segundos.

- Criterio propuesto: Poder cambiar de usuario (logearse con otra cuenta en caso que la 2da cuenta no este logeada) a la app mediante un botón en la UI.


# 6. Requerimientos funcionales

Los RF describen las capacidades y operaciones esperadas. Son una sistematización preliminar de la presentación, no una aprobación contractual.

## 6.1 Estado operacional

| ID    | El sistema deberá permitir / realizar                         |
| ----- | ------------------------------------------------------------- |
| RF-01 | Consultar el estado operacional de cada estacionamiento.      |
| RF-02 | Mostrar el estado de sus servicios tecnológicos críticos.     |
| RF-03 | Identificar visualmente estacionamientos con afectaciones.    |
| RF-04 | Permitir a supervisores consultar múltiples estacionamientos. |

## 6.2 Gestión de incidentes

| ID    | El sistema deberá permitir / realizar                          |
| ----- | -------------------------------------------------------------- |
| RF-05 | Registrar incidentes tecnológicos detectados en terreno.       |
| RF-06 | Asociar cada incidente a estacionamiento y servicio afectados. |
| RF-07 | Adjuntar fotografías o videos como evidencia del incidente.    |
| RF-08 | Consultar el estado de atención de un incidente.               |
| RF-09 | Registrar actividades ejecutadas durante su resolución.        |
| RF-10 | Adjuntar evidencia de las acciones de resolución.              |
| RF-11 | Registrar el cierre y la validación de resolución.             |

## 6.3 Manuales y procedimientos

| ID    | El sistema deberá permitir / realizar                                 |
| ----- | --------------------------------------------------------------------- |
| RF-12 | Consultar manuales y procedimientos operacionales.                    |
| RF-13 | Buscar procedimientos por servicio o problema.                        |
| RF-14 | Presentar instrucciones para primeras acciones de diagnóstico.        |
| RF-15 | Facilitar la consulta autónoma de instrucciones de primera respuesta. |

## 6.4 Directorio y soporte

| ID    | El sistema deberá permitir / realizar                                |
| ----- | -------------------------------------------------------------------- |
| RF-16 | Consultar directorio de contactos de soporte técnico.                |
| RF-17 | Identificar canales de escalamiento por servicio.                    |
| RF-18 | Distinguir contactos internos, mesa de ayuda y proveedores externos. |
| RF-19 | Identificar al responsable de recibir cada tipo de afectación.       |

## 6.5 Alertas

| ID    | El sistema deberá permitir / realizar                     |
| ----- | --------------------------------------------------------- |
| RF-20 | Recibir alertas operacionales ante afectaciones críticas. |
| RF-21 | Mostrar parking y servicio asociado a cada alerta.        |

## 6.6 Reportes e indicadores

| ID    | El sistema deberá permitir / realizar                                    |
| ----- | ------------------------------------------------------------------------ |
| RF-22 | Consultar indicadores de disponibilidad de servicios.                    |
| RF-23 | Presentar información operacional consolidada para seguimiento y mejora. |

## 6.7 Mantenimiento preventivo

| ID    | El sistema deberá permitir / realizar                 |
| ----- | ----------------------------------------------------- |
| RF-24 | Registrar mantenimientos preventivos en terreno.      |
| RF-25 | Asociar actividades a los equipos intervenidos.       |
| RF-26 | Registrar fechas de ejecución.                        |
| RF-27 | Adjuntar evidencia de mantenimientos.                 |
| RF-28 | Generar reportes de cumplimiento del plan preventivo. |

Nota: los criterios exactos de semáforo de estado, las fuentes de actualización y la interoperabilidad con sistemas existentes permanecen abiertos.

# 7. Requerimientos no funcionales propuestos

Los RNF expresan cualidades y restricciones esperadas. La reunión no los especifica con métricas definitivas; se proponen para negociación y verificación posterior.

## 7.1 Seguridad y acceso

| ID     | Requisito de calidad propuesto                                             |
| ------ | -------------------------------------------------------------------------- |
| RNF-01 | Autenticar usuarios autorizados con Tokens JWT.                            |
| RNF-02 | Restringir funcionalidades según roles y permisos.                         |
| RNF-03 | Proteger la información durante transmisión y almacenamiento.              |
| RNF-04 | Cumplir políticas de seguridad corporativas y normativa chilena aplicable. |

## 7.2 Usabilidad y accesibilidad

| ID     | Requisito de calidad propuesto                                          |
| ------ | ----------------------------------------------------------------------- |
| RNF-05 | Disponer de una interfaz sencilla e intuitiva para personal en terreno. |
| RNF-06 | Presentar información operacional con claridad.                         |
| RNF-07 | Identificar estados visualmente sin depender solo del color.            |
| RNF-08 | Ordenar los procedimientos para facilitar el diagnóstico inicial.       |

## 7.3 Rendimiento

| ID     | Requisito de calidad propuesto                                                  |
| ------ | ------------------------------------------------------------------------------- |
| RNF-09 | Ofrecer tiempos de respuesta adecuados en contingencias.                        |
| RNF-10 | Permitir registrar incidentes y evidencia sin demoras que afecten la operación. |

## 7.4 Disponibilidad y confiabilidad

| ID     | Requisito de calidad propuesto                                       |
| ------ | -------------------------------------------------------------------- |
| RNF-11 | Mantener accesible la información necesaria para atender incidentes. |
| RNF-12 | Preservar los incidentes y evidencias registrados con persistencia.  |
| RNF-13 | Prevenir pérdida de datos al registrar o actualizar incidentes.      |

## 7.5 Compatibilidad e interoperabilidad

| ID     | Requisito de calidad propuesto                                                       |
| ------ | ------------------------------------------------------------------------------------ |
| RNF-14 | Ser compatible con dispositivos móviles acordados con la empresa.                    |
| RNF-15 | Facilitar futuras integraciones con fuentes operacionales disponibles y autorizadas. |

## 7.6 Trazabilidad y auditoría

| ID     | Requisito de calidad propuesto                                    |
| ------ | ----------------------------------------------------------------- |
| RNF-16 | Mantener la trazabilidad de incidentes y resolución.              |
| RNF-17 | Identificar responsable y fecha de actividades.                   |
| RNF-18 | Preservar asociación entre evidencia e incidente o mantenimiento. |

## 7.7 Mantenibilidad y escalabilidad

| ID     | Requisito de calidad propuesto                                                  |
| ------ | ------------------------------------------------------------------------------- |
| RNF-19 | Mantener organización modular para agregar funcionalidades.                     |
| RNF-20 | Permitir actualizar procedimientos y contactos sin cambiar el código de la app. |
| RNF-21 | Incorporar nuevos parkings y servicios sin rediseño estructural.                |

Ejemplo de métrica por acordar: tiempo máximo de consulta del estado de un parking (p. ej., tres segundos en condiciones normales). No se afirma que esa cifra fuese exigida por el cliente. La disponibilidad de esta aplicación es distinta de la disponibilidad de los servicios que consulta.

# 8. Tecnologías y herramientas

Se utilizarán las siguentes herramientas:


| Herramienta/Tecnología                   | Función                                                                    |
| ---------------------------------------- | -------------------------------------------------------------------------- |
| Kotlin	                               | Lenguaje de programación utilizado para desarrollar la aplicación Android. |
| Jetpack Compose                          | Herramienta para construir las interfaces gráficas de la aplicación.       |
| Room	                                   | Biblioteca que permite almacenar y gestionar datos localmente por SQLite.  |
| AWS Cognito	                           | Servicio de autenticación que gestiona el inicio y cierre de sesión.       |
| Spring Boot	                           | Framework utilizado para desarrollar los microservicios del backend.       |
| Retrofit	                               | Biblioteca que realiza solicitudes HTTP y consumir APIs desde Android.     |
| JUnit	                                   | Framework utilizado para ejecutar pruebas unitarias.                       |

# 9. Arquitectura y Estructura del Proyecto

El flujo de datos sigue un patrón unireccional desacoplado:

```text
[Jetpack Compose UI] ◄──(StateFlow)── [ViewModel]
                                             │
                                     (Repository)
                                       /         \
                        (LocalDataSource)       (RemoteDataSource)
                                │                           │
                            [Room DAO]                  [Retrofit API]
                                │                           │
                            (SQLite DB)                 (Servidor REST)
```

## ¿Por qué esta arquitectura?

Separar el proyecto en capas (`domain`, `data`, `ui`, `di`) no es una formalidad: cada capa resuelve un problema concreto y, al aislarlas, un cambio en una no obliga a tocar las demás.

- **`domain`**: es el "centro" de la app. Define *qué* es cada objeto (ej. Parking) y *qué* se puede hacer con ésto, sin saber si los datos vienen de SQLite, de una API o de un archivo. Así, si mañana cambias Room por otra base de datos, esta capa no se entera.
- **`data`**: es el "cómo". Sabe hablar con Room (`local`) y con Retrofit (`remote`), y sabe traducir esos formatos técnicos al modelo de dominio (`mapper`). El `repository` de esta capa es quien decide, en la práctica, de dónde sale la información que ve el usuario.
- **`ui`**: es la "cara visible". Solo le importa mostrar datos y reaccionar a acciones del usuario (escribir, tocar un botón). No sabe ni le importa si detrás hay Room, Retrofit o ambos.
- **`di`**: es el "pegamento". Como cada capa depende de interfaces (no de implementaciones concretas) para no acoplarse, algo tiene que decidir *qué implementación real* se usa en cada caso; eso lo hace Hilt a través de estos módulos.

El flujo de datos es unidireccional: la UI nunca escribe directamente en la base de datos ni llama a la API; todo pasa por el ViewModel → Repository → (Room y/o Retrofit). Esto hace que, sin importar de dónde vengan los datos, la UI siempre reciba la misma forma de información (`Flow<List<OBJETO>>`) y se actualice sola cuando esos datos cambian.

La estructura de paquetes sugerida en `com.example.appsaba` es:

```text
com.example.appsaba/                                        #com.cognito-auth/ ya no existe
 ├── MainActivity.kt
 ├── SabaApplication.kt
 ├── AppNavigation.kt #Se combina con SabaMainScaffold.kt, SabaMainScaffold.kt ya no existe
 ├── auth/
 │    ├── CognitoAuthManager.kt
 │    ├── CognitoLogoutRedirectActivity.kt
 │    └── CognitoAuthResult.kt #Se separa de dataclass CognitoTokens
 │
 ├── core/
 │    ├── config/                            
 │    │    ├── AppConfig.kt                     
 │    │    └── CognitoConfig.kt                             # SabaCognitoConfig.kt y CognitoConfig.kt se fusionan en un solo archivo
 │    └── jwt/                       
 │         └── JwtDecoder.kt                                # SabaCognitoConfig.kt y CognitoConfig.kt se fusionan en un solo archivo
 │
 ├── domain/
 │    ├── model/                                            # Modelos de Dominio
 │    │    ├── Incident.kt                                  # ReportIncident.kt y Ticket.kt ya no existen
 │    │    ├── LoginSession.kt                              # se fusiona con data class SavedSession que estaba en SessionStore.kt
 │    │    ├── Parking.kt                                   # Se fusiona con /local/model/RemoteParking.kt, /local/model/RemoteParking.kt ya no existe
 │    │    ├── Token.kt                                     # Solía ser dataclass CognitoTokens que estaba dentro de CognitoAuthResult.kt
 │    │    ├── Service.kt                     
 │    │    ├── SessionUser.kt                 
 │    │    ├── Session.kt                                   # Solía ser SavedSession.kt
 │    │    └── User.kt                 
 │    └── repository/                                       # Sólo el Repositorio, Implementación está en data/repository/
 │         ├── IncidentRepository.kt             
 │         ├── LoginRepository.kt                
 │         ├── ParkingRepository.kt     
 │         ├── ServiceRepository.kt               
 │         └── UserRepository.kt  
 │
 ├── data/
 │    ├── local/                
 │    │    ├── entity/                                      # Entidades Room (Tablas)
 │    │    │    ├── IncidentEntity.kt                       
 │    │    │    ├── ParkingEntity.kt                        # Se fusiona con RemoteParkingDao.kt, RemoteParkingDao.kt ya no existe
 │    │    │    ├── ServiceEntity.kt                       
 │    │    │    └── UserEntity.kt                          
 │    │    ├── dao/                                         # DAOs con Consultas SQL
 │    │    │    ├── IncidentDao.kt                          
 │    │    │    ├── ParkingDao.kt                           # Se fusiona con RemoteParkingDao.kt, RemoteParkingDao.kt ya no existe
 │    │    │    ├── ServiceDao.kt                           
 │    │    │    └── UserDao.kt                              
 │    │    ├── AppDatabase.kt                               # RoomDatabase
 │    │    └── datasource/                                  # Fuentes de Datos Locales
 │    │         ├── IncidentLocalDataSource.kt              
 │    │         ├── ParkingLocalDataSource.kt               
 │    │         ├── ServiceLocalDataSource.kt               
 │    │         └── UserLocalDataSource.kt                  
 │    ├── remote/
 │    │    ├── dto/                         # Data Transfer Object (JSON)
 │    │    │    ├── AuthDto.kt                             
 │    │    │    └── Ec2ParkingDto.kt                      
 │    │    ├── apiservice/                  
 │    │    │    ├── AuthApiService.kt           
 │    │    │    ├── Ec2ParkingApiService.kt           
 │    │    │    ├── FakeAuthApiService.kt           
 │    │    │    └── ApiService.kt           
 │    │    ├── datasource/                  # Fuente de Datos Remota
 │    │    │    ├── IncidentRemoteDataSource.kt              
 │    │    │    ├── ParkingRemoteDataSource.kt                  
 │    │    │    ├── ServiceRemoteDataSource.kt               
 │    │    │    └── UserRemoteDataSource.kt                  
 │    │    └── client/                         
 │    │         ├── Ec2RetrofitClient.kt                             
 │    │         └── RetrofitClient.kt  
 │    │     
 │    ├── mapper/                           # Mapeadores DTO <-> Entity <-> Domain  
 │    │    ├── IncidentMapper.kt                            
 │    │    ├── ParkingMapper.kt                             
 │    │    ├── ServiceMapper.kt                             
 │    │    └── UserMapper.kt                             
 │    ├── repository/                       # Implementación del Repositorio
 │    │    ├── IncidentRepositoryImpl.kt             
 │    │    ├── LoginRepositoryImpl.kt                
 │    │    ├── ParkingRepositoryImpl.kt     # Se fusiona con ParkingRepositoryImpl.kt, RemoteParkingRepositoryImpl.kt ya no existe
 │    │    ├── ServiceRepositoryImpl.kt               
 │    │    └── UserRepositoryImpl.kt                  
 ├── di/
 │    ├── AuthModule.kt                     # Proveedor Hilt para la Autenticación
 │    ├── DatabaseModule.kt                 # Proveedor Hilt para Room
 │    ├── NetworkModule.kt                  # Proveedor Hilt para Retrofit
 │    └── RepositoryModule.kt               # Vinculación Hilt para Repositorio
 │
 └── ui/ #Carpetas ui/data/ y ui/navigation/ ya no existen
      ├── components/
      │     ├── AuthCard.kt                
      │     └── DataWidgets.kt                
      ├── common/
      │     ├── CommonUiState.kt             # Estado inmutable de la UI
      │     └── CommonViewModel.kt           # ViewModel
      ├── guides/   
      │     ├── GuidesUiState.kt             # Estado inmutable de la UI
      │     ├── GuidesViewModel.kt           # ViewModel
      │     └── GuidesScreen.kt              # Vista Compose (Formulario y Lista)
      ├── home/ 
      │     ├── HomeUiState.kt               # Estado inmutable de la UI
      │     ├── HomeViewModel.kt             # ViewModel
      │     └── HomeScreen.kt                # Vista Compose (Formulario y Lista)
      ├── incidents/    
      │     ├── IncidentUiState.kt           # Estado inmutable de la UI
      │     ├── IncidentViewModel.kt         # ViewModel
      │     └── IncidentScreen.kt            # Vista Compose (Formulario y Lista)
      ├── login/    
      │     ├── LoginUiState.kt              # Estado inmutable de la UI, se fusiona con data class CognitoUiState que solía estar dentro de CognitoAuthViewModel.kt
      │     ├── LoginViewModel.kt            # ViewModel
      │     └── LoginScreen.kt               # Vista Compose (Formulario y Lista)
      ├── maintenance/   
      │     ├── MaintenanceUiState.kt        # Estado inmutable de la UI
      │     ├── MaintenanceViewModel.kt      # ViewModel
      │     └── MaintenanceScreen.kt         # Vista Compose (Formulario y Lista)
      ├── parkings/ 
      │     ├── ParkingUiState.kt            # Estado inmutable de la UI
      │     ├── ParkingViewModel.kt          # ViewModel
      │     └── ParkingScreen.kt             # Vista Compose (Formulario y Lista)
      ├── users/    
      │     ├── UsersUiState.kt              # Estado inmutable de la UI
      │     ├── UsersViewModel.kt            # ViewModel
      │     └── UsersScreen.kt               # Vista Compose (Formulario y Lista)
      └── theme/    

```

# 10. Funcionalidades y pantallas

### Describir cada funcionalidad, sus formularios, validaciones, navegación y comportamiento esperado.

La aplicación SABA dispone de diferentes módulos orientados a la supervisión tecnológica de estacionamientos, gestión de incidentes y consulta de información operacional.

Cada funcionalidad se encuentra organizada en su propio módulo de interfaz, siguiendo la arquitectura establecida para el proyecto.

## **10.1 Pantalla de Login**

**Archivos relacionados:**

- ui/login/LoginScreen.kt
- ui/login/LoginViewModel.kt
- ui/login/LoginUiState.kt
- auth/CognitoAuthManager.kt

**Descripción:**

El módulo de inicio de sesión permite autenticar a los usuarios para acceder a las funcionalidades de SABA.

La aplicación contempla dos mecanismos de acceso:

- **Autenticación remota:** mediante Amazon Cognito, utilizando el flujo Authorization Code con PKCE.
- **Acceso local:** mediante datos almacenados en Room, según lo contemplado en los requerimientos del proyecto. Su funcionamiento específico debe verificarse en la implementación actual.

**Flujo de funcionamiento:**

1. El usuario accede a la pantalla de Login.
2. Selecciona el método de autenticación (local o Cognito).
3. El sistema procesa las credenciales mediante el método seleccionado.
4. Si la autenticación es exitosa, el usuario logra entrar.
5. El usuario accede a la pantalla Home.

**Validaciones:**

- Verificar que la autenticación haya finalizado correctamente.
- Evitar el acceso a pantallas protegidas sin una sesión válida.
- Mostrar mensajes de error cuando falle la autenticación.

## **10.2 Gestión de incidentes**

**Archivos relacionados:**

- ui/incidents/IncidentScreen.kt
- ui/incidents/IncidentViewModel.kt
- ui/incidents/IncidentUiState.kt

**Descripción:**

Este módulo permite registrar, consultar y gestionar incidentes tecnológicos que afectan a los estacionamientos.

**Funcionalidades previstas:**

- Consultar incidentes registrados.
- Registrar un nuevo incidente.
- Asociar incidentes a estacionamientos y servicios.
- Consultar el estado de atención.
- Adjuntar evidencia fotográfica o audiovisual.
- Registrar actividades de resolución.
- Validar el cierre de incidentes.

**Flujo esperado:**

1. El usuario ingresa al módulo de incidentes.
2. Consulta los incidentes existentes o inicia un nuevo registro.
3. Completa el formulario con la información correspondiente.
4. El sistema valida los datos ingresados.
5. Se registra el incidente mediante el repositorio correspondiente.

**Validaciones propuestas:**

- Verificar los campos obligatorios.
- Comprobar que exista un estacionamiento seleccionado.
- Evitar registros con información insuficiente.
- Informar errores de almacenamiento o comunicación.

## **10.3 Pantalla de Guías**

**Archivos relacionados:**

- ui/guides/GuidesScreen.kt
- ui/guides/GuidesViewModel.kt
- ui/guides/GuidesUiState.kt

**Descripción:**

El módulo de Guías y Procedimientos tiene como objetivo facilitar el acceso a instrucciones de diagnóstico y primera atención ante problemas tecnológicos presentes en los estacionamientos.
Su propósito es orientar a los trabajadores durante la identificación y atención inicial de fallas, reduciendo la dependencia del personal especializado para consultas básicas.

**Funcionalidades previstas:**

-  Consultar guías y procedimientos técnicos.
-  Identificar procedimientos relacionados con servicios o equipos tecnológicos.
-  Acceder a instrucciones de diagnóstico inicial.
-  Consultar recomendaciones para la primera atención de incidentes.
-  Facilitar la identificación de situaciones que requieren asistencia técnica especializada.
-  Contactar al número de soporte técnico


**Flujo de funcionamiento:**

1.  El usuario ingresa a la pantalla de Guías.
2.  Consulta las guías o procedimientos disponibles.
3.  Selecciona la guía relacionada con el problema identificado.
4.  Visualiza las instrucciones y recomendaciones.
5.  Utiliza la información para orientar el diagnóstico inicial o solicitar asistencia especializada.
6.  (Opcional) Puede llamar al número de contacto de soporte técnico para asistencia detallada de persona a persona.


**Consideraciones funcionales:**

-  Presentar las instrucciones de manera clara y ordenada.
-  Permitir una navegación sencilla entre los procedimientos.
-  Diferenciar las acciones de diagnóstico inicial de aquellas que requieren personal autorizado.
-  Informar cuando no exista un procedimiento disponible para el problema consultado.
-  Agregar permisos al boton de llamada


## **10.4 Pantalla de Mantenciones**

**Archivos relacionados:**

- ui/maintenance/MaintenanceScreen.kt
- ui/maintenance/MaintenanceViewModel.kt
- ui/maintenance/MaintenanceUiState.kt

**Descripción:**

El módulo de Mantenciones tiene como objetivo permitir a los trabajadores consultar, registrar y realizar seguimiento de las actividades de mantenimiento preventivo de los equipos tecnológicos presentes en los estacionamientos.
Su propósito es facilitar el control de las intervenciones realizadas, mantener un historial de los trabajos ejecutados y conservar evidencias que permitan verificar el cumplimiento de las tareas de mantenimiento.

**Funcionalidades previstas:**

-  Consultar las mantenciones registradas.
-  Registrar nuevas actividades de mantenimiento.
-  Asociar cada mantención a un equipo o servicio tecnológico.
-  Registrar fechas y descripciones de las intervenciones.
-  Adjuntar evidencia de las actividades realizadas.
-  Consultar el historial de mantenciones.
-  Generar reportes de las actividades ejecutadas.



**Flujo de funcionamiento:**

1.  El usuario ingresa a la pantalla de Mantenciones.
2.  Consulta las mantenciones existentes o selecciona la opción para registrar una nueva.
3.  Selecciona el equipo intervenido e ingresa los detalles de la actividad.
4.  El sistema valida los datos ingresados y registra la información.
5.  El usuario puede consultar posteriormente el historial de las intervenciones.



**Validaciones:**

-  Verificar que los campos obligatorios estén completos.
-  Comprobar que se haya seleccionado un equipo válido.
-  Validar que las fechas ingresadas sean correctas.
-  Evitar registros incompletos.
-  Informar errores durante el almacenamiento de información.


## **10.5 Pantalla Home**

**Archivos relacionados:**

- ui/home/HomeScreen.kt
- ui/home/HomeViewModel.kt
- ui/home/HomeUiState.kt

**Descripción:**

La pantalla Home constituye la interfaz principal de SABA, desde la cual el usuario puede acceder a las distintas funcionalidades disponibles dentro de la aplicación.
Su propósito es proporcionar un punto de acceso centralizado a los módulos del sistema y facilitar la consulta de información operacional relevante para los trabajadores.

**Funcionalidades previstas:**

-  Presentar la interfaz principal después del inicio de sesión.
-  Facilitar la navegación hacia las funcionalidades de la aplicación.
-  Permitir el acceso a los módulos de Incidencias, Guías y Mantenciones, según las opciones habilitadas.
-  Proporcionar acceso a información general de los estacionamientos y sus servicios.
-  Facilitar la consulta del estado operacional cuando esa información se encuentre disponible.


**Flujo de funcionamiento:**

1.  El usuario inicia sesión correctamente.
2.  La aplicación presenta la pantalla Home.
3.  El usuario visualiza las opciones y la información disponible.
4.  Selecciona el módulo al que desea acceder.
5.  La aplicación navega a la pantalla correspondiente.


**Validaciones:**

-  Comprobar que el usuario tenga una sesión válida para acceder a la pantalla.
-  Mostrar correctamente las opciones de navegación disponibles.
-  Evitar la navegación hacia rutas inexistentes.
-  Gestionar los estados de carga y error cuando se consulte información operacional.
-  Mantener una presentación clara y organizada de los elementos de la interfaz.


# 11. Persistencia de datos

### Documentar las entidades, DAOs, relaciones, repositorios y operaciones realizadas con Room.

SABA utiliza Room como mecanismo de persistencia local, permitiendo almacenar información en una base de datos SQLite dentro del dispositivo Android.

La gestión de los datos sigue la estructura definida en el proyecto, separando entidades, DAOs, fuentes de datos y repositorios.

## **11.1 Entidades**

Las entidades representan las tablas utilizadas para almacenar la información local.

| **Entidad**      | **Responsabilidad**                                          |
| ---------------- | ------------------------------------------------------------ |
| `UserEntity`     | Almacenar información local de usuarios.                     |
| `IncidentEntity` | Representar los incidentes registrados.                      |
| `ParkingEntity`  | Representar los estacionamientos.                            |
| `ServiceEntity`  | Representar los servicios asociados al contexto operacional. |

Los campos, claves primarias y relaciones deben documentarse a partir de las declaraciones reales de cada entidad.

**Por qué existe:** Room necesita una clase anotada para saber cómo crear y leer la tabla SQL. `@Entity(tableName = "parkings")` le dice "crea una tabla llamada `parkings`" y cada propiedad se vuelve una columna. `@PrimaryKey(autoGenerate = true)` delega en SQLite la generación del `id`, para no tener que inventarlo a mano.

**Por qué no es lo mismo que `Parking` (domain):** porque si mañana cambias un detalle de la tabla (por ejemplo, agregar una columna interna de auditoría), no quieres que ese detalle "se filtre" al resto de la app. `ParkingEntity` es la representación de la fila en SQLite; `Parking` es el concepto de negocio.

Representa la estructura de la tabla `parkings` en SQLite:

```kotlin
package com.example.appsaba.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parkings")
data class ParkingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val address: String,
    val commune: String = "",
    val totalSpaces: Int = 0,
    val freeSpaces: Int = 0,
    val isActive: Boolean = true
)
```


## **11.2 Data Access Objects (DAO)**

Los DAOs proporcionan los métodos necesarios para interactuar con SQLite mediante Room.

Los archivos correspondientes son:

- UserDao.kt
- IncidentDao.kt
- ParkingDao.kt
- ServiceDao.kt

Estos componentes encapsulan las consultas y operaciones de acceso a los datos.

**Por qué existe:** el DAO (*Data Access Object*) es el único lugar del proyecto donde se escribe SQL. Room genera automáticamente la implementación de esta interfaz a partir de las anotaciones (`@Query`, `@Insert`, `@Query` para `DELETE`), así que nunca escribes el código que ejecuta esas consultas, solo la firma.

**Qué sentido tiene:** que `observeAll()` devuelva `Flow<List<ParkingEntity>>` (en vez de una lista simple) significa que Room **emite un nuevo valor automáticamente cada vez que la tabla cambia**. Eso es lo que permite que, más adelante, el ViewModel "escuche" la tabla y la UI se actualice sola sin tener que refrescar manualmente.

**¿Qué es `Flow` exactamente?** Es un flujo de datos asíncrono de Kotlin: en vez de pedir un valor una sola vez (como hace una función `suspend fun observe(): List<Parking>`), un `Flow` representa **una secuencia de valores en el tiempo** a la que alguien se "suscribe" con `.collect { ... }`. Pensarlo como una especie de "radio" al que te sintonizas: no pides un dato puntual, sino que te quedas escuchando y te avisan cada vez que hay uno nuevo.

Aplicado a `observeAll(): Flow<List<ParkingEntity>>`:
- Es un flujo **frío** (*cold*): no hace nada hasta que alguien lo colecciona
- Room se encarga de **emitir una nueva lista completa cada vez que la tabla `parkings` cambia** (un `INSERT` o `DELETE`), sin que nadie tenga que pedirlo explícitamente ni volver a ejecutar la consulta a mano.
- Se cancela solo: cuando el que está coleccionando desaparece (por ejemplo, la pantalla se cierra y `viewModelScope` se cancela), Room deja de emitir y libera los recursos.
- Es la base de que toda la cadena Room → Repository → ViewModel → Compose sea **reactiva**: un cambio en SQLite se propaga solo hasta la UI, sin botones de "refrescar" ni *polling*.

Define las operaciones de base de datos. Retorna `Flow` para observación reactiva:

```kotlin
package com.example.appsaba.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appsaba.data.local.entity.ParkingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(parking: ParkingEntity): Long

    @Update
    suspend fun update(parking: ParkingEntity): Int

    @Delete
    suspend fun delete(parking: ParkingEntity): Int

    @Query("SELECT * FROM parkings ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<ParkingEntity>>

    @Query("SELECT * FROM parkings WHERE isActive = 1 ORDER BY name COLLATE NOCASE ASC")
    fun observeActive(): Flow<List<ParkingEntity>>

    @Query("SELECT * FROM parkings WHERE id = :parkingId LIMIT 1")
    suspend fun getById(parkingId: Long): ParkingEntity?
}
```

## **11.3 Repositorios y fuentes de datos**

La aplicación separa las operaciones de almacenamiento de la lógica de presentación.

Por ejemplo, el acceso a los incidentes sigue la siguiente estructura:

```
IncidentViewModel → IncidentRepository → IncidentRepositoryImpl → IncidentLocalDataSource → IncidentDao → Room
```

De esta manera, la interfaz no necesita acceder directamente a la base de datos.

La integración posterior con fuentes remotas deberá respetar esta misma separación de responsabilidades.

### 11.3.1 AppDatabase.kt
**Por qué existe:** es el punto único de acceso a la base de datos física. `@Database(entities = [...], version = 1)` le dice a Room qué tablas debe crear y en qué versión de esquema está la base; `abstract fun parkingDao()` expone el DAO para que Hilt pueda inyectarlo en otros lugares. Solo debe existir **una instancia** de esta clase en toda la app (por eso más adelante se inyecta como `@Singleton`).

```kotlin
package com.example.appsaba.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appsaba.data.local.entity.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        ParkingEntity::class,
        ServiceEntity::class,
        IncidentEntity::class
    ],
    version = 2,
    exportSchema = false // MVP: pendiente configurar exportacion y migraciones versionadas
)
```

### 11.3.2 ParkingLocalDataSource.kt
**¿Por qué existe si ya tenemos el DAO?** Esta es la pregunta obligada, porque en este proyecto `ParkingLocalDataSourceImpl` solo **delega 1:1** cada método al `ParkingDao` — no agrega ninguna lógica propia. La razón de que exista igual no es técnica sino de **desacoplamiento de capas**:

- El `ParkingDao` es una interfaz de **Room**: sus tipos, anotaciones (`@Dao`, `@Query`, `@Insert`) y semántica solo tienen sentido porque Room las entiende. Si el `Repository` inyectara el DAO directamente, quedaría atado a Room.
- El `ParkingLocalDataSource` es una interfaz **neutral**: dice "la fuente de datos local sabe `getById()`, `insert()`, etc.", sin mencionar Room para nada. La implementación (`ParkingLocalDataSourceImpl`) es la *única* clase que conoce al DAO; todo lo demás (Repository, ViewModel, UI) ni se entera de que existe.
- En pruebas, se puede reemplazar `ParkingLocalDataSource` por una implementación falsa sin tocar Room ni el dispositivo, al testear el Repository o el ViewModel de forma aislada.
- Si el día de mañana cambias Room por otra librería de persistencia, o agregas una segunda fuente local (por ejemplo, un cache en memoria), el cambio queda contenido en esta capa: el Repository no se entera.

**Siendo honestos:** en un proyecto de este tamaño, con un solo DAO y sin planes de cambiar de librería, esta capa hoy es principalmente *boilerplate* — no evita ningún bug ni agrega comportamiento. Es una decisión de **Clean Architecture por consistencia y escalabilidad a futuro**, no una necesidad técnica inmediata. Si el proyecto fuera a quedarse siempre así de simple, se podría inyectar el `ParkingDao` directamente en el Repository sin perder nada hoy; la ventaja de mantener el `DataSource` aparece recién cuando el proyecto crece (más fuentes de datos, más pruebas, cambios de librería).

Abstrae las llamadas directas al DAO:

```kotlin
package com.example.appsaba.data.local.datasource

import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.local.ParkingDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ParkingLocalDataSource {
    fun observeAll(): Flow<List<ParkingEntity>>
    fun observeActive(): Flow<List<ParkingEntity>>
    suspend fun getById(parkingId: Long): ParkingEntity?
    suspend fun insert(parking: ParkingEntity): Long
    suspend fun update(parking: ParkingEntity): Int
    suspend fun delete(parking: ParkingEntity): Int
}

class ParkingLocalDataSourceImpl @Inject constructor(
    private val dao: ParkingDao
) : ParkingLocalDataSource {
    override fun observeAll() = dao.observeAll()
    override fun observeActive() = dao.observeActive()
    override suspend fun getById(parkingId: Long) = dao.getById(parkingId)
    override suspend fun insert(parking: ParkingEntity) = dao.insert(parking)
    override suspend fun update(parking: ParkingEntity) = dao.update(parking)
    override suspend fun delete(parking: ParkingEntity) = dao.delete(parking)
}

```


# 12. Autenticación y seguridad

### Explicar el funcionamiento de Amazon Cognito, el inicio y cierre de sesión, y el tratamiento de credenciales y tokens.

SABA incorpora Amazon Cognito para gestionar la autenticación remota de los usuarios.

## **12.1 Autenticación mediante Cognito**

La autenticación utiliza el flujo **Authorization Code con PKCE**, diseñado para permitir que una aplicación móvil autentique usuarios sin incluir un secreto de cliente dentro de la aplicación.

Entre sus componentes se encuentran:

| **Componente**                     | **Responsabilidad**                                     |
| ---------------------------------- | ------------------------------------------------------- |
| `CognitoAuthManager.kt`            | Gestionar las operaciones de autenticación con Cognito. |
| `CognitoAuthResult.kt`             | Representar el resultado del proceso de autenticación.  |
| `CognitoConfig.kt`                 | Centralizar la configuración de Cognito.                |
| `CognitoLogoutRedirectActivity.kt` | Procesar la redirección asociada al cierre de sesión.   |
| `JwtDecoder.kt`                    | Decodificar información de tokens JWT.                  |

## **12.2 Gestión de sesiones**

La aplicación utiliza modelos de dominio relacionados con los usuarios y las sesiones, como LoginSession, SessionUser, Session y Token.

La documentación definitiva deberá explicar cómo se conserva la sesión, cómo se comprueba su vigencia y qué información se elimina al cerrar sesión.

## **12.3 Consideraciones de seguridad**

- Evitar almacenar contraseñas en texto plano.
- No incluir credenciales privadas en el repositorio.
- Proteger los tokens de autenticación.
- Utilizar conexiones HTTPS para transmitir información sensible.
- Comprobar que los servicios protegidos validen las credenciales correspondientes.