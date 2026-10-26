package com.example.appsaba.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appsaba.data.remote.Ec2ParkingDto
import com.example.appsaba.data.remote.Ec2RetrofitClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Primera prueba real Android -> Nginx -> Express -> SQLite (solo GET). */
@Composable
fun Ec2ParkingsSection() {
    var parkings by remember { mutableStateOf<List<Ec2ParkingDto>>(emptyList()) }
    var message by remember { mutableStateOf("Consultando EC2...") }
    val scope = rememberCoroutineScope()

    suspend fun refresh() {
        message = "Consultando EC2..."
        try {
            parkings = Ec2RetrofitClient.api.getParkings()
            message = if (parkings.isEmpty()) "Sin estacionamientos en EC2" else "Conectado a EC2"
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            message = "No se pudo conectar con EC2: ${error.localizedMessage ?: "Error de red"}"
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Estacionamientos en EC2 (prueba)", style = MaterialTheme.typography.titleMedium)
        Text(message, style = MaterialTheme.typography.bodySmall)
        parkings.forEach { parking ->
            Text("#${parking.id} · ${parking.name} — ${parking.status}")
        }
        OutlinedButton(onClick = { scope.launch { refresh() } }) {
            Text("Actualizar desde EC2")
        }
        Text(
            "Estos datos vienen del servidor; no sustituyen los estacionamientos locales de Room.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
