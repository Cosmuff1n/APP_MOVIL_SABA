package com.example.appsaba.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appsaba.ui.viewmodel.LoginViewModel


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    LoginContent(
        state = state,
        onRutChange = viewModel::onRutChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePassword = viewModel::onTogglePasswordVisibility,
        onLoginClick = viewModel::onLoginClick,
        onErrorShown = viewModel::onErrorShown,
        onLoginSuccess = onLoginSuccess
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
    onLoginSuccess: () -> Unit = {}
) {

    val snackbar = remember { SnackbarHostState() }


    LaunchedEffect(state.loginError) {
        state.loginError?.let {
            snackbar.showSnackbar(it)
            onErrorShown()
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthCard(
            title = "SyncPass",
            subtitle = "Inicia sesión para acreditar asistencia"
        ) {
            OutlinedTextField(
                value = state.rut,
                onValueChange = onRutChange,
                label = { Text("Rut o correo") }, // label flotante M3, no placeholder en duro
                singleLine = true,
                isError = state.rutError != null,
                supportingText = { state.rutError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyb-oardType = KeyboardType.Email, // teclado con @ para una mano en terreno
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth() // fillMaxWidth dentro del ColumnScope del Card
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                isError = state.passwordError != null,
                supportingText = { state.passwordError?.let { Text(it) } },
                visualTransformation = if (state.isPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(), // bullets •
                trailingIcon = {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (state.isPasswordVisible)
                                Icons.Filled.VisibilityOff
                            else
                                Icons.Filled.Visibility,
                            contentDescription = if (state.isPasswordVisible)
                                "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onLoginClick() }
                ),
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
                    // Progress dentro del botón con color onPrimary para contraste sobre el primario M3.
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Ingresar")
                }
            }
        }

        SnackbarHost(hostState = snackbar)
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    SyncPassTheme {
        LoginContent(
            state = LoginUiState(username = "juan_perez", password = "Password123"),
            onRutChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onLoginClick = {},
            onErrorShown = {}
        )
    }
}