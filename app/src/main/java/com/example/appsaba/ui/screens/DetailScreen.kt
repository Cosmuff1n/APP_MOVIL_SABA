package com.example.appsaba.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.appsaba.ui.components.PlaceholderScreen

@Composable
fun DetailScreen(serviceId: String, onBack: () -> Unit) {
    // Convierte el id corto en el nombre que se muestra.
    val name = when (serviceId) {
        "barriers" -> "Barreras de entrada"
        "lpr" -> "Lectura LPR"
        "pos" -> "Terminales POS"
        "atm2" -> "Cajero automático #2"
        else -> "Servicio desconocido"
    }

    PlaceholderScreen("Detalle: $name (temporal)") {
        Button(onClick = onBack) { Text("Volver a estado operacional") }
    }
}
