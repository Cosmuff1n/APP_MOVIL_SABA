package com.example.appsaba.ui.login

import androidx.lifecycle.ViewModel
import com.example.appsaba.BuildConfig
import com.example.appsaba.data.remote.FakeAuthApiService
import androidx.lifecycle.viewModelScope
import com.example.appsaba.data.remote.RetrofitClient
import com.example.appsaba.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class LoginViewModel(
    // repository por constructor con default = permite pasar un fake en tests sin tocar producción.
    private val repository: AuthRepository = AuthRepository(
        if (BuildConfig.DEBUG) FakeAuthApiService() else RetrofitClient.authApi
    )
) : ViewModel() {

    // CON guion (_uiState) = PRIVADO y MUTABLE: solo el ViewModel puede escribir.
    // MutableStateFlow guarda el LoginUiState actual y notifica a cada update.
    private val _uiState = MutableStateFlow(LoginUiState())
    // SIN guion (uiState) = PÚBLICO y SOLO LECTURA: la UI observa pero no puede mutar directo.
    // asStateFlow() expone la vista inmutable. Patrón backing property: escritura adentro, lectura afuera.
    // Así se cumple UDF: eventos suben (funciones onX), estado baja (uiState).
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // onRutChange = evento de cada tecla en el campo usuario.
    // Guarda el texto nuevo y LIMPIA rutError/loginError para que el error desaparezca al corregir.
    // Por qué update+copy (ver nota abajo): crea un estado NUEVO inmutable en vez de mutar el actual.
    fun onRutChange(value: String) {
        _uiState.update { it.copy(rut = value, rutError = null, loginError = null) }
    }

    // onPasswordChange = idem para clave. Limpia passwordError/loginError al escribir de nuevo.
    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, loginError = null) }
    }

    // onTogglePasswordVisibility = invierte el flag con !it.isPasswordVisible (toma el valor actual 'it').
    // Solo toca ese campo; copy conserva todo lo demás.
    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    // onLoginClick = valida y luego llama a red. Flujo: lee snapshot, valida, loading, request, success/error.
    fun onLoginClick() {
        val current = _uiState.value // foto actual para validar sin suscribirse
        if (current.isLoading) return
        val rutError = if (current.rut.isBlank()) "Ingresa tu RUT" else null
        val passwordError = if (current.password.length < 6) "Mínimo 6 caracteres" else null
        if (rutError != null || passwordError != null) {
            // Hay errores de formulario: los publica y NO llama a red (return temprano).
            _uiState.update { it.copy(rutError = rutError, passwordError = passwordError) }
            return
        }
        // Sin errores: marca loading para deshabilitar botón (canSubmit) y mostrar spinner.
        _uiState.update {
            it.copy(isLoading = true, loginError = null, isSuccess = false, authToken = null)
        }
        // viewModelScope.launch = corrutina atada al ViewModel: no bloquea UI y se cancela al salir.
        viewModelScope.launch {
            val result = repository.login(current.rut, current.password)
            result
                .onSuccess { response ->
                    // Antes: it.copy(isLoading = false, isSuccess = true)
                    // Ahora: además guarda el token para llevarlo a Home (memoria; DataStore va en 03).
                    val token = response.access_token.trim()
                    _uiState.update {
                        if (token.isBlank()) {
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                loginError = "El servidor no entregó un token.",
                                authToken = null
                            )
                        } else {
                            it.copy(isLoading = false, isSuccess = true, authToken = token)
                        }
                    }
                }
                .onFailure { error ->
                    val message = if (BuildConfig.DEBUG && error is IllegalArgumentException) {
                        "RUT o contraseña de prueba incorrectos"
                    } else {
                        "No se pudo iniciar sesión. Revisa tus datos o tu conexión."
                    }
                    _uiState.update {
                        it.copy(isLoading = false, isSuccess = false, authToken = null,
                            loginError = message)
                    }
                }
        }
    }

    // onErrorShown = la Screen lo llama tras mostrar el Snackbar para limpiar loginError y no repetirlo.
    fun onErrorShown() {
        _uiState.update { it.copy(loginError = null) }
    }
}