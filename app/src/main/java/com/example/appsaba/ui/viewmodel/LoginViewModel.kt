package com.example.appsaba.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.room.util.copy
import com.example.appsaba.ui.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onRutChange(rut: String) {
        _uiState.update{ uiState.copy(rut = rut) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update{ uiState.copy(password = password) }
    }

    fun onParkingChange(parking: String) {
        _uiState.update{ uiState.copy(selectedParking = parking) }
    }

    fun login() {

        val current = _uiState.value

        val rutError =
            if (current.rut.isBlank())
                "Ingresa tu RUT"
            else
                null

        val passwordError =
            if (current.password.isBlank())
                "Ingresa tu contraseña"
            else
                null

        if (rutError != null || passwordError != null) {

            _uiState.update {
                it.copy(
                    rutError = rutError,
                    passwordError = passwordError
                )
            }

            return
        }

        _uiState.update {
            it.copy(
                isSuccess = true
            )
        }
    }

    /*
    * fun login(onSuccess: () -> Unit) {
        val current = _uiState.value // foto actual para validar sin suscribirse
        val usernameError = if (current.rut.isBlank()) "Ingresa tu rut" else null
        val passwordError = if (current.password.length < 6) "Mínimo 6 caracteres" else null
        if (usernameError != null || passwordError != null) {
            // Hay errores de formulario: los publica y NO llama a red (return temprano).
            _uiState.update { it.copy(rutError = usernameError, passwordError = passwordError) }
            return
        }
        // Sin errores: marca loading para deshabilitar botón (canSubmit) y mostrar spinner.
        _uiState.update { it.copy(isLoading = true, loginError = null) }
        // viewModelScope.launch = corrutina atada al ViewModel: no bloquea UI y se cancela al salir.
        viewModelScope.launch {
            val result = repository.login(current.rut, current.password)
            result
                .onSuccess { response ->
                    // TODO paso siguiente: guardar response.access_token en DataStore/Room
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    // Mensaje genérico al usuario (no expone 401/timeout ni datos sensibles).
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = "No se pudo iniciar sesión. Revisa tus datos o tu conexión."
                        )
                    }
                }
        }
    }*/
}