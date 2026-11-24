package com.example.appsaba.ui.incidents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** Panel independiente del CRUD Room; nunca mezcla identificadores de SQLite con PostgreSQL. */
@Composable
fun RemoteIncidentsSection(accessToken: String?, model: IncidentViewModel) {
    val state by model.remoteState.collectAsStateWithLifecycle()
    var parkingId by remember { mutableStateOf("") }
    var serviceId by remember { mutableStateOf("") }
    var reporter by remember { mutableStateOf("") }
    var failure by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("degradado") }
    var resolvingId by remember { mutableStateOf<Long?>(null) }
    var resolver by remember { mutableStateOf("") }
    var solution by remember { mutableStateOf("") }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Incidencias remotas · BFF", style = MaterialTheme.typography.titleMedium)
            Text("PostgreSQL vía Cognito → BFF → Incidents. Los tickets Room permanecen independientes.",
                style = MaterialTheme.typography.bodySmall)
            Text(state.feedback, style = MaterialTheme.typography.bodySmall)
            if (accessToken.isNullOrBlank()) {
                Text("Inicia sesión con Cognito para utilizar el BFF.")
            } else {
                TextButton(enabled = !state.busy, onClick = model::refreshRemote) {
                    Text("Actualizar desde BFF")
                }
                Text("Crear ticket remoto", style = MaterialTheme.typography.titleSmall)
                Text("Usa el ID remoto de Parking (de Inicio/BFF). El serviceId es un identificador remoto positivo, no el ID local de Room.",
                    style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = parkingId, onValueChange = { parkingId = it },
                        label = { Text("Parking remoto ID") }, modifier = Modifier.weight(1f), singleLine = true
                    )
                    OutlinedTextField(
                        value = serviceId, onValueChange = { serviceId = it },
                        label = { Text("Service ID") }, modifier = Modifier.weight(1f), singleLine = true
                    )
                }
                OutlinedTextField(value = reporter, onValueChange = { reporter = it },
                    label = { Text("Reportado por") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = failure, onValueChange = { failure = it },
                    label = { Text("Tipo de falla") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it },
                    label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = severity == "degradado", onClick = { severity = "degradado" },
                        label = { Text("Degradado") })
                    FilterChip(selected = severity == "fuera-servicio", onClick = { severity = "fuera-servicio" },
                        label = { Text("Fuera de servicio") })
                }
                Button(enabled = !state.busy && parkingId.toLongOrNull() != null &&
                        serviceId.toLongOrNull() != null && failure.isNotBlank() && description.isNotBlank(),
                    onClick = {
                        model.createRemote(parkingId.toLongOrNull(), serviceId.toLongOrNull(),
                            reporter, failure, description, severity)
                    }) { Text("Crear en BFF") }
                Text("Registros del servidor: ${state.incidents.size}",
                    style = MaterialTheme.typography.titleSmall)
                state.incidents.forEach { incident ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("#${incident.id} · ${incident.ticketCode} · Parking #${incident.parkingId}")
                        Text("${incident.failureType} — ${incident.status}",
                            style = MaterialTheme.typography.bodySmall)
                        Text(incident.description, style = MaterialTheme.typography.bodySmall)
                        if (incident.isResolved) {
                            Text("Resuelta por ${incident.resolvedByName.orEmpty()}")
                        } else {
                            TextButton(enabled = !state.busy,
                                onClick = { resolvingId = incident.id }) { Text("Resolver") }
                            if (resolvingId == incident.id) {
                                OutlinedTextField(value = resolver, onValueChange = { resolver = it },
                                    label = { Text("Resuelta por") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = solution, onValueChange = { solution = it },
                                    label = { Text("Solución") }, modifier = Modifier.fillMaxWidth())
                                Button(enabled = !state.busy && resolver.isNotBlank() && solution.isNotBlank(),
                                    onClick = {
                                        model.resolveRemote(incident, resolver, solution)
                                        resolvingId = null
                                        resolver = ""; solution = ""
                                    }) { Text("Confirmar resolución") }
                            }
                        }
                    }
                }
            }
        }
    }
}
