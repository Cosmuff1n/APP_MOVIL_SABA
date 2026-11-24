package com.example.appsaba.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/** La pantalla no conoce DAO ni Retrofit: unicamente emite eventos al ViewModel. */
@Composable
fun Ec2ParkingsSection(accessToken: String?) {
    val model: HomeViewModel = viewModel()
    val state by model.uiState.collectAsState()
    val cached = state.remoteParkings
    val busy = state.remoteBusy
    var name by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Long?>(null) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(accessToken) { model.setRemoteToken(accessToken) }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("BFF + Room (cache integrada)", style = MaterialTheme.typography.titleMedium)
        Text(state.remoteFeedback, style = MaterialTheme.typography.bodySmall)
        if (accessToken.isNullOrBlank()) {
            Text("Para operaciones remotas entra con Cognito. Room local sigue disponible.")
        } else {
            OutlinedButton(enabled = !busy, onClick = model::refreshRemote) { Text("Actualizar desde BFF") }
            OutlinedTextField(value = name, onValueChange = { name = it },
                label = { Text(if (editingId == null) "Nuevo estacionamiento BFF" else "Nuevo nombre") },
                modifier = Modifier.fillMaxWidth())
            Button(enabled = !busy && name.isNotBlank(), onClick = {
                val row = cached.firstOrNull { it.id == editingId }
                if (row == null) model.createRemote(name) else model.renameRemote(row, name)
                name = ""; editingId = null
            }) { Text(if (editingId == null) "Crear en BFF" else "Guardar nombre en BFF") }
        }
        if (cached.isEmpty()) Text("Sin estacionamientos remotos en cache")
        cached.forEach { parking ->
            Text("#${parking.remoteId} · ${parking.name} · ${if (parking.isActive) "Activo" else "Inactivo"}")
            if (!accessToken.isNullOrBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(enabled = !busy, onClick = { editingId = parking.id; name = parking.name }) { Text("Editar") }
                    TextButton(enabled = !busy, onClick = { model.toggleRemote(parking) }) {
                        Text(if (parking.isActive) "Desactivar" else "Activar")
                    }
                    TextButton(enabled = !busy, onClick = { pendingDeleteId = parking.id }) { Text("Eliminar") }
                }
                if (pendingDeleteId == parking.id) {
                    Row {
                        Text("¿Eliminar en BFF?")
                        TextButton(enabled = !busy, onClick = {
                            pendingDeleteId = null; model.deleteRemote(parking)
                        }) { Text("Confirmar") }
                        TextButton(onClick = { pendingDeleteId = null }) { Text("Cancelar") }
                    }
                }
            }
        }
        Text("Los IDs remotos y locales son independientes; la cache se conserva sin Internet.",
            style = MaterialTheme.typography.bodySmall)
    }
}
