package com.example.appsaba.ui.state

data class LoginUiState(
    val rut: String = "",
    val password: String = "",
    val selectedParking: String = "Parking Plaza Italia",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val rutError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isSuccess: Boolean = false
) {
    // El botón "Ingresar" se habilita solo si hay datos y no se está cargando.
    val canSubmit: Boolean
        get() = rut.isNotBlank() && password.isNotBlank() && !isLoading
}
