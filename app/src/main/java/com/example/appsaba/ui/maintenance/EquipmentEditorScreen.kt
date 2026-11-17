package com.example.appsaba.ui.maintenance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appsaba.domain.model.Service

import com.example.appsaba.ui.data.RecordCard
import com.example.appsaba.ui.data.ConfirmDelete

private val types = listOf("barriers", "lpr", "pos", "atm")
private val statuses = listOf("operativo", "degradado", "fuera-servicio")

@Composable
internal fun EquipmentEditorScreen(services: List<Service>, parkingId: Long?, busy: Boolean,
                              model: MaintenanceViewModel) {
    var editing by remember(parkingId) { mutableStateOf<Service?>(null) }
    var name by remember(parkingId) { mutableStateOf("") }
    var type by remember(parkingId) { mutableStateOf(types.first()) }
    var detail by remember(parkingId) { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<Service?>(null) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("2. Equipos tecnológicos", style = MaterialTheme.typography.titleLarge)
        if (parkingId == null) {
            Text("Crea primero un estacionamiento en la primera pestaña.")
        } else {
            OutlinedTextField(value = name, onValueChange = { name = it },
                label = { Text("Nombre del equipo") }, singleLine = true,
                modifier = Modifier.fillMaxWidth())
            Text("Tipo", style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                types.forEach { choice ->
                    FilterChip(selected = type == choice, onClick = { type = choice },
                        label = { Text(choice.uppercase()) })
                }
            }
            OutlinedTextField(value = detail, onValueChange = { detail = it },
                label = { Text("Detalle / ubicación") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = !busy, onClick = {
                    model.saveService(editing?.id, parkingId, name, type, detail)
                }) { Text(if (editing == null) "Agregar equipo" else "Guardar equipo") }
                TextButton(onClick = { editing = null; name = ""; type = types.first(); detail = "" }) {
                    Text("Limpiar")
                }
            }
            Text("Equipos (${services.size})", style = MaterialTheme.typography.titleMedium)
            if (services.isEmpty()) Text("Todavía no existen equipos para este estacionamiento.")
            services.forEach { service ->
                RecordCard("${service.name} · ${service.type.uppercase()}") {
                    Text("Detalle: ${service.detail.ifBlank { "-" }}")
                    Text("Estado actual: ${service.status}")
                    Text("Cambiar estado:", style = MaterialTheme.typography.labelMedium)
                    // Filas separadas para pantallas pequeñas.
                    statuses.forEach { status ->
                        FilterChip(selected = service.status == status, enabled = !busy,
                            onClick = { model.setServiceStatus(service, status) },
                            label = { Text(status) })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {
                            editing = service; name = service.name; type = service.type; detail = service.detail
                        }) { Text("Editar") }
                        TextButton(enabled = !busy, onClick = { pendingDelete = service }) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
    pendingDelete?.let { service ->
        ConfirmDelete("el equipo '${service.name}'",
            onConfirm = { pendingDelete = null; model.deleteService(service) },
            onCancel = { pendingDelete = null })
    }
}
