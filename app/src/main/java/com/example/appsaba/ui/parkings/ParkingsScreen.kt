package com.example.appsaba.ui.parkings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.appsaba.domain.model.Parking

import com.example.appsaba.ui.data.RecordCard
import com.example.appsaba.ui.data.ConfirmDelete

@Composable
internal fun ParkingsScreen(parkings: List<Parking>, busy: Boolean, model: ParkingsViewModel) {
    var editing by remember { mutableStateOf<Parking?>(null) }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var commune by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("0") }
    var free by remember { mutableStateOf("0") }
    var pendingDelete by remember { mutableStateOf<Parking?>(null) }

    fun reset() {
        editing = null; name = ""; address = ""; commune = ""; total = "0"; free = "0"
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("1. Estacionamientos", style = MaterialTheme.typography.titleLarge)
        Text("Crea primero un estacionamiento. Después podrás asignarle equipos e incidencias.")
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = commune, onValueChange = { commune = it }, label = { Text("Comuna") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = total, onValueChange = { total = it },
                label = { Text("Plazas totales") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = free, onValueChange = { free = it },
                label = { Text("Plazas libres") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(enabled = !busy, onClick = {
                model.saveParking(editing?.id, name, address, commune, total, free)
            }) { Text(if (editing == null) "Agregar" else "Guardar cambios") }
            TextButton(onClick = { reset() }) { Text("Limpiar") }
        }
        Text("Guardados (${parkings.size})", style = MaterialTheme.typography.titleMedium)
        if (parkings.isEmpty()) Text("Aún no hay estacionamientos en SQLite.")
        parkings.forEach { parking ->
            RecordCard(parking.name) {
                Text("${parking.address} · ${parking.commune}")
                Text("Libres: ${parking.freeSpaces} / ${parking.totalSpaces} · ${if (parking.isActive) "Activo" else "Inactivo"}")
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    OutlinedButton(onClick = {
                        editing = parking; name = parking.name; address = parking.address
                        commune = parking.commune; total = parking.totalSpaces.toString()
                        free = parking.freeSpaces.toString()
                    }) { Text("Editar") }
                    OutlinedButton(enabled = !busy, onClick = { model.toggleParking(parking) }) {
                        Text(if (parking.isActive) "Desactivar" else "Activar")
                    }
                }
                TextButton(enabled = !busy, onClick = { pendingDelete = parking }) { Text("Eliminar") }
            }
        }
    }
    pendingDelete?.let { parking ->
        ConfirmDelete("el estacionamiento '${parking.name}'",
            onConfirm = { pendingDelete = null; model.deleteParking(parking) },
            onCancel = { pendingDelete = null })
    }
}
