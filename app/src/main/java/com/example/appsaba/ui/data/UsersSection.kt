package com.example.appsaba.ui.data

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
import androidx.compose.ui.unit.dp
import com.example.appsaba.data.model.Parking
import com.example.appsaba.data.model.User

@Composable
internal fun UsersSection(users: List<User>, parkings: List<Parking>, selectedParkingId: Long?,
                          busy: Boolean, model: SabaDataViewModel) {
    var editing by remember { mutableStateOf<User?>(null) }
    var name by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("operador") }
    var assignedParking by remember { mutableStateOf<Long?>(null) }
    var pendingDelete by remember { mutableStateOf<User?>(null) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("4. Perfiles locales", style = MaterialTheme.typography.titleLarge)
        Text("Estos perfiles NO sirven para iniciar sesión: el login sigue siendo independiente.")
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT opcional") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Rol") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Text("Asignación: ${parkings.find { it.id == assignedParking }?.name ?: "Sin estacionamiento"}",
            style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { assignedParking = selectedParkingId }) {
                Text("Asignar seleccionado")
            }
            TextButton(onClick = { assignedParking = null }) { Text("Sin asignación") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(enabled = !busy, onClick = { model.saveUser(editing?.id, name, rut, role, assignedParking) }) {
                Text(if (editing == null) "Agregar perfil" else "Guardar perfil")
            }
            TextButton(onClick = {
                editing = null; name = ""; rut = ""; role = "operador"; assignedParking = null
            }) { Text("Limpiar") }
        }
        Text("Perfiles (${users.size})", style = MaterialTheme.typography.titleMedium)
        if (users.isEmpty()) Text("No hay perfiles locales registrados.")
        users.forEach { user ->
            RecordCard(user.name) {
                Text("RUT: ${user.rut ?: "-"} · Rol: ${user.role}")
                Text("Asignado a: ${parkings.find { it.id == user.assignedParkingId }?.name ?: "Ninguno"}")
                Text(if (user.isActive) "Activo" else "Inactivo")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        editing = user; name = user.name; rut = user.rut.orEmpty(); role = user.role
                        assignedParking = user.assignedParkingId
                    }) { Text("Editar") }
                    OutlinedButton(enabled = !busy, onClick = { model.toggleUser(user) }) {
                        Text(if (user.isActive) "Desactivar" else "Activar")
                    }
                }
                TextButton(enabled = !busy, onClick = { pendingDelete = user }) { Text("Eliminar") }
            }
        }
    }
    pendingDelete?.let { user ->
        ConfirmDelete("el perfil '${user.name}'",
            onConfirm = { pendingDelete = null; model.deleteUser(user) },
            onCancel = { pendingDelete = null })
    }
}
