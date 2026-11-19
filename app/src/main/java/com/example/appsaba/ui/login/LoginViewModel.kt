package com.example.appsaba.ui.login

import android.content.Intent
import com.example.appsaba.auth.CognitoAuthManager
import com.example.appsaba.auth.CognitoAuthResult
import kotlinx.coroutines.CancellationException
import androidx.lifecycle.ViewModel
import com.example.appsaba.BuildConfig
import androidx.lifecycle.viewModelScope
import com.example.appsaba.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val cognitoManager: CognitoAuthManager
) : ViewModel() {


    /** El ViewModel controla AppAuth/PKCE; la UI solamente abre el Intent. */
    fun startCognitoLogin() {
        if (_uiState.value.cognitoBusy) return
        _uiState.update { it.copy(cognitoBusy = true, cognitoError = null, cognitoTokens = null) }
        viewModelScope.launch {
            try {
                val intent = cognitoManager.buildAuthIntent()
                _uiState.update { it.copy(pendingCognitoIntent = intent) }
            } catch (cancelled: CancellationException) {
                _uiState.update { it.copy(cognitoBusy = false) }
                throw cancelled
            } catch (error: Exception) {
                _uiState.update { it.copy(cognitoBusy = false, cognitoError = "Cognito: ${error.message ?: "No se pudo iniciar"}") }
            }
        }
    }

    fun onCognitoIntentOpened() {
        _uiState.update { it.copy(pendingCognitoIntent = null) }
    }

    fun onCognitoResponse(data: Intent?) {
        try {
            cognitoManager.exchangeCode(data) { authResult ->
                when (authResult) {
                    is CognitoAuthResult.Success -> {
                        val tokens = authResult.tokens
                        _uiState.update { state ->
                            if (tokens.idToken.isNullOrBlank() || tokens.accessToken.isNullOrBlank()) {
                                state.copy(cognitoBusy = false, cognitoError = "Cognito no devolvio los tokens requeridos")
                            } else state.copy(cognitoBusy = false, cognitoTokens = tokens)
                        }
                    }
                    is CognitoAuthResult.Failure ->
                        _uiState.update { it.copy(cognitoBusy = false, cognitoError = authResult.message) }
                    CognitoAuthResult.Cancelled ->
                        _uiState.update { it.copy(cognitoBusy = false, cognitoError = "Inicio de sesion cancelado") }
                }
            }
        } catch (error: Exception) {
            _uiState.update { it.copy(cognitoBusy = false, cognitoError = error.message ?: "Error de Cognito") }
        }
    }

    fun onCognitoConsumed() {
        _uiState.update { it.copy(cognitoTokens = null, cognitoError = null) }
    }

    fun onCognitoLaunchError(message: String) {
        _uiState.update { it.copy(cognitoBusy = false, pendingCognitoIntent = null, cognitoError = message) }
    }

    // CognitoAuthManager lo proporciona AuthModule como @Singleton.
    // No debe cerrarse aquí: otra instancia del LoginViewModel podría reutilizar
    // el mismo administrador tras cerrar sesión o recrear la Activity.

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
                    val token = response.token.trim()
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

    // El resultado del login local ya fue entregado a MainActivity.
    // Limpiarlo impide que al volver a Login tras el logout,
    // LaunchedEffect vuelva a enviar el token y reingrese a Home.
    fun onLoginConsumed() {
        _uiState.update {
            it.copy(isSuccess = false, authToken = null, password = "")
        }
    }

    // onErrorShown = la Screen lo llama tras mostrar el Snackbar para limpiar loginError y no repetirlo.
    fun onErrorShown() {
        _uiState.update { it.copy(loginError = null) }
    }
}