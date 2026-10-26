package com.example.appsaba.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appsaba.ui.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onRutChange(value: String) {
        _uiState.update {
            it.copy(rut = value, rutError = null, loginError = null)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(password = value, passwordError = null, loginError = null)
        }
    }

    fun onParkingChange(value: String) {
        _uiState.update {
            it.copy(selectedParking = value)
        }
    }

    // Muestra u oculta la contraseña (ícono del ojo).
    fun onTogglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    // Se llama después de mostrar el mensaje de error, para que no se repita.
    fun onErrorShown() {
        _uiState.update {
            it.copy(loginError = null)
        }
    }

    // Antes se llamaba login(); ahora onLoginClick para coincidir con LoginScreen.
    fun onLoginClick() {
        val current = _uiState.value

        val rutError = if (current.rut.isBlank()) "Ingresa tu RUT" else null
        val passwordError = if (current.password.isBlank()) "Ingresa tu contraseña" else null

        if (rutError != null || passwordError != null) {
            _uiState.update {
                it.copy(rutError = rutError, passwordError = passwordError)
            }
            return
        }

        // TEMPORAL: login simulado, mientras todavía no conectamos Retrofit/API.
        _uiState.update {
            it.copy(isSuccess = true)
        }
    }
}
