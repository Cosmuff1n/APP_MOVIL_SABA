package com.example.appsaba.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsaba.core.jwt.JwtDecoder
import com.example.appsaba.domain.repository.ParkingRepository
import com.example.appsaba.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Aquí el token recibe los repositorios para leer estacionamientos y servicios desde Room.
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val parkings: ParkingRepository,
    private val services: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Id del parking elegido
    private val selectedParkingId = MutableStateFlow<Long?>(null)

    init {
        // 2) Lista de parkings activos. Room avisa solo cuando algo cambia (Flow).
        viewModelScope.launch {
            parkings.observeActive().collect { list ->
                val current = _uiState.value.selectedParkingId
                val chosen = current?.takeIf { id -> list.any { it.id == id } } ?: list.firstOrNull()?.id
                selectedParkingId.value = chosen
                _uiState.update { it.copy(parkings = list, selectedParkingId = chosen) }
            }
        }

        // 3) Servicios del parking elegido (barreras, LPR, POS, cajeros).
        viewModelScope.launch {
            selectedParkingId.flatMapLatest { id ->
                if (id == null) flowOf(emptyList())
                else services.observeByParking(id)
            }.collect { services ->
                _uiState.update { it.copy(services = services) }
            }
        }
    }

    /** El token sigue en MainActivity: Home solo lee sus claims para mostrarlos. */
    fun setSessionToken(token: String) {
        JwtDecoder.decode(token)
            .onSuccess { user -> _uiState.update { it.copy(user = user, decodeError = null) } }
            .onFailure { _uiState.update { it.copy(decodeError = "No se pudo leer la sesión") } }
    }

    fun selectParking(id: Long) {
        selectedParkingId.value = id
        _uiState.update { it.copy(selectedParkingId = id) }
    }
}

