package com.example.appsaba.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appsaba.data.local.SabaDatabase
import com.example.appsaba.data.remote.Ec2RetrofitClient
import com.example.appsaba.data.repository.RemoteParkingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

/** Operaciones remotas protegidas; caché Room independiente de los registros locales. */
@Composable
fun Ec2ParkingsSection(accessToken: String?) {
    val appContext = LocalContext.current.applicationContext
    val dao = remember(appContext) { SabaDatabase.getInstance(appContext).remoteParkingDao() }
    val repository = remember(appContext) { RemoteParkingRepository(Ec2RetrofitClient.api, dao) }
    val cached by dao.observeAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf("La caché muestra la última descarga correcta") }
    var name by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Long?>(null) }
    var busy by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    fun runRemote(action: suspend (String) -> Unit) {
        val token = accessToken
        if (token.isNullOrBlank() || busy) return
        busy = true
        scope.launch {
            try {
                action(token)
                message = "Sincronización correcta con EC2"
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (e: HttpException) {
                message = when(e.code()) {
                    401 -> "Sesión Cognito caducada. Cierra sesión e ingresa otra vez."
                    409 -> "Conflicto: el registro cambió. Actualiza antes de editar."
                    else -> "HTTP ${e.code()} en EC2"
                }
            } catch (e: Exception) {
                message = "EC2 no disponible. Se conserva la caché Room: ${e.localizedMessage}"
            } finally { busy = false }
        }
    }

    LaunchedEffect(accessToken) {
        if (!accessToken.isNullOrBlank()) runRemote { repository.refresh(it) }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("EC2 + Room (caché remota)", style = MaterialTheme.typography.titleMedium)
        if (accessToken.isNullOrBlank()) {
            Text("Las operaciones remotas requieren iniciar sesión con Cognito. Tu login local sigue funcionando para Room.")
        } else {
            Text(message, style = MaterialTheme.typography.bodySmall)
            OutlinedButton(enabled = !busy, onClick = { runRemote { repository.refresh(it) } }) {
                Text("Actualizar desde EC2")
            }
            OutlinedTextField(value=name, onValueChange={name=it},
                label={Text(if(editingId==null) "Nuevo estacionamiento EC2" else "Nuevo nombre")},
                modifier=Modifier.fillMaxWidth())
            Button(enabled=!busy && name.isNotBlank(), onClick={
                val item = cached.firstOrNull { it.id == editingId }
                runRemote { token ->
                    if (item == null) repository.create(token, name)
                    else repository.rename(token, item, name)
                    name="";editingId=null
                }
            }) { Text(if(editingId==null) "Crear en EC2" else "Guardar nombre en EC2") }
        }
        if (cached.isEmpty()) Text("Sin registros remotos guardados en la caché Room")
        cached.forEach { parking ->
            Text("#${parking.id} · ${parking.name} · ${if(parking.isActive) "Activo" else "Inactivo"}")
            if (!accessToken.isNullOrBlank()) {
                Row(horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                    TextButton(enabled=!busy, onClick={editingId=parking.id;name=parking.name}) { Text("Editar") }
                    TextButton(enabled=!busy, onClick={runRemote { repository.toggle(it,parking) }}) {
                        Text(if(parking.isActive) "Desactivar" else "Activar")
                    }
                    TextButton(enabled=!busy, onClick={pendingDeleteId=parking.id}) { Text("Eliminar") }
                }
                if(pendingDeleteId==parking.id) {
                    Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                        Text("¿Eliminar en EC2?", style=MaterialTheme.typography.bodySmall)
                        TextButton(enabled=!busy, onClick={
                            pendingDeleteId=null
                            runRemote { repository.delete(it,parking) }
                        }) { Text("Confirmar") }
                        TextButton(onClick={pendingDeleteId=null}) { Text("Cancelar") }
                    }
                }
            }
        }
        Text("No modifica la tabla parkings local. Se usa el último snapshot de EC2 cuando no hay Internet.",
            style=MaterialTheme.typography.bodySmall)
    }
}
