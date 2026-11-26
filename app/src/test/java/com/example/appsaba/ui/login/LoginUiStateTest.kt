package com.example.appsaba.ui.login

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUiStateTest {
    @Test
    fun inicialmentePermiteIntentarIngresar() {
        assertTrue(LoginUiState().canSubmit)
    }

    @Test
    fun duranteLaCargaImpideIntentarIngresar() {
        assertFalse(LoginUiState(isLoading = true).canSubmit)
    }

    @Test
    fun alTerminarCargaSeHabilitaNuevamente() {
        assertTrue(LoginUiState(isLoading = true).copy(isLoading = false).canSubmit)
    }
}
