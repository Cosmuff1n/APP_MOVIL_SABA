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
    viewModel: LoginViewModel = viewModel(), onLoginSuccess: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = state.rut, onValueChange = {
            viewModel.onRutChange(it)
        }, label = {
            Text("RUT")
        })

        OutlinedTextField(
            value = state.password, onValueChange = {
                viewModel.onPasswordChange(it)
            }, label = {
                Text("Contraseña")
            }, visualTransformation = PasswordVisualTransformation()
        )

        Button(onClick = {
            viewModel.login()
        }) {
            Text("Ingresar")
        }
    }
}