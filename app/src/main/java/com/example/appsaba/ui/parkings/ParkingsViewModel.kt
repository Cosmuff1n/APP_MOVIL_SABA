package com.example.appsaba.ui.parkings

import androidx.lifecycle.viewModelScope
import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.repository.ParkingRepository
import com.example.appsaba.ui.common.ActionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class ParkingsViewModel @Inject constructor(
    private val repository: ParkingRepository
) : ActionViewModel() {
    val uiState: StateFlow<ParkingsUiState> = combine(repository.observeAll(), status) { list, action ->
        ParkingsUiState(list, action.busy, action.feedback)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ParkingsUiState())

    fun saveParking(id: Long?, name: String, address: String, commune: String, total: String, free: String) {
        val capacity = total.toIntOrNull()
        val available = free.toIntOrNull()
        if (name.isBlank() || address.isBlank() || capacity == null || available == null ||
            capacity < 0 || available < 0 || available > capacity) {
            reject("Completa nombre, dirección y plazas válidas (libres <= totales).")
            return
        }
        execute("Estacionamiento guardado en SQLite") {
            if (id == null) repository.insert(Parking(name = name.trim(), address = address.trim(),
                commune = commune.trim(), totalSpaces = capacity, freeSpaces = available))
            else {
                val previous = requireNotNull(repository.getById(id)) { "Estacionamiento no encontrado" }
                repository.update(previous.copy(name = name.trim(), address = address.trim(),
                    commune = commune.trim(), totalSpaces = capacity, freeSpaces = available))
            }
        }
    }
    fun toggleParking(parking: Parking) = execute("Estado del estacionamiento actualizado") {
        repository.update(parking.copy(isActive = !parking.isActive))
    }
    fun deleteParking(parking: Parking) = execute("Estacionamiento eliminado") { repository.delete(parking) }
}
