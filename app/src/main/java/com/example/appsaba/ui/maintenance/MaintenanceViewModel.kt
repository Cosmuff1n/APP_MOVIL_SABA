package com.example.appsaba.ui.maintenance

import androidx.lifecycle.viewModelScope
import com.example.appsaba.domain.model.Service
import com.example.appsaba.domain.repository.ParkingRepository
import com.example.appsaba.domain.repository.ServiceRepository
import com.example.appsaba.ui.common.ActionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MaintenanceViewModel @Inject constructor(
    parkingRepository: ParkingRepository,
    private val repository: ServiceRepository
) : ActionViewModel() {
    private val selected = MutableStateFlow<Long?>(null)
    private val observedServices = selected.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.observeByParking(id)
    }
    val uiState: StateFlow<MaintenanceUiState> = combine(
        parkingRepository.observeAll(), selected, observedServices, status
    ) { parkings, id, services, action ->
        MaintenanceUiState(parkings, id, services.filter { it.parkingId == id }, action.busy, action.feedback)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MaintenanceUiState())

    fun selectParking(id: Long?) { selected.value = id }
    fun saveService(id: Long?, parkingId: Long?, name: String, type: String, detail: String) {
        if (parkingId == null || name.isBlank() || type.isBlank()) {
            reject("Selecciona un estacionamiento e indica nombre y tipo.")
            return
        }
        execute("Equipo guardado en SQLite") {
            if (id == null) repository.insert(Service(parkingId = parkingId, name = name.trim(), type = type.trim(), detail = detail.trim()))
            else {
                val previous = requireNotNull(repository.getById(id)) { "Equipo no encontrado" }
                repository.update(previous.copy(name = name.trim(), type = type.trim(), detail = detail.trim()))
            }
        }
    }
    fun setServiceStatus(service: Service, newStatus: String) {
        if (newStatus !in setOf("operativo", "degradado", "fuera-servicio")) {
            reject("Estado de equipo no válido")
            return
        }
        execute("Estado del equipo guardado") {
            repository.updateStatus(service.id, newStatus,
                if (newStatus == "operativo") null else service.problem)
        }
    }
    fun deleteService(service: Service) = execute("Equipo eliminado") { repository.delete(service) }
}
