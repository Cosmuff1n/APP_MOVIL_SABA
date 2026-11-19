package com.example.appsaba.ui.login

import android.content.Intent
import com.example.appsaba.domain.model.Token

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
    val authToken: String? = null,
    val cognitoBusy: Boolean = false,
    val cognitoError: String? = null,
    val pendingCognitoIntent: Intent? = null,
    val cognitoTokens: Token? = null
) {
    val canSubmit: Boolean
        get() = !isLoading
}