package com.example.appsaba.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appsaba.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit) {
    val state = viewModel.uiState

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = state.rut, onValueChange = {
            viewModel.onRutChange(it)
        }, label = {
            Text("RUT")
        }
        )

        OutlinedTextField(
            value = state.password, onValueChange = {
            viewModel.onPasswordChange(it)
        }, label = {
            Text("Contraseña")
        }, visualTransformation = PasswordVisualTransformation()
        )

        Button(onClick = {
            viewModel.login(onSuccess = onLoginSuccess)
        }) {
            Text("Ingresar a terreno")
        }
    }
}