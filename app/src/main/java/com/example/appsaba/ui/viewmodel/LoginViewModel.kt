package com.example.appsaba.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.appsaba.ui.state.LoginUiState

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onRutChange(rut: String) {
        uiState = uiState.copy(rut = rut)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun onParkingChange(parking: String) {
        uiState = uiState.copy(selectedParking = parking)
    }

    fun login(onSuccess: () -> Unit) {

        if (uiState.rut.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Todos los campos deben ser llenados")
            return
        }
        onSuccess()
    }
}