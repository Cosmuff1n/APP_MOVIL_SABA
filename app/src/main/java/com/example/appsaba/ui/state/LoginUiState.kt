package com.example.appsaba.ui.state

data class LoginUiState(
    val rut: String = "",
    val password: String = "",
    val selectedParking: String = "Parking Plaza Italia",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)