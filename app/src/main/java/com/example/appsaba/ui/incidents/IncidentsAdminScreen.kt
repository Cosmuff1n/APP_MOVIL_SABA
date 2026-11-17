package com.example.appsaba.ui.incidents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import com.example.appsaba.domain.model.Incident
import com.example.appsaba.domain.model.Service
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.appsaba.ui.data.RecordCard
import com.example.appsaba.ui.data.ConfirmDelete

@Composable
internal fun IncidentsAdminScreen(incidents: List<Incident>, services: List<Service>,
                               parkingId: Long?, busy: Boolean, model: IncidentsViewModel) {
    var serviceId by remember(parkingId) { mutableStateOf<Long?>(null) }
    var failure by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reporter by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("degradado") }
    var resolving by remember { mutableStateOf<Incident?>(null) }
    var resolver by remember { mutableStateOf("") }
    var solution by remember { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<Incident?>(null) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("3. Incidencias", style = MaterialTheme.typography.titleLarge)
        if (parkingId == null) {
            Text("Primero debes crear un estacionamiento.")
        } else if (services.isEmpty()) {
            Text("Este estacionamiento no tiene equipos. Crea uno en Equipos.")
        } else {
            Text("Reportar una falla", style = MaterialTheme.typography.titleMedium)
            Text("Equipo: ${services.find { it.id == serviceId }?.name ?: "Elige uno"}")
            services.forEach { service ->
                FilterChip(selected = serviceId == service.id, onClick = { serviceId = service.id },
                    label = { Text(service.name) })
            }
            OutlinedTextField(value = failure, onValueChange = { failure = it },
                label = { Text("Tipo de falla") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it },
                label = { Text("Descripción (máx. 240 caracteres)") },
                modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(value = reporter, onValueChange = { reporter = it },
                label = { Text("Nombre de quien reporta (opcional)") }, modifier = Modifier.fillMaxWidth())
            Text("Gravedad:")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("degradado", "fuera-servicio").forEach { status ->
                    FilterChip(selected = severity == status, onClick = { severity = status },
                        label = { Text(status) })
                }
            }
            Button(enabled = !busy, onClick = {
                model.reportIncident(parkingId, serviceId, failure, description, severity, reporter)
            }) { Text("Registrar ticket local") }
        }
        Text("Historial: ${incidents.size} · Abiertas: ${incidents.count { !it.isResolved }}",
            style = MaterialTheme.typography.titleMedium)
        if (incidents.isEmpty()) Text("Aún no hay incidencias registradas.")
        incidents.forEach { incident ->
            RecordCard("${incident.ticketCode} · ${if (incident.isResolved) "Resuelto" else "Abierto"}") {
                Text("Equipo: ${services.find { it.id == incident.serviceId }?.name ?: incident.serviceId}")
                Text("${incident.failureType} · ${incident.status}")
                Text(incident.description)
                Text("Reportó: ${incident.reporterName.ifBlank { "No indicado" }} · ${localTime(incident.reportedAt)}")
                if (incident.isResolved) {
                    Text("Resuelto por: ${incident.resolvedByName.orEmpty()}")
                    Text(incident.resolutionDescription.orEmpty())
                } else {
                    if (resolving?.id == incident.id) {
                        OutlinedTextField(value = resolver, onValueChange = { resolver = it },
                            label = { Text("Quién resolvió") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = solution, onValueChange = { solution = it },
                            label = { Text("Qué hizo (máx. 300)") }, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(enabled = !busy, onClick = {
                                model.resolveIncident(incident, resolver, solution)
                            }) { Text("Guardar resolución") }
                            TextButton(onClick = { resolving = null }) { Text("Cancelar") }
                        }
                    } else {
                        Button(onClick = {
                            resolving = incident; resolver = ""; solution = ""
                        }) { Text("Resolver") }
                    }
                }
                TextButton(enabled = !busy, onClick = { pendingDelete = incident }) { Text("Eliminar ticket") }
            }
        }
    }
    pendingDelete?.let { incident ->
        ConfirmDelete("el ticket '${incident.ticketCode}'",
            onConfirm = { pendingDelete = null; model.deleteIncident(incident) },
            onCancel = { pendingDelete = null })
    }
}

private fun localTime(epochMillis: Long): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date(epochMillis))
