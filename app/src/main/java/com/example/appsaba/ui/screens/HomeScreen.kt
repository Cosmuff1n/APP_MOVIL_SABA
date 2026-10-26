package com.example.appsaba.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.appsaba.ui.components.PlaceholderScreen

@Composable
fun HomeScreen(onViewDetail: (String) -> Unit = {}) {
    PlaceholderScreen("Panel de operación (temporal)") {
        // Botón de prueba para la ruta con argumento (P11 del Figma).
        Button(onClick = { onViewDetail("atm2") }) {
            Text("Ver cajero automático #2")
        }
    }
}
