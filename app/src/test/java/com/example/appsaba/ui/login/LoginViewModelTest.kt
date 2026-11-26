@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.appsaba.ui.login

import com.example.appsaba.auth.CognitoAuthManager
import com.example.appsaba.domain.model.LoginSession
import com.example.appsaba.domain.repository.LoginRepository
import com.example.appsaba.testing.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()
    private lateinit var repo: LoginRepository
    private lateinit var vm: LoginViewModel
    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        val cognito: CognitoAuthManager = mockk(relaxed = true)
        vm = LoginViewModel(repo, cognito)
    }

    @Test
    fun initialStateHasEmptyCredentials() {
        assertEquals("", vm.uiState.value.rut)
        assertEquals("", vm.uiState.value.password)
        assertFalse(vm.uiState.value.isSuccess)
    }

    @Test
    fun fieldEventsUpdateState() {
        vm.onRutChange("123"); vm.onPasswordChange("clave123")
        assertEquals("123", vm.uiState.value.rut)
        assertEquals("clave123", vm.uiState.value.password)
    }

    @Test
    fun passwordVisibilityCanBeToggledTwice() {
        vm.onTogglePasswordVisibility(); assertTrue(vm.uiState.value.isPasswordVisible)
        vm.onTogglePasswordVisibility(); assertFalse(vm.uiState.value.isPasswordVisible)
    }

    @Test
    fun emptyCredentialsShowValidationErrorsWithoutCallingApi() = runTest {
        vm.onLoginClick(); runCurrent()
        assertNotNull(vm.uiState.value.rutError)
        assertNotNull(vm.uiState.value.passwordError)
        coVerify(exactly = 0) { repo.login(any(), any()) }
    }

    @Test
    fun shortPasswordRejectedBeforeNetwork() = runTest {
        vm.onRutChange("123"); vm.onPasswordChange("abc"); vm.onLoginClick(); runCurrent()
        assertEquals("Mínimo 6 caracteres", vm.uiState.value.passwordError)
        coVerify(exactly = 0) { repo.login(any(), any()) }
    }

    @Test
    fun successfulLoginStoresToken() = runTest {
        coEvery {
            repo.login(
                "123",
                "clave123"
            )
        } returns Result.success(LoginSession("  access-token  "))
        vm.onRutChange("123"); vm.onPasswordChange("clave123"); vm.onLoginClick(); runCurrent()
        assertTrue(vm.uiState.value.isSuccess)
        assertEquals("access-token", vm.uiState.value.authToken)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun blankTokenIsNotAccepted() = runTest {
        coEvery { repo.login(any(), any()) } returns Result.success(LoginSession(" "))
        vm.onRutChange("123"); vm.onPasswordChange("clave123"); vm.onLoginClick(); runCurrent()
        assertFalse(vm.uiState.value.isSuccess)
        assertNotNull(vm.uiState.value.loginError)
    }

    @Test
    fun failedLoginSetsErrorAndClearsLoading() = runTest {
        coEvery {
            repo.login(
                any(),
                any()
            )
        } returns Result.failure(IllegalStateException("offline"))
        vm.onRutChange("123"); vm.onPasswordChange("clave123"); vm.onLoginClick(); runCurrent()
        assertFalse(vm.uiState.value.isLoading)
        assertFalse(vm.uiState.value.isSuccess)
        assertNotNull(vm.uiState.value.loginError)
    }

    @Test
    fun consumingSuccessResetsTokenAndPassword() = runTest {
        coEvery { repo.login(any(), any()) } returns Result.success(LoginSession("access"))
        vm.onRutChange("123"); vm.onPasswordChange("clave123"); vm.onLoginClick(); runCurrent()
        vm.onLoginConsumed()
        assertNull(vm.uiState.value.authToken)
        assertEquals("", vm.uiState.value.password)
        assertFalse(vm.uiState.value.isSuccess)
    }

    @Test
    fun editingCredentialsClearsErrors() {
        vm.onLoginClick()
        vm.onRutChange("123"); vm.onPasswordChange("clave123")
        assertNull(vm.uiState.value.rutError)
        assertNull(vm.uiState.value.passwordError)
    }

    @Test
    fun cognitoLaunchErrorIsVisibleAndClearable() {
        vm.onCognitoLaunchError("Falló Cognito")
        assertEquals("Falló Cognito", vm.uiState.value.cognitoError)
        vm.onCognitoConsumed(); assertNull(vm.uiState.value.cognitoError)
    }
}
