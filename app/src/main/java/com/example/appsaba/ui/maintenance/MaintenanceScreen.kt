package com.example.appsaba.ui.maintenance

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appsaba.domain.model.Service
import com.example.appsaba.ui.incidents.IncidentsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Diseño de Mantención adaptado a registros reales de Room, sin datos falsos ni otro login. */
private val Blue = Color(0xFF0B72B9)
private val BlueDark = Color(0xFF0A5C97)
private val Background = Color(0xFFF4F6F9)
private val Foreground = Color(0xFF1B2433)
private val Muted = Color(0xFF7A869A)
private val Green = Color(0xFF1E7B4F)
private val Amber = Color(0xFFB88A13)
private val Red = Color(0xFFD6404E)

private fun statusText(s: String): String = when (s) {
    "fuera-servicio" -> "Fuera de servicio"
    "degradado" -> "Degradado"
    else -> "Operativo"
}
private fun statusColor(s: String): Color = when (s) {
    "fuera-servicio" -> Red
    "degradado" -> Amber
    else -> Green
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(onManageEquipment: () -> Unit = {}) {
    val context = LocalContext.current
    val vm: MaintenanceViewModel = viewModel()
    val incidentVm: IncidentsViewModel = viewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()
    val incidentState by incidentVm.uiState.collectAsStateWithLifecycle()
    var selectedServiceId by remember { mutableStateOf<Long?>(null) }
    var reportId by remember { mutableStateOf<Long?>(null) }
    val parking = state.parkings.firstOrNull { it.id == state.selectedParkingId }
    val selectedService = state.services.firstOrNull { it.id == selectedServiceId }
    val reportService = state.services.firstOrNull { it.id == reportId }

    LaunchedEffect(state.parkings, state.selectedParkingId) {
        val valid = state.selectedParkingId?.takeIf { id -> state.parkings.any { it.id == id } }
        if (valid == null) vm.selectParking(state.parkings.firstOrNull()?.id)
    }
    LaunchedEffect(state.selectedParkingId) { incidentVm.selectParking(state.selectedParkingId) }
    LaunchedEffect(state.feedback) {
        state.feedback?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            vm.feedbackShown()
        }
    }
    LaunchedEffect(incidentState.feedback) {
        incidentState.feedback?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            incidentVm.feedbackShown()
        }
    }
    Column(Modifier.fillMaxSize().background(Background)) {
        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(parking?.name?.uppercase() ?: "SABA", color = Blue,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(if (selectedService == null) "Panel de mantenimiento" else "Detalle del servicio",
                    fontSize = 22.sp, color = Foreground, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Outlined.Build, null, tint = Blue)
        }
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (selectedService == null) {
                Text("ESTADO ACTUAL DE EQUIPOS", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (state.parkings.size > 1) {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.parkings.forEach { item ->
                            FilterChip(selected = item.id == state.selectedParkingId,
                                onClick = { vm.selectParking(item.id); selectedServiceId = null },
                                label = { Text(item.name) })
                        }
                    }
                }
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Blue, BlueDark)))
                        .padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(parking?.name?.uppercase() ?: "SIN ESTACIONAMIENTO", fontSize = 11.sp, color = Color(0xFFBFE3F8))
                            Text("Estado operacional", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Monitoreo de servicios tecnológicos", fontSize = 11.sp, color = Color.White)
                        }
                        Text("${state.services.count { it.status == "operativo" }}/${state.services.size}",
                            fontSize = 21.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    if (state.services.isEmpty()) {
                        Text("Aún no hay equipos. Registra primero un estacionamiento y sus equipos en Datos locales.",
                            Modifier.padding(16.dp), color = Muted, fontSize = 13.sp)
                    }
                    state.services.forEach { service ->
                        Row(Modifier.fillMaxWidth().clickable { selectedServiceId = service.id }
                            .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Build, null, tint = statusColor(service.status))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(service.name, fontWeight = FontWeight.SemiBold, color = Foreground)
                                Text(service.detail.ifBlank { service.type }, fontSize = 12.sp, color = Muted)
                            }
                            Text(statusText(service.status), color = statusColor(service.status), fontSize = 11.sp)
                            Icon(Icons.Outlined.ChevronRight, null, tint = Muted)
                        }
                        HorizontalDivider(color = Color(0xFFEDF0F4))
                    }
                }
                val failing = state.services.firstOrNull { it.status == "fuera-servicio" }
                    ?: state.services.firstOrNull { it.status == "degradado" }
                if (failing != null) {
                    Button(onClick = { reportId = failing.id }, modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB82520))) {
                        Icon(Icons.Outlined.ReportProblem, null)
                        Spacer(Modifier.width(10.dp))
                        Text("Reportar falla crítica")
                    }
                }
                OutlinedButton(onClick = onManageEquipment, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Settings, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Administrar equipos locales (Room)")
                }
            } else {
                TextButton(onClick = { selectedServiceId = null }) {
                    Icon(Icons.Outlined.ChevronLeft, null)
                    Text("Volver al estado operacional")
                }
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Blue)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("SERVICIO TECNOLÓGICO", fontSize = 11.sp, color = Color(0xFFBBE1FC))
                        Text(selectedService.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 23.sp)
                        Text(statusText(selectedService.status), color = Color.White, fontSize = 14.sp)
                    }
                }
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MaintenanceDataLine("Parking", parking?.name ?: "-")
                        MaintenanceDataLine("Tipo", selectedService.type.uppercase())
                        MaintenanceDataLine("Detalle", selectedService.detail.ifBlank { "-" })
                        MaintenanceDataLine("Estado", statusText(selectedService.status))
                        MaintenanceDataLine("Última actualización", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL"))
                            .format(Date(selectedService.lastUpdatedAt)))
                    }
                }
                if (selectedService.status != "operativo") {
                    Text("DESCRIPCIÓN DEL PROBLEMA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Red)
                    Text(selectedService.problem?.takeIf { it.isNotBlank() } ?: "Equipo requiere revisión técnica.",
                        color = Foreground)
                }
                var showProcedure by remember(selectedService.id) { mutableStateOf(false) }
                OutlinedButton(onClick = { showProcedure = !showProcedure }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.MenuBook, null); Spacer(Modifier.width(8.dp)); Text("Ver procedimiento")
                }
                if (showProcedure) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Primera respuesta (guía general)", fontWeight = FontWeight.SemiBold)
                            Text("1. Comprueba la energía y los indicadores del equipo.")
                            Text("2. Verifica conexiones y estado operacional.")
                            Text("3. Si la falla persiste, registra una incidencia.")
                        }
                    }
                }
                Button(onClick = { reportId = selectedService.id }, modifier = Modifier.fillMaxWidth()) {
                    Text("Reportar incidencia")
                }
                OutlinedButton(onClick = {
                    try { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+56200000000"))) }
                    catch (_: Exception) { Toast.makeText(context, "Marcador no disponible", Toast.LENGTH_SHORT).show() }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Contactar soporte")
                }
                Text("Cambiar estado", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("operativo", "degradado", "fuera-servicio").forEach { value ->
                        FilterChip(selected = selectedService.status == value,
                            onClick = { vm.setServiceStatus(selectedService, value) },
                            enabled = !state.busy, label = { Text(statusText(value)) })
                    }
                }
            }
        }
    }
    if (reportService != null) {
        ModalBottomSheet(onDismissRequest = { reportId = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            MaintenanceIncidentForm(reportService, state.services,
                busy = incidentState.busy, onCancel = { reportId = null }) { id, failure, desc, severity, uri ->
                incidentVm.reportIncident(state.selectedParkingId, id, failure, desc, severity, "", uri)
                reportId = null
            }
        }
    }
}

@Composable
private fun MaintenanceDataLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f), color = Muted, fontSize = 12.sp)
        Text(value, fontSize = 12.sp, color = Foreground, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MaintenanceIncidentForm(
    selected: Service, services: List<Service>, busy: Boolean, onCancel: () -> Unit,
    onPublish: (Long, String, String, String, Uri?) -> Unit
) {
    var serviceId by remember(selected.id) { mutableLongStateOf(selected.id) }
    var failure by remember { mutableStateOf("Falla de comunicación") }
    var description by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("fuera-servicio") }
    var evidence by remember { mutableStateOf<Uri?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        evidence = it
    }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Reportar un incidente", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f))
            IconButton(onClick = onCancel) { Icon(Icons.Outlined.Close, "Cerrar") }
        }
        Text("Equipo afectado", fontWeight = FontWeight.SemiBold)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            services.forEach { service ->
                FilterChip(selected = serviceId == service.id,
                    onClick = { serviceId = service.id }, label = { Text(service.name) })
            }
        }
        Text("Tipo de falla", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(value = failure, onValueChange = { failure = it },
            modifier = Modifier.fillMaxWidth(), singleLine = true)
        Text("Gravedad", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("degradado", "fuera-servicio").forEach { value ->
                FilterChip(selected = severity == value, onClick = { severity = value },
                    label = { Text(statusText(value)) })
            }
        }
        OutlinedTextField(value = description,
            onValueChange = { if (it.length <= 240) description = it },
            modifier = Modifier.fillMaxWidth(), minLines = 3,
            label = { Text("Observaciones") })
        Text("${description.length}/240 caracteres", color = Muted, fontSize = 11.sp)
        OutlinedButton(onClick = {
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.CameraAlt, null)
            Spacer(Modifier.width(8.dp))
            Text(if (evidence == null) "Adjuntar foto o video (opcional)" else "Evidencia seleccionada")
        }
        message?.let { Text(it, color = Red) }
        Button(enabled = !busy, modifier = Modifier.fillMaxWidth(), onClick = {
            if (description.isBlank() || failure.isBlank()) {
                message = "Completa tipo de falla y descripción"
            } else {
                onPublish(serviceId, failure, description, severity, evidence)
            }
        }) { Text("Publicar incidencia") }
        Spacer(Modifier.height(24.dp))
    }
}
