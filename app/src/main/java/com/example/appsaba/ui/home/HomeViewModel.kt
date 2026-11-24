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
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
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
    private var activeRemoteToken: String? = null

    init {
        viewModelScope.launch {
            parkings.observeRemote().collect { cached ->
                _uiState.update { it.copy(remoteParkings = cached) }
            }
        }
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

    fun setRemoteToken(token: String?) {
        if (token == activeRemoteToken) return
        activeRemoteToken = token
        if (!token.isNullOrBlank()) remoteAction(token) { parkings.refreshRemote(it) }
    }

    private fun remoteAction(token: String?, operation: suspend (String) -> Unit) {
        if (token.isNullOrBlank() || _uiState.value.remoteBusy) return
        _uiState.update { it.copy(remoteBusy = true, remoteFeedback = "Conectando con BFF...") }
        viewModelScope.launch {
            try {
                operation(token)
                _uiState.update { it.copy(remoteFeedback = "Sincronizacion correcta con BFF") }
            } catch (cancelled: CancellationException) { throw cancelled
            } catch (error: HttpException) {
                val msg = when (error.code()) {
                    401 -> "Autenticacion rechazada (HTTP 401). Comprueba Cognito en el BFF y los microservicios."
                    409 -> "Conflicto de revision: actualiza antes de editar."
                    else -> "HTTP ${error.code()} al conectar con BFF"
                }
                _uiState.update { it.copy(remoteFeedback = msg) }
            } catch (error: Exception) {
                _uiState.update { it.copy(remoteFeedback = "BFF no disponible. Cache Room conservada: ${error.localizedMessage}") }
            } finally {
                _uiState.update { it.copy(remoteBusy = false) }
            }
        }
    }

    fun refreshRemote() = remoteAction(activeRemoteToken) { parkings.refreshRemote(it) }
    fun createRemote(name: String) = remoteAction(activeRemoteToken) { parkings.createRemote(it, name) }
    fun renameRemote(parking: com.example.appsaba.domain.model.Parking, name: String) =
        remoteAction(activeRemoteToken) { parkings.renameRemote(it, parking, name) }
    fun toggleRemote(parking: com.example.appsaba.domain.model.Parking) =
        remoteAction(activeRemoteToken) { parkings.toggleRemote(it, parking) }
    fun deleteRemote(parking: com.example.appsaba.domain.model.Parking) =
        remoteAction(activeRemoteToken) { parkings.deleteRemote(it, parking) }

    fun selectParking(id: Long) {
        selectedParkingId.value = id
        _uiState.update { it.copy(selectedParkingId = id) }
    }
}

