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


    fun login() {

        val current = _uiState.value

        val rutError = if (current.rut.isBlank()) "Ingresa tu RUT" else null

        val passwordError = if (current.password.isBlank()) "Ingresa tu contraseña" else null


        if (rutError != null || passwordError != null) {
            _uiState.update {
                it.copy(rutError = rutError, passwordError = passwordError)
            }

            return
        }


        // TEMPORAL:
        // mientras todavía no conectamos Retrofit/API

        _uiState.update {
            it.copy(
                isSuccess = true
            )
        }
    }
}