package com.example.appsaba.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appsaba.core.jwt.JwtDecoder
import com.example.appsaba.data.repository.SabaRepositories
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
class HomeViewModel(
    accessToken: String,
    private val repositories: SabaRepositories
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Id del parking elegido
    private val selectedParkingId = MutableStateFlow<Long?>(null)

    init {
        // 1) Sesión
        JwtDecoder.decode(accessToken)
            .onSuccess { user -> _uiState.update { it.copy(user = user) } }
            .onFailure { _uiState.update { it.copy(decodeError = "Sesión inválida. Vuelve a ingresar.") } }

        // 2) Lista de parkings activos. Room avisa solo cuando algo cambia (Flow).
        viewModelScope.launch {
            repositories.parkingRepository.observeActive().collect { list ->
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
                else repositories.serviceRepository.observeByParking(id)
            }.collect { services ->
                _uiState.update { it.copy(services = services) }
            }
        }
    }

    fun selectParking(id: Long) {
        selectedParkingId.value = id
        _uiState.update { it.copy(selectedParkingId = id) }
    }
}

class HomeViewModelFactory(
    private val accessToken: String,
    private val repositories: SabaRepositories
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        HomeViewModel(accessToken, repositories) as T
}