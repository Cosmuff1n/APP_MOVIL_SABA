# 02 — Home de Saba: dashboard operacional

> Prerrequisito: el flujo `login → home` ya funciona.
> Objetivo: convertir Home en un dashboard de estado operacional sin meter lógica de negocio dentro del Composable.

## 0. Qué debe mostrar Home

Parking de demostración actual:

```text
Parking Plaza Italia
```

Servicios iniciales:

```text
Barreras de entrada
Lectura LPR
Terminales POS
Cajero automático #2
```

Estados:

```text
OPERATIVO
DEGRADADO
FUERA_DE_SERVICIO
```

El primer Home puede seguir usando datos locales ficticios. **No necesitas esperar al backend para construir bien la UI.**

---

## Paso 1 — Modelo de estado operacional

Nuevo archivo sugerido: `data/model/ServiceStatus.kt`

```kotlin
package com.example.appsaba.data.model

enum class ServiceStatus {
    OPERATIVO,
    DEGRADADO,
    FUERA_DE_SERVICIO
}
```

`data/model/ParkingService.kt`:

```kotlin
package com.example.appsaba.data.model

data class ParkingService(
    val id: Long,
    val name: String,
    val status: ServiceStatus
)
```

### Por qué `enum class`

Porque el servicio solo debería tener uno de un conjunto cerrado de estados.

Evita errores como:

```text
"operativo"
"Operativo"
"OPERATIVOO"
```

---

## Paso 2 — Estado del Home

`ui/state/HomeUiState.kt`:

```kotlin
package com.example.appsaba.ui.state

import com.example.appsaba.data.model.ParkingService

data class HomeUiState(
    val parkingName: String = "Parking Plaza Italia",
    val services: List<ParkingService> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
```

---

## Paso 3 — HomeViewModel con datos ficticios primero

`ui/viewmodel/HomeViewModel.kt`:

```kotlin
package com.example.appsaba.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appsaba.data.model.ParkingService
import com.example.appsaba.data.model.ServiceStatus
import com.example.appsaba.ui.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            services = listOf(
                ParkingService(1, "Barreras de entrada", ServiceStatus.OPERATIVO),
                ParkingService(2, "Lectura LPR", ServiceStatus.DEGRADADO),
                ParkingService(3, "Terminales POS", ServiceStatus.OPERATIVO),
                ParkingService(4, "Cajero automático #2", ServiceStatus.FUERA_DE_SERVICIO)
            )
        )
    )

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
```

Esto permite construir el dashboard **antes** de conectar API.

---

## Paso 4 — HomeScreen observa el estado

Conceptualmente:

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    HomeContent(state = state)
}
```

La separación recomendada:

```text
HomeScreen
  └── conecta ViewModel

HomeContent
  └── dibuja state
```

Esto hace los previews más fáciles.

---

## Paso 5 — Cada servicio como componente reutilizable

Por ejemplo:

```kotlin
@Composable
fun ServiceStatusCard(
    service: ParkingService,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(service.name)
            Text(service.status.name)
        }
    }
}
```

Más adelante puedes mejorar colores/iconos. Primero asegúrate de que la estructura funcione.

---

## Paso 6 — Acciones desde un servicio

Al tocar un servicio, el diseño del MVP puede ofrecer:

```text
Ver detalle
Consultar guía
Reportar incidencia
Revisar mantenimiento
```

No necesitas implementar todas el mismo día.

Orden sugerido:

1. `Reportar incidencia` → conecta con `04_incidencias_api.md`.
2. `Mantenimiento` → conecta con `03_mantenimiento_room.md`.
3. `Guía` → inicialmente local.

---

## Paso 7 — Qué cambia cuando exista la API

Hoy:

```text
HomeViewModel
    ↓
listOf(...datos fake...)
```

Después:

```text
HomeViewModel
    ↓
ServiceRepository
    ↓
Retrofit
    ↓
GET /api/parkings/{id}/services
```

La pantalla puede permanecer casi igual porque solo observa `HomeUiState`.

Ese es el beneficio de separar la UI del origen de datos.

---

## Checklist 02

- [ ] Home sigue entrando desde `AppNavigation`.
- [ ] Muestra el parking actual.
- [ ] Muestra los cuatro servicios iniciales.
- [ ] Los estados usan un tipo cerrado (`enum class`).
- [ ] La lista vive en estado/ViewModel, no escrita directamente por toda la UI.
- [ ] Los datos de prueba son ficticios.
- [ ] La pantalla está preparada para cambiar de fake local a Repository/API sin reconstruirse completa.

## Siguiente

Una vez Home está estable, implementa un requisito de almacenamiento real:

```text
03_mantenimiento_room.md
```
