package com.example.appsaba.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import com.example.appsaba.BuildConfig
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appsaba.ui.components.AuthCard
import com.example.appsaba.ui.theme.AppSabaTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.cognitoauth.CognitoAuthManager
import com.cognitoauth.CognitoAuthResult
import com.example.appsaba.config.SabaCognitoConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (token: String) -> Unit = {},
    onCognitoLoginSuccess: (token: String) -> Unit = onLoginSuccess
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Instancia reutilizable de AppAuth durante la vida de esta pantalla.
    val cognitoManager = remember(context) {
        CognitoAuthManager(context.applicationContext, SabaCognitoConfig.create())
    }
    DisposableEffect(cognitoManager) {
        onDispose { cognitoManager.dispose() }
    }

    var cognitoBusy by remember { mutableStateOf(false) }
    var cognitoMessage by remember { mutableStateOf<String?>(null) }

    val cognitoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        cognitoManager.exchangeCode(activityResult.data) { authResult ->
            scope.launch {
                cognitoBusy = false
                cognitoMessage = when (authResult) {
                    is CognitoAuthResult.Success -> {
                        val idToken = authResult.tokens.idToken
                        val accessToken = authResult.tokens.accessToken
                        if (!idToken.isNullOrBlank() && !accessToken.isNullOrBlank()) {
                            // El ID Token identifica al usuario en Home.
                            // El Access Token se usará después para nuestra API remota.
                            onCognitoLoginSuccess(idToken)
                            null
                        } else {
                            "Cognito respondió, pero faltan tokens esperados."
                        }
                    }
                    is CognitoAuthResult.Failure ->
                        "Error de Cognito: ${authResult.message}"
                    CognitoAuthResult.Cancelled -> "Inicio de sesión cancelado."
                }
            }
        }
    }

    LoginContent(
        state = state,
        onRutChange = viewModel::onRutChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePassword = viewModel::onTogglePasswordVisibility,
        onLoginClick = viewModel::onLoginClick,
        onErrorShown = viewModel::onErrorShown,
        // Consumimos el éxito solo del login local antes de navegar.
        // Cognito utiliza onCognitoLoginSuccess por separado.
        onLoginSuccess = { t ->
            viewModel.onLoginConsumed()
            onLoginSuccess(t)
        },
        onCognitoClick = {
            if (!cognitoBusy) {
                cognitoBusy = true
                cognitoMessage = null
                scope.launch {
                    try {
                        cognitoLauncher.launch(cognitoManager.buildAuthIntent())
                    } catch (cancelled: CancellationException) {
                        cognitoBusy = false
                        throw cancelled
                    } catch (error: Exception) {
                        cognitoBusy = false
                        cognitoMessage = "No se pudo abrir Cognito: ${error.message ?: "Error desconocido"}"
                    }
                }
            }
        },
        cognitoBusy = cognitoBusy,
        cognitoMessage = cognitoMessage
    )
}

@Composable
fun LoginContent(
    state: LoginUiState,
    onRutChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLoginClick: () -> Unit,
    onErrorShown: () -> Unit,
    onLoginSuccess: (token: String) -> Unit = {},
    onCognitoClick: (() -> Unit)? = null,
    cognitoBusy: Boolean = false,
    cognitoMessage: String? = null
) {
    LaunchedEffect(state.isSuccess, state.authToken) {
        val token = state.authToken
        if (state.isSuccess && !token.isNullOrBlank()) onLoginSuccess(token)
    }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.loginError) {
        state.loginError?.let { message ->
            snackbar.showSnackbar(message)
            onErrorShown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthCard(
            title = "App Saba",
            subtitle = "Inicia sesión para acceder a tu estacionamiento"
        ) {
            if (BuildConfig.DEBUG) {
                Text(
                    "MODO PRUEBA (sin conexión a Internet)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "RUT: 12.345.678-5   |   Clave: Password123",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = state.rut,
                onValueChange = onRutChange,
                label = { Text("RUT") },
                singleLine = true,
                isError = state.rutError != null,
                supportingText = { state.rutError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                isError = state.passwordError != null,
                supportingText = { state.passwordError?.let { Text(it) } },
                visualTransformation = if (state.isPasswordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (state.isPasswordVisible)
                                Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (state.isPasswordVisible)
                                "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onLoginClick() }),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onLoginClick,
                enabled = state.canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.height(24.dp)
                    )
                } else {
                    Text("Ingresar a terreno")
                }
            }

            // Prueba real de Cognito; se mantiene independiente del login simulado.
            if (onCognitoClick != null) {
                OutlinedButton(
                    onClick = onCognitoClick,
                    enabled = !cognitoBusy && !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    if (cognitoBusy) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.height(24.dp)
                        )
                    } else {
                        Text("Probar inicio de sesión con Cognito")
                    }
                }
                cognitoMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }

        SnackbarHost(hostState = snackbar)
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    AppSabaTheme {
        LoginContent(
            state = LoginUiState(rut = "12.345.678-5", password = "Password123"),
            onRutChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onLoginClick = {},
            onErrorShown = {}
        )
    }
}
