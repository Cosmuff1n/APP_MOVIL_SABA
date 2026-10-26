package com.example.appsaba.ui.login

data class LoginUiState(
    val rut: String = "",
    val password: String = "",
    val selectedParking: String = "Parking Plaza Italia",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val rutError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isSuccess: Boolean = false,
    val authToken: String? = null // <- agregado 02: guarda el access_token del 200
) {
    val canSubmit: Boolean
        get() = !isLoading
}