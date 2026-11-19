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
- [13. Arquitectura e integración de microservicios Spring Boot (CSR, Clients y BFF)](#13-arquitectura-e-integración-de-microservicios-spring-boot-csr-clients-y-bff)



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

# 13. Arquitectura e integración de microservicios Spring Boot (CSR, Clients y BFF)

> **Estado documental:** especificación técnica de desarrollo. La existencia de una clase en los árboles de esta sección indica una **responsabilidad prevista**, no que esté programada, compilada o probada. El proyecto `springboot/parking/` ya fue creado, pero su CRUD, la persistencia y la conexión extremo a extremo deben verificarse. Los proyectos `incidents/` y `bff/` están planificados.

## 13.1 Objetivo y principios de implementación

SABA incorporará un backend en **Java con Spring Boot** para que los estacionamientos y las incidencias puedan compartirse entre dispositivos. Se utilizarán el **patrón Controller–Service–Repository (CSR)**, una interfaz de servicio con su `ServiceImpl`, **Clients HTTP basados en `RestClient`** y un **Backend for Frontend (BFF)** como punto de entrada remoto de Android.

La aplicación Android **conserva íntegramente** la arquitectura del capítulo 9: `Screen → ViewModel → Repository → LocalDataSource/Room` o `RemoteDataSource/Retrofit`. El backend se agrega detrás de Retrofit; no se reorganizan las carpetas Android para adaptarlas a Spring Boot.

Reglas de diseño:

- Un microservicio es dueño de su información, sus reglas y su propia base de datos. No accede por SQL a la base de otro microservicio.
- Las solicitudes externas llegan al BFF; este delega mediante Clients HTTP y no contiene los repositorios JPA de los servicios.
- Los Controllers reciben y responden HTTP; los `ServiceImpl` implementan reglas; los Repository JPA consultan y guardan datos.
- **No se crea un microservicio de autenticación**. Android gestiona el inicio/cierre de sesión con Cognito; BFF y microservicios validan y autorizan el access token recibido.
- Se conservarán nombres, tipos y contratos existentes de Android siempre que sea posible. Si un cambio de contrato es imprescindible, se documentará y se actualizarán ambos extremos de manera coordinada.
- Los archivos del backend solo se crearán cuando representen una responsabilidad concreta. No se agregarán componentes decorativos, sin uso ni implementaciones duplicadas.

## 13.2 Componentes, alcance y estado

| Componente | Responsabilidad | Datos persistidos | Estado conocido |
| --- | --- | --- | --- |
| `springboot/parking/` | CRUD y validaciones de estacionamientos remotos; gestión de revisiones. | Base de datos propia, prevista con PostgreSQL/JPA. | Proyecto inicial creado; funcionalidad por verificar. |
| `springboot/incidents/` | Registro, consulta, seguimiento y cierre de incidencias remotas. | Base de datos propia, prevista con PostgreSQL/JPA. | Pendiente. |
| `springboot/bff/` | API HTTP para Android y delegación hacia ambos microservicios. | Sin base de datos de negocio. | Pendiente. |
| `app/` | UI, sesiones Cognito, lógica Android y datos locales con Room. | SQLite del dispositivo. | Aplicación existente; la integración con Spring Boot está pendiente. |

El servicio `parking` se desarrollará primero porque Android **ya define** los endpoints remotos de estacionamientos. Los servicios no implementan cobros, emisión de boletas ni control directo de barreras.

## 13.3 Diagrama de comunicación completo

```text
 SABA ANDROID (Kotlin / Compose)
 ┌─────────────────────────────────────────────────────────────┐
 │ Screen ──eventos──> ViewModel ──> Repository (domain)       │
 │      <── StateFlow ───┘                  │                    │
 │                              RepositoryImpl (data)          │
 │                                  /         \                │
 │                         LocalDataSource  RemoteDataSource   │
 │                               │               │             │
 │                         DAO / Room       API / Retrofit     │
 │                               │               │             │
 │                            SQLite        HTTP + JWT         │
 └───────────────────────────────────────────────┼─────────────┘
                                                 │
                                                 ▼
                        ┌────────────────────────────────────┐
                        │ BFF: Controller → Client/RestClient│
                        │     Validación JWT y autorización  │
                        └────────────────┬───────────────────┘
                                         │
                             ┌───────────┴────────────┐
                             ▼                        ▼
                      MS parking                 MS incidents
                      Controller                 Controller
                          │                          │
                       Service                    Service
                          │                          │
                      ServiceImpl                ServiceImpl
                          │                          │
                     Repository JPA              Repository JPA
                          │                          │
                     BD parking                 BD incidents
                             ▲                        │
                             └── ParkingClient ───────┘
                                 RestClient interno
                                 (solo si hace falta)
```

**Tipos de cliente distintos:** `Retrofit` pertenece a Android y llama al BFF; `RestClient` pertenece a Spring Boot y permite llamar del BFF a los microservicios o entre microservicios. **Los Clients no acceden a tablas**: transmiten solicitudes HTTP, cuerpos JSON, encabezados y respuestas.

## 13.4 Estructura prevista de archivos del backend

Los nombres indicados a continuación son el **objetivo de desarrollo**. El paquete Java real se determinará desde el `group` y el código generado por Spring Initializr; no se inventará un package incompatible con el proyecto de NetBeans.

```text
APP_MOVIL_SABA/
├── app/                                    # Android: se conserva su arquitectura
└── springboot/
    ├── parking/                            # Proyecto Java + Gradle independiente
    │   ├── build.gradle
    │   └── src/
    │       ├── main/
    │       │   ├── java/<paquete_real>/
    │       │   │   ├── <ParkingApplication>.java
    │       │   │   ├── controller/ParkingController.java
    │       │   │   ├── service/ParkingService.java
    │       │   │   ├── serviceimpl/ParkingServiceImpl.java
    │       │   │   ├── repository/ParkingRepository.java
    │       │   │   ├── model/ParkingEntity.java
    │       │   │   ├── dto/ParkingDto.java
    │       │   │   ├── exception/            # Si hay excepciones propias
    │       │   │   └── config/               # Seguridad y beans necesarios
    │       │   └── resources/
    │       │       ├── application.properties # O application.yml
    │       │       └── db/migration/         # Si utilizamos Flyway
    │       └── test/java/<paquete_real>/
    ├── incidents/                          # Proyecto independiente
    │   └── src/main/java/<paquete_real>/
    │       ├── <IncidentsApplication>.java
    │       ├── controller/IncidentController.java
    │       ├── service/IncidentService.java
    │       ├── serviceimpl/IncidentServiceImpl.java
    │       ├── repository/IncidentRepository.java
    │       ├── model/IncidentEntity.java
    │       ├── dto/IncidentDto.java
    │       ├── client/                      # Solo si se usa consulta interservicio
    │       │   ├── ParkingClient.java
    │       │   └── ParkingClientImpl.java
    │       ├── exception/
    │       └── config/
    └── bff/                                # Proyecto independiente
        └── src/main/java/<paquete_real>/
            ├── <BffApplication>.java
            ├── controller/
            │   ├── ParkingBffController.java
            │   └── IncidentBffController.java
            ├── client/
            │   ├── ParkingClient.java
            │   ├── ParkingClientImpl.java
            │   ├── IncidentClient.java
            │   └── IncidentClientImpl.java
            ├── dto/                        # Solo si el BFF adapta contratos
            ├── exception/
            └── config/                     # Seguridad / RestClient
```

Las carpetas `config/`, `exception/` y `dto/` no obligan a crear archivos vacíos. Cada aplicación Spring Boot también tendrá su propia configuración Gradle, recursos y pruebas.

## 13.5 Archivos de `parking`: responsabilidad y comunicación

| Archivo | Qué contiene o realiza | Se comunica con |
| --- | --- | --- |
| Clase principal `*Application.java` | Arranca el contexto Spring Boot y detecta componentes del paquete. | Configuración y beans de Spring. |
| `build.gradle` | Declara Java, Gradle y las dependencias necesarias. | Proceso de compilación; no participa en solicitudes HTTP. |
| `application.properties`/`.yml` | Puerto, conexión a BD, URL del emisor JWT y parámetros externalizados. | Spring Boot, JPA, seguridad y Clients si existieran. |
| `controller/ParkingController.java` | Atiende HTTP `GET`, `POST`, `PUT` y `DELETE`; recibe DTO, path y headers; devuelve códigos y datos. | `ParkingService` (inyectado). |
| `service/ParkingService.java` | **Interfaz** con las operaciones disponibles; no contiene las consultas SQL. | Contrato entre Controller y `ParkingServiceImpl`. |
| `serviceimpl/ParkingServiceImpl.java` | **Implementación**: valida reglas, comprueba `revision`, transforma DTO ↔ Entity y coordina transacciones. | `ParkingRepository` y modelos/DTO. |
| `repository/ParkingRepository.java` | Interfaz `JpaRepository<ParkingEntity, Long>` (o equivalente). | JPA y la base de datos de `parking`. |
| `model/ParkingEntity.java` | Define las columnas y reglas de persistencia de la tabla remota mediante JPA. | `ParkingRepository` y `ParkingServiceImpl`. |
| `dto/ParkingDto.java` | Representa la información enviada y recibida por JSON. Puede requerir DTOs de creación y modificación si sus restricciones difieren. | Controller, ServiceImpl y Client del BFF. |
| `exception/` | Excepciones específicas y manejo global de errores cuando corresponda. | Controller/ServiceImpl → respuestas HTTP. |
| `config/` | Configuración de seguridad y, cuando sea necesario, de beans. | Validación JWT y componentes de Spring. |
| `db/migration/` | Scripts Flyway para crear/evolucionar el esquema, si se adopta Flyway. | PostgreSQL al iniciar el servicio. |
| `src/test/` | Pruebas de reglas del ServiceImpl, Controller y persistencia. | Componentes sujetos a prueba y dobles de prueba. |

**Flujo interno de creación de estacionamiento:** `ParkingController → ParkingService (interfaz) → ParkingServiceImpl → ParkingRepository → ParkingEntity / PostgreSQL`. La respuesta recorre el camino inverso en forma de `ParkingDto`, **no como entidad JPA enviada directamente a Android**.

> La interfaz `ParkingService` permite inyectar su implementación mediante Spring. La interfaz `ParkingRepository` de JPA es distinta tanto de `ParkingService` como del repositorio de dominio Android que también se llama `ParkingRepository.kt`.

## 13.6 Archivos de `incidents`: responsabilidad y comunicación

| Archivo | Qué contiene o realiza | Se comunica con |
| --- | --- | --- |
| `controller/IncidentController.java` | Expone los endpoints REST de incidencias, recibe solicitudes y devuelve respuestas. | `IncidentService`. |
| `service/IncidentService.java` | Interfaz de operaciones de consulta, registro, actualización y cierre. | Controller y ServiceImpl. |
| `serviceimpl/IncidentServiceImpl.java` | Valida campos y transiciones de estado, coordina el guardado y, si hace falta, verifica el estacionamiento asociado. | `IncidentRepository` y opcionalmente `ParkingClient`. |
| `repository/IncidentRepository.java` | Accede únicamente a la base de datos de Incidencias mediante JPA. | `IncidentEntity` y PostgreSQL del servicio. |
| `model/IncidentEntity.java` | Almacena los datos persistentes de la incidencia y sus relaciones dentro de la BD del servicio. | Repository y ServiceImpl. |
| `dto/IncidentDto.java` | Define los campos JSON de entrada/salida de incidencias. | Controller, ServiceImpl y BFF. |
| `client/ParkingClient.java` | **Interfaz**, si necesitamos consultar un estacionamiento desde Incidencias. | `IncidentServiceImpl`. |
| `client/ParkingClientImpl.java` | **Implementación** mediante Spring `RestClient`; llama a un endpoint del MS Estacionamientos. | HTTP → `ParkingController` remoto. |
| `exception/` y `config/` | Errores controlados, parámetros de red y verificación de JWT. | Controller, ServiceImpl, RestClient y Spring Security. |

**Caso de uso previsto:** si se exige que `parkingId` corresponda a un estacionamiento remoto existente, `IncidentServiceImpl` llama a `ParkingClient`; este hace una solicitud HTTP al MS `parking`. Tras recibir una confirmación válida, Incidencias guarda **su propio** `IncidentEntity` mediante `IncidentRepository`.

La consulta entre servicios **no es automática ni obligatoria para todos los endpoints**. La implementaremos solo cuando exista una regla de negocio definida; la caída de `parking` debe producir un resultado controlado y no permitir un registro incoherente por omisión. No hay claves foráneas SQL entre las bases de los dos servicios.

## 13.7 Archivos del BFF: responsabilidad y comunicación

| Archivo | Qué hace | Se comunica con |
| --- | --- | --- |
| `controller/ParkingBffController.java` | Recibe las rutas públicas `/api/parkings`, sus DTO, headers y parámetros. | `ParkingClient` del BFF. |
| `client/ParkingClient.java` | Interfaz que declara las operaciones HTTP hacia `parking`. | Controller y `ParkingClientImpl`. |
| `client/ParkingClientImpl.java` | Ejecuta `GET`, `POST`, `PUT` y `DELETE` con `RestClient`; propaga headers y convierte JSON ↔ DTO. | `ParkingController` del MS `parking`. |
| `controller/IncidentBffController.java` | Recibe las rutas públicas `/api/incidents`. | `IncidentClient` del BFF. |
| `client/IncidentClient.java` | Interfaz para llamadas hacia `incidents`. | Controller y `IncidentClientImpl`. |
| `client/IncidentClientImpl.java` | Implementación `RestClient` para llamar al MS de Incidencias. | `IncidentController` remoto. |
| `dto/` | DTO propio **solo** cuando el BFF tenga que transformar o agregar una respuesta. | Controllers y Clients del BFF. |
| `config/` | Bean/configuración de `RestClient`, URL de cada servicio y reglas de seguridad. | Clients y Spring Security. |
| `exception/` | Errores de red, timeouts y traducción de códigos HTTP sin ocultar fallos. | Controllers y Clients. |

El BFF funciona inicialmente como **intermediario HTTP**. No implementa CRUD mediante JPA, no abre conexiones a las bases de datos de los microservicios y no reemplaza sus `ServiceImpl`. Solo añadiremos respuestas agregadas para Home si se define un caso concreto.

La URL de Retrofit en Android apuntará al BFF **cuando la ruta y la autenticación estén funcionando**. Cada Client BFF tiene una URL interna propia, configurable, distinta de la URL pública.

## 13.8 ¿Debemos compartir los DTO y Entity con Android?

**No se comparten las clases fuente.** Cada aplicación tiene su propia representación de datos; **comparten un contrato JSON** que debe mantener nombres de campos, tipos y reglas de compatibilidad.

| Responsabilidad | Android (Kotlin) | Backend (Java) |
| --- | --- | --- |
| Modelo que utiliza la lógica de la aplicación | `domain/model/Parking.kt` | Concepto representado en `ParkingServiceImpl`; no se copia el modelo Kotlin. |
| Persistencia local | `data/local/entity/ParkingEntity.kt`, anotaciones Room/SQLite | **No utiliza** la entidad Android. |
| Persistencia remota | Android no administra directamente PostgreSQL. | `parking/model/ParkingEntity.java`, anotaciones JPA. |
| Comunicación HTTP | `data/remote/dto/Ec2ParkingDto.kt` y los DTOs de creación/modificación | `parking/dto/ParkingDto.java` y DTOs de escritura compatibles. |
| Al pasar por el BFF | Retrofit serializa/deserializa JSON | Client `RestClient` utiliza DTOs Java; no necesita Entity JPA. |

Ejemplo del JSON que deberá poder leer `Ec2ParkingDto.kt` (valores ilustrativos):

```json
{
  "id": 15,
  "name": "Estacionamiento Central",
  "address": "Av. Principal 123",
  "commune": "Santiago",
  "totalSpaces": 120,
  "freeSpaces": 42,
  "isActive": true,
  "revision": 3,
  "updatedAt": 1795000000000,
  "status": "Activo"
}
```

El campo `updatedAt` del DTO Android actual es un **`Long`**. En Java deberá serializarse como número compatible, no cambiarlo silenciosamente por un string ISO. El `status` es un campo del contrato; su semántica definitiva se documentará sin confundir actividad del parking con el estado de sus equipos.

En la app, `ParkingMapper.kt` transforma `Ec2ParkingDto → ParkingEntity` de caché y luego el modelo `Parking` del dominio; el backend transforma `ParkingDto ↔ ParkingEntity` JPA. **No se transfieren objetos de memoria entre Java y Kotlin: se intercambian JSON mediante HTTP.**

Para Incidencias, el contrato debe respetar las propiedades reales del modelo `domain/model/Incident.kt` y los formularios existentes: `ticketCode`, `parkingId`, `serviceId`, `failureType`, `description`, `status`, `reportedAt`, datos del reportante, evidencia y resolución. **La conversión local/servidor todavía requiere diseñarse**, especialmente por la diferencia entre IDs locales y remotos.

## 13.9 Archivos Android involucrados en la integración

Los nombres siguientes pertenecen al proyecto Android actual; su estructura no se modifica.

| Archivo Android | Papel en la comunicación con Spring Boot |
| --- | --- |
| `ui/home/Ec2ParkingsSection.kt` | UI actual de consulta y CRUD remoto de estacionamientos. Entrega eventos a `HomeViewModel`. |
| `ui/home/HomeViewModel.kt` y `HomeUiState.kt` | Gestionan token remoto, carga, acciones, lista en caché y mensajes de error; exponen estado a Compose. |
| `domain/repository/ParkingRepository.kt` | Contrato de dominio para datos locales y operaciones remotas. |
| `data/repository/ParkingRepositoryImpl.kt` | Coordina el acceso remoto y la actualización de Room. |
| `data/remote/datasource/ParkingRemoteDataSource.kt` | Encapsula las llamadas al API Retrofit y agrega `Authorization: Bearer ...`. |
| `data/remote/apiservice/Ec2ParkingApiService.kt` | Declara exactamente los métodos, rutas y headers públicos de la API remota. |
| `data/remote/dto/Ec2ParkingDto.kt` | Define los DTOs de lectura, creación y modificación del contrato HTTP. |
| `data/remote/client/Ec2RetrofitClient.kt` | Construye Retrofit con `BuildConfig.EC2_API_BASE_URL`; puede apuntar al BFF al integrar el backend, sin cambiar su ubicación arquitectónica. |
| `di/NetworkModule.kt` | Proporciona `Ec2ParkingApiService` mediante Hilt. |
| `data/mapper/ParkingMapper.kt` | Convierte el DTO remoto a la representación local y al modelo de dominio. |
| `data/local/datasource/ParkingLocalDataSource.kt`, `data/local/dao/ParkingDao.kt` | Guardan y observan la caché remota en Room. |
| `data/local/entity/ParkingEntity.kt` | Guarda IDs locales, `remoteId`, `revision`, `remoteUpdatedAt` y `lastSyncedAt`. |
| `ui/incidents/IncidentViewModel.kt` | Gestiona actualmente el registro y cierre **locales** de incidencias; no debe describirse como integrado al MS todavía. |
| `data/repository/IncidentRepositoryImpl.kt` | Usa `IncidentLocalDataSource` actualmente; se extenderá dentro de la arquitectura definida cuando exista el contrato remoto. |
| `data/remote/datasource/IncidentRemoteDataSource.kt` | Ruta prevista en el informe para integrar el MS Incidencias, **sin dar por hecha su implementación**. |
| `auth/CognitoAuthManager.kt` | Gestiona autenticación Cognito y entrega los tokens a la sesión Android; el backend no lo sustituye. |

**Advertencia sobre nombres:** se conservan `Ec2ParkingApiService` y `Ec2RetrofitClient` durante la integración por compatibilidad. Su nombre no obliga a utilizar EC2: lo relevante será la URL que se configure. No se renombrarán automáticamente clases que ya funcionan.

## 13.10 Ejemplo completo: consultar estacionamientos

1. La UI `Ec2ParkingsSection.kt` solicita actualizar mediante `HomeViewModel.refreshRemote()`.
2. `HomeViewModel` llama a `ParkingRepository.refreshRemote(token)` a través de su contrato de dominio.
3. `ParkingRepositoryImpl` utiliza `ParkingRemoteDataSourceImpl`, que llama a `Ec2ParkingApiService.getParkings(...)`.
4. Retrofit envía `GET /api/parkings` al **BFF** con `Authorization: Bearer <access_token>`.
5. `ParkingBffController` delega en su `ParkingClient`, implementado con `RestClient`, que consulta `ParkingController` en el MS `parking`.
6. `ParkingController → ParkingService → ParkingServiceImpl → ParkingRepository JPA` consulta la base de datos del servidor.
7. La respuesta retorna como JSON; Android la convierte a `Ec2ParkingDto`, la guarda en caché Room por `ParkingMapper`/`ParkingLocalDataSource` y `HomeViewModel` recibe los cambios observados.
8. Jetpack Compose actualiza la lista al recolectar el `StateFlow` sin consultar directamente el servidor ni la base de datos.

```text
Ec2ParkingsSection → HomeViewModel → ParkingRepository (Android)
 → ParkingRepositoryImpl → ParkingRemoteDataSource → Ec2ParkingApiService
 → Retrofit [HTTP] → ParkingBffController → ParkingClientImpl [RestClient]
 → ParkingController → ParkingService → ParkingServiceImpl
 → ParkingRepository (JPA) → PostgreSQL
 ← JSON ← Ec2ParkingDto ← ParkingMapper → Room/Flow → HomeUiState → UI
```

## 13.11 Ejemplo completo: crear, modificar y eliminar estacionamientos

El contrato HTTP **ya definido por Android** es:

| HTTP | Ruta pública | Cuerpo/encabezado | Resultado |
| --- | --- | --- | --- |
| `GET` | `/api/parkings` | Header `Authorization` | Lista de `Ec2ParkingDto`. |
| `POST` | `/api/parkings` | `Ec2ParkingCreateDto` | `Ec2ParkingDto` creado. |
| `PUT` | `/api/parkings/{id}` | `Ec2ParkingPatchDto` con `revision` | `Ec2ParkingDto` actualizado. |
| `DELETE` | `/api/parkings/{id}` | Header `If-Match` con la revisión | Respuesta sin cuerpo obligatorio. |

**Campos al crear:** `name`, `address`, `commune`, `totalSpaces`, `freeSpaces`, `isActive`. **Campos al modificar actualmente:** `revision` y, opcionalmente, `name` o `isActive`. Todas las solicitudes llevan un access token en `Authorization`.

Ejemplo de edición:

```http
PUT /api/parkings/15
Authorization: Bearer <access_token>
Content-Type: application/json

{"revision":3,"name":"Estacionamiento Central"}
```

La revisión es necesaria para evitar sobrescribir modificaciones de otro usuario. Si el registro cambió desde que Android lo consultó, el backend debe responder un error de conflicto de forma controlada; BFF y Retrofit no deben interpretar el fallo como éxito. El número de revisión, los códigos HTTP precisos y el mecanismo de concurrencia deben concordar entre todos los componentes.

Al completar un CRUD remoto, `ParkingRepositoryImpl` llama a `refreshRemote(...)` y actualiza la caché. **Un estacionamiento almacenado solo localmente no se convierte automáticamente en remoto**; esa sincronización requiere reglas específicas. Para no confundir identificadores, el modelo Android contiene `id` (SQLite) y `remoteId` (servidor). Actualmente el mapper asigna a la caché de estacionamientos remotos un ID local negativo derivado del ID remoto, para separarlo de los IDs positivos locales.

## 13.12 Ejemplo previsto: registrar una incidencia

El registro de incidencias en la aplicación actual funciona mediante **Room local**. Para conectarlo con Spring Boot se acordará antes un contrato de creación y una política de sincronización; no sustituiremos sin pruebas el flujo existente.

Flujo de integración previsto:

1. `IncidentScreen` solicita la creación a `IncidentViewModel` mediante un formulario validado.
2. `IncidentViewModel` llama a `IncidentRepository` (Android). `IncidentRepositoryImpl` coordinará la política de almacenamiento local/remoto definida.
3. `IncidentRemoteDataSource` utilizará Retrofit para enviar el DTO acordado a `POST /api/incidents` del BFF.
4. `IncidentBffController → IncidentClientImpl` enviará la solicitud al MS `incidents`.
5. `IncidentController → IncidentService → IncidentServiceImpl` validará los datos y, si corresponde, consultará `ParkingClientImpl → ParkingController` para comprobar `parkingId`.
6. `IncidentRepository` JPA persistirá `IncidentEntity` **en la base de datos de Incidencias** y devolverá un DTO con el resultado.
7. Android actualizará su estado y persistencia conforme a la política de sincronización elegida; los errores del servidor no deberán borrar un registro local exitoso.

**Atención a los identificadores:** un `parkingId` que identifica una fila local Room no equivale automáticamente al `id` del servidor. Antes de enviar una incidencia asociada a un parking se verificará su identidad remota. Lo mismo aplica a `serviceId`; para este recurso todavía no está definido el contrato remoto y no debe inventarse una traducción arbitraria.

**Evidencias:** `IncidentViewModel` guarda actualmente los archivos seleccionados en almacenamiento privado del dispositivo y conserva una URI local. Esa URI **no permite** que otros usuarios descarguen la evidencia. Una incidencia remota con fotografías o vídeos necesitará un contrato de carga de archivos y almacenamiento en el backend o en un servicio de objetos, con autorización y límites de tamaño. Esto debe diseñarse antes de afirmar soporte remoto para adjuntos.

La eliminación física de incidentes podría contradecir requisitos de trazabilidad. Debemos definir si se usa borrado lógico, cancelación o permisos especiales antes de fijar un endpoint `DELETE`.

## 13.13 Contrato inicial previsto para Incidencias

Estas rutas son **propuestas**, a diferencia de las rutas de parking verificadas en el código Android:

| HTTP | Ruta pública propuesta | Función prevista |
| --- | --- | --- |
| `GET` | `/api/incidents` | Listar registros autorizados. |
| `GET` | `/api/incidents/{id}` | Consultar detalle. |
| `POST` | `/api/incidents` | Crear un incidente. |
| `PUT` | `/api/incidents/{id}` | Actualizar campos, estado o resolución según reglas. |
| `DELETE` | `/api/incidents/{id}` | **Opcional** y sujeto a trazabilidad/permisos. |

La estructura de `IncidentDto.java`, estados permitidos, tratamiento de evidencia, permisos y manejo de la relación con servicios se concretarán a partir de los requerimientos de SABA y sus clases Android, no por analogía automática con otras aplicaciones.

## 13.14 Autenticación, autorización y seguridad

El usuario se autentica en la pantalla Android mediante **Amazon Cognito con Authorization Code + PKCE**. La app obtiene un access token; al llamar al BFF envía `Authorization: Bearer <token>`.

- **BFF:** valida firma, emisor, expiración y demás claims relevantes; comprueba permisos de acceso a rutas antes de delegar.
- **Microservicios:** no confían ciegamente en el BFF; también validan y autorizan el token si aceptan solicitudes protegidas. Cuando el BFF delega, transmite el token de usuario de manera segura según el contrato definido.
- **Spring Security OAuth2 Resource Server:** mecanismo previsto para validar los JWT de Cognito con claves públicas publicadas por el proveedor. Debe configurarse para el User Pool y el tipo de token correctos (access token, no ID token).
- **Android:** conserva su gestor de sesión. `JwtDecoder.kt` permite leer claims, **pero decodificar un JWT no equivale a verificar su firma**; la validación de servidor es indispensable.

No se crea un servicio de login Spring Boot ni se almacenan contraseñas Cognito en PostgreSQL. Las URL del emisor y de los servicios son configuración; ninguna clave privada ni token se subirá a GitHub o quedará escrita en el código de los proyectos.

## 13.15 Bases de datos, entidades y consistencia

**PostgreSQL + Spring Data JPA** son la persistencia prevista del backend. Cada servicio posee su base de datos o esquema exclusivo; Flyway podrá aplicar migraciones SQL versionadas. El BFF no necesita Entity JPA ni Repository JPA para delegar solicitudes.

**Room / SQLite** permanece dentro de Android. Sus entidades no se comparten con JPA. La existencia de `ParkingEntity.kt` y `ParkingEntity.java` es correcta porque tienen almacenamiento, anotaciones y ciclo de vida distintos.

Definiciones pendientes que deben resolverse antes de programar la sincronización:

- Identidad local frente a remota (`id`/`remoteId`) y asociaciones de incidencias con estacionamientos y servicios.
- Política de actualización de caché, tolerancia a red intermitente y conflictos de modificación.
- Comportamiento ante eliminación o desactivación de un estacionamiento referenciado por incidentes.
- Concurrencia por `revision` para `PUT` y `DELETE`.
- Almacenamiento de adjuntos y preservación de trazabilidad.

**La caché de lectura no es una cola de escrituras offline.** No se afirmará sincronización bidireccional hasta implementarla y probarla.

## 13.16 Errores, resiliencia y configuración

| Situación | Comportamiento previsto |
| --- | --- |
| Consulta exitosa | `200 OK` y DTO tipado. |
| Creación exitosa | `201 Created` y DTO creado, según contrato. |
| Cuerpo inválido | `400 Bad Request` con mensaje controlado. |
| Token faltante o inválido | `401 Unauthorized`. |
| Usuario sin autorización | `403 Forbidden`. |
| ID inexistente | `404 Not Found`. |
| Revisión obsoleta | `409 Conflict` o `412 Precondition Failed`, según contrato final. |
| MS dependiente caído | Error de integración controlado (`502`/`503`/`504` según la causa); no se devuelve éxito falso. |
| Red de Android caída | Mostrar mensaje y conservar la caché Room cuando corresponda. |

Las excepciones internas se traducen en `exception/` y no deben exponer stack traces ni información sensible al cliente. Los `RestClient` necesitarán tiempos de espera definidos y tratamiento explícito de fallos de conexión.

**Configuración:** cada proyecto Java tiene su propio puerto y `application.properties`/`.yml`; las URLs internas y credenciales de base de datos se externalizan. El backend se trabajará en **Apache NetBeans, Java 26 y Gradle/Groovy**, sujetos a compatibilidad con las versiones instaladas. Android se mantiene en Android Studio con su configuración propia.

No se incorporan Swagger/OpenAPI, tracing distribuido ni monitoreo adicional como requisitos de esta versión, salvo decisión posterior explícita.

## 13.17 Pruebas técnicas y criterios de aceptación

| Nivel | Qué debe probarse |
| --- | --- |
| `ParkingServiceImpl` | CRUD, validación de datos, revisión concurrente, inexistencia de recurso. |
| `IncidentServiceImpl` | Reglas de negocio, estados, referencias remotas, fallos del `ParkingClient`. |
| Controllers | Rutas, validaciones y códigos de respuesta con pruebas HTTP. |
| Repository JPA | Persistencia real, consultas y restricciones de integridad en BD de prueba. |
| Client `RestClient` | URL, serialización de DTO, cabeceras, propagación de JWT, timeouts y errores HTTP. |
| BFF | Delegación correcta al servicio destino, protección de rutas y fidelidad de contratos. |
| Android | Integración Retrofit, actualización de Room, estados del ViewModel y comportamiento sin red. |
| Extremo a extremo | Registro/consulta Android → BFF → MS → PostgreSQL → Android. |
| Seguridad | Rechazo de token vencido, alterado o insuficiente; ausencia de credenciales en repositorio. |

Las pruebas backend pueden utilizar JUnit, Mockito y JaCoCo. **La exigencia de cobertura ≥80% se refiere a la lógica de la aplicación móvil** y no queda automáticamente cubierta por ejecutar solamente pruebas de Spring Boot.

No se marcará una funcionalidad como completada por tener clases o endpoints escritos: debe arrancar, compilar, responder correctamente y superar sus casos de prueba.

## 13.18 Orden de implementación y puntos de control

| Paso | Trabajo | Verificación |
| --- | --- | --- |
| 1 | Confirmar el proyecto `springboot/parking` y su Gradle/JDK. | `bootRun` inicia. |
| 2 | Crear `ParkingEntity`, DTO, Repository, Service, ServiceImpl y Controller. | CRUD en MS parking con BD propia. |
| 3 | Implementar revisión, validaciones y excepciones. | `PUT`/`DELETE` respetan `revision` e informan errores. |
| 4 | Crear BFF con `ParkingBffController`, `ParkingClient` y `ParkingClientImpl`. | Las rutas públicas funcionan a través de BFF. |
| 5 | Integrar Cognito en seguridad del backend. | Las llamadas protegidas exigen access token válido. |
| 6 | Conectar Retrofit Android al BFF sin reestructurar `app/`. | CRUD remoto visible en SABA y caché Room actualizada. |
| 7 | Implementar MS Incidencias y su contrato. | Incidentes remotos con persistencia y reglas verificadas. |
| 8 | Agregar `IncidentClient` del BFF e interservicio con Parking solo si es necesario. | Registro y consulta desde SABA con respuestas coherentes. |
| 9 | Ejecutar pruebas y completar evidencias técnicas. | Build, casos HTTP, seguridad, cobertura y pruebas en el teléfono. |

## 13.19 Decisiones que requieren confirmación técnica

1. Puertos definitivos y URLs internas de `parking`, `incidents` y `bff`.
2. Esquemas PostgreSQL, versión, credenciales y migraciones Flyway.
3. Campos exactos de los DTO Java y serialización de `updatedAt` y `revision`.
4. Reglas de autorización por perfiles, recursos y acciones.
5. Identificación remota de `Service` y política de asociación `parkingId`/`serviceId` para incidencias.
6. Contrato de adjuntos remotos y política de retención/eliminación de incidentes.
7. Si Incidencias necesita validar estacionamientos por Client en línea y qué hacer ante la caída de ese servicio.
8. Si Home necesita una respuesta agregada del BFF; no se desarrollará sin un caso de uso.
9. La **API externa** es un requisito separado: deberá consumirse desde Android mediante Retrofit y mostrarse en UI; no se confunde con los Clients internos de Spring Boot.

**Regla para implementar código a partir de este documento:** tomar como fuente de verdad el contrato vigente de Android y esta especificación, revisar las clases reales antes de editar, conservar la estructura de la app y crear en Spring Boot únicamente las clases declaradas o justificadas por una responsabilidad imprescindible. Si una decisión aún aparece pendiente, no presentarla como implementada ni fijarla arbitrariamente sin documentar el cambio.
