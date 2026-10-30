package com.example.appsaba.ui.maintenance

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.appsaba.data.model.Parking
import com.example.appsaba.data.model.Service
import com.example.appsaba.ui.theme.AppSabaTheme
import com.example.appsaba.ui.data.SabaDataViewModel
import com.example.appsaba.ui.data.SabaDataViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Diseño de Mantención aportado por el equipo; datos y acciones conectados a Room / MVVM. */
private val Blue = Color(0xFF0B72B9)
private val BlueDark = Color(0xFF0A5C97)
private val BgScreen = Color(0xFFF4F6F9)
private val TextDark = Color(0xFF1B2433)
private val TextMuted = Color(0xFF7A869A)
private val Green = Color(0xFF1E7B4F)
private val GreenBg = Color(0xFFE3F4EA)
private val Red = Color(0xFFD6404E)
private val RedBg = Color(0xFFFDEEEE)
private val Amber = Color(0xFFE5A823)
private val AmberBg = Color(0xFFFFF4D6)
private val LightBlueBg = Color(0xFFEAF3F9)

private enum class ServiceStatus(val label: String, val color: Color, val bg: Color) {
    OPERATIVO("Operativo", Green, GreenBg),
    DEGRADADO("Degradado", Amber, AmberBg),
    FUERA("Fuera de servicio", Red, RedBg);
    companion object {
        fun fromDb(status: String) = when (status) {
            "degradado" -> DEGRADADO
            "fuera-servicio" -> FUERA
            else -> OPERATIVO
        }
    }
}

private data class TechService(
    val model: Service,
    val name: String = model.name,
    val subtitle: String = model.detail.ifBlank { when (model.type.lowercase()) {
        "barriers" -> "Control de barreras de acceso"
        "lpr" -> "Lectura de patentes"
        "pos" -> "Terminal de pagos"
        "atm" -> "Cajero automático"
        else -> model.type
    } },
    val icon: ImageVector = when (model.type.lowercase()) {
        "barriers" -> Icons.Outlined.Garage
        "lpr" -> Icons.Outlined.CameraAlt
        "pos" -> Icons.Outlined.CreditCard
        "atm" -> Icons.Outlined.Calculate
        else -> Icons.Outlined.Build
    },
    val status: ServiceStatus = ServiceStatus.fromDb(model.status),
    val lastUpdate: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL"))
        .format(Date(model.lastUpdatedAt)),
    val problem: String? = model.problem
)

private data class IncidentDraft(
    val serviceId: Long,
    val failure: String,
    val description: String,
    val severity: String,
    val evidence: Uri?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(onManageEquipment: () -> Unit = {}) {
    val context = LocalContext.current
    val factory = remember(context.applicationContext) { SabaDataViewModelFactory(context.applicationContext) }
    val model: SabaDataViewModel = viewModel(factory = factory)
    val parkings by model.parkings.collectAsStateWithLifecycle()
    val selectedId by model.selectedParkingId.collectAsStateWithLifecycle()
    val dbServices by model.services.collectAsStateWithLifecycle()
    val busy by model.busy.collectAsStateWithLifecycle()
    val feedback by model.feedback.collectAsStateWithLifecycle()

    var detailId by remember { mutableStateOf<Long?>(null) }
    var reportServiceId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(parkings, selectedId) {
        if (selectedId == null || parkings.none { it.id == selectedId }) {
            parkings.firstOrNull()?.let { model.selectParking(it.id) }
        }
    }
    LaunchedEffect(feedback) {
        feedback?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            model.feedbackShown()
        }
    }

    val selectedParking = parkings.firstOrNull { it.id == selectedId }
    val services = dbServices.filter { it.parkingId == selectedId }.map(::TechService)
    val detail = services.firstOrNull { it.model.id == detailId }
    val reporting = services.firstOrNull { it.model.id == reportServiceId }

    if (detailId != null && detail == null) {
        LaunchedEffect(detailId, services) { detailId = null }
    }

    if (detail != null) {
        MaintenanceDetail(
            service = detail,
            parkingName = selectedParking?.name.orEmpty(),
            onBack = { detailId = null },
            onReport = { reportServiceId = detail.model.id },
            onSetStatus = { newStatus -> model.setServiceStatus(detail.model, newStatus) },
            onContact = {
                // Usa el marcador real del dispositivo, igual que la sección Guías.
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+56200000000"))
                try { context.startActivity(intent) }
                catch (_: Exception) {
                    Toast.makeText(context, "No hay aplicación de teléfono disponible", Toast.LENGTH_LONG).show()
                }
            },
            busy = busy
        )
    } else {
        MaintenancePanel(
            services = services,
            parkings = parkings,
            selectedParkingId = selectedId,
            parkingName = selectedParking?.name,
            onSelectParking = { model.selectParking(it); detailId = null },
            onService = { detailId = it },
            onReport = { reportServiceId = it },
            onManageEquipment = onManageEquipment
        )
    }

    if (reporting != null && selectedId != null) {
        ModalBottomSheet(
            onDismissRequest = { reportServiceId = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            MaintenanceReportSheet(
                service = reporting,
                allServices = services,
                onClose = { reportServiceId = null },
                onPublish = { draft ->
                    model.reportIncident(
                        parkingId = selectedId,
                        serviceId = draft.serviceId,
                        failure = draft.failure,
                        description = draft.description,
                        severity = draft.severity,
                        reporter = "",
                        evidenceUri = draft.evidence
                    )
                    reportServiceId = null
                },
                busy = busy
            )
        }
    }
}

@Composable
private fun MaintenanceTopHeader(title: String, parkingName: String?) {
    Row(
        Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(parkingName?.uppercase() ?: "SABA", color = Blue, fontSize = 11.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(title, color = TextDark, fontSize = 22.sp, fontWeight = FontWeight.Medium)
        }
        Box(
            Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(BgScreen)
                .border(1.dp, Color(0xFFE3E8EF), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Outlined.Build, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp)) }
        Spacer(Modifier.width(10.dp))
        Box(Modifier.size(42.dp).clip(CircleShape).background(Blue), contentAlignment = Alignment.Center) {
            Text("SB", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun MaintenanceStatusChip(status: ServiceStatus, inverted: Boolean = false) {
    Row(Modifier.clip(RoundedCornerShape(50)).background(if (inverted) Color.White else status.bg)
        .padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(status.color))
        Spacer(Modifier.width(5.dp))
        Text(status.label, color = status.color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MaintenanceWhiteCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp))
        .clip(RoundedCornerShape(18.dp)).background(Color.White), content = content)
}

@Composable
private fun MaintenancePanel(
    services: List<TechService>, parkings: List<Parking>, selectedParkingId: Long?,
    parkingName: String?, onSelectParking: (Long) -> Unit, onService: (Long) -> Unit,
    onReport: (Long) -> Unit, onManageEquipment: () -> Unit
) {
    val operational = services.count { it.status == ServiceStatus.OPERATIVO }
    val failing = services.firstOrNull { it.status == ServiceStatus.FUERA }
        ?: services.firstOrNull { it.status == ServiceStatus.DEGRADADO }
    Column(Modifier.fillMaxSize().background(BgScreen)) {
        MaintenanceTopHeader("Panel de mantenimiento", parkingName)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text("ESTADO ACTUAL DE EQUIPOS", color = TextMuted, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Spacer(Modifier.height(16.dp))
            if (parkings.size > 1) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    parkings.forEach { parking ->
                        FilterChip(selected = parking.id == selectedParkingId,
                            onClick = { onSelectParking(parking.id) }, label = { Text(parking.name) })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            MaintenanceWhiteCard {
                Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Blue, BlueDark)))
                    .padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(parkingName?.uppercase() ?: "SIN ESTACIONAMIENTO", color = Color(0xFFBFE3F8),
                            fontSize = 11.sp, letterSpacing = 1.sp)
                        Text("Estado operacional", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("Monitoreo de servicios tecnológicos", color = Color(0xFFBFE3F8), fontSize = 11.sp)
                    }
                    Column(Modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = .15f))
                        .padding(horizontal = 14.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$operational/${services.size}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("OPERATIVOS", color = Color(0xFFBFE3F8), fontSize = 8.sp, letterSpacing = 1.sp)
                    }
                }
                if (failing != null) {
                    Row(Modifier.fillMaxWidth().background(RedBg).padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).clip(CircleShape).background(Red))
                        Spacer(Modifier.width(8.dp))
                        Text("Atención requerida", color = Red, fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("Según Room", color = Amber, fontSize = 10.sp)
                    }
                }
                if (services.isEmpty()) {
                    Text(if (parkingName == null) "Primero registra un estacionamiento desde Datos locales."
                        else "No hay equipos registrados en este estacionamiento.",
                        modifier = Modifier.padding(16.dp), color = TextMuted)
                }
                services.forEach { service ->
                    MaintenanceServiceRow(service) { onService(service.model.id) }
                    HorizontalDivider(color = Color(0xFFEFF2F6))
                }
                if (failing != null) {
                    Row(Modifier.padding(16.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Red).clickable { onReport(failing.model.id) }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(.2f)),
                            contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Shield, null, tint = Color.White)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Reportar falla crítica", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Registrar una incidencia en Room", color = Color.White.copy(.85f), fontSize = 11.sp)
                        }
                        Icon(Icons.Outlined.ChevronRight, null, tint = Color.White)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onManageEquipment, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Settings, null)
                Spacer(Modifier.width(8.dp))
                Text("Administrar equipos locales")
            }
        }
    }
}

@Composable
private fun MaintenanceServiceRow(service: TechService, onClick: () -> Unit) {
    val bad = service.status != ServiceStatus.OPERATIVO
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(if (bad) RedBg else GreenBg),
            contentAlignment = Alignment.Center) {
            Icon(service.icon, null, tint = if (bad) Red else Green)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(service.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
            Text(service.subtitle, fontSize = 11.sp, color = TextMuted)
        }
        MaintenanceStatusChip(service.status)
        Icon(Icons.Outlined.ChevronRight, null, tint = TextMuted)
    }
}

@Composable
private fun MaintenanceDetail(
    service: TechService, parkingName: String, onBack: () -> Unit,
    onReport: () -> Unit, onSetStatus: (String) -> Unit,
    onContact: () -> Unit, busy: Boolean
) {
    var showProcedure by remember(service.model.id) { mutableStateOf(false) }
    val isFailing = service.status != ServiceStatus.OPERATIVO
    Column(Modifier.fillMaxSize().background(BgScreen)) {
        MaintenanceTopHeader("Detalle del servicio", parkingName)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            Row(Modifier.clickable(onClick = onBack), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ChevronLeft, null, tint = Blue, modifier = Modifier.size(18.dp))
                Text("Volver a Estado operacional", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(Blue, BlueDark))).padding(20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(.15f)),
                    contentAlignment = Alignment.Center) {
                    Icon(service.icon, null, tint = Color(0xFF7CF0C8), modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("SERVICIO TECNOLÓGICO", color = Color(0xFF8FDCFF), fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(service.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    MaintenanceStatusChip(service.status, inverted = true)
                }
            }
            Spacer(Modifier.height(16.dp))
            MaintenanceWhiteCard {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    MaintenanceInfoRow("Nombre del equipo", service.name)
                    HorizontalDivider(color = Color(0xFFEFF2F6))
                    MaintenanceInfoRow("Parking", parkingName)
                    HorizontalDivider(color = Color(0xFFEFF2F6))
                    MaintenanceInfoRow("Estado actual", service.status.label, valueColor = service.status.color, dot = true)
                    HorizontalDivider(color = Color(0xFFEFF2F6))
                    MaintenanceInfoRow("Última actualización", service.lastUpdate)
                }
            }
            if (isFailing) {
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(RedBg)
                    .border(1.dp, Color(0xFFF6CDD0), RoundedCornerShape(16.dp)).padding(14.dp)) {
                    Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Red),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.PriorityHigh, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("DESCRIPCIÓN DEL PROBLEMA", color = Color(0xFFB02A37), fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(service.problem?.takeIf { it.isNotBlank() }
                            ?: "El equipo requiere revisión técnica.", color = Color(0xFF8A3A42), fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("GESTIÓN DEL EQUIPO", color = TextMuted, fontSize = 12.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("Acciones disponibles", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Spacer(Modifier.height(12.dp))
            MaintenanceActionItem(Icons.AutoMirrored.Outlined.MenuBook, AmberBg, Amber,
                "Ver procedimiento", "Consulta los pasos de diagnóstico") { showProcedure = !showProcedure }
            if (showProcedure) {
                Spacer(Modifier.height(8.dp))
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEDF1F5)).padding(14.dp)) {
                    Text("Procedimiento de revisión inicial", fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp, color = TextDark)
                    Spacer(Modifier.height(6.dp))
                    listOf("Verifica la alimentación eléctrica del equipo.",
                        "Revisa el estado de conexión y los indicadores.",
                        "Si la falla continúa, registra una incidencia.").forEach {
                        Text(it, fontSize = 11.sp, color = TextMuted,
                            modifier = Modifier.padding(start = 12.dp, top = 3.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            MaintenanceActionItem(Icons.Outlined.Shield, LightBlueBg, Blue,
                "Reportar incidencia", "Registra una falla para este equipo", highlight = true, onClick = onReport)
            Spacer(Modifier.height(8.dp))
            MaintenanceActionItem(Icons.Outlined.Phone, Color(0xFFEDF1F5), TextDark,
                "Contactar soporte", "Abrir el marcador telefónico", onClick = onContact)
            Spacer(Modifier.height(16.dp))
            Text("Actualizar estado", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextDark)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("operativo" to "Operativo", "degradado" to "Degradado", "fuera-servicio" to "Fuera").forEach { (value, label) ->
                    FilterChip(selected = service.model.status == value, enabled = !busy,
                        onClick = { onSetStatus(value) }, label = { Text(label, fontSize = 11.sp) })
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MaintenanceInfoRow(label: String, value: String, valueColor: Color = TextDark, dot: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
        if (dot) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(valueColor))
            Spacer(Modifier.width(6.dp))
        }
        Text(value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MaintenanceActionItem(
    icon: ImageVector, iconBg: Color, iconTint: Color, title: String, subtitle: String,
    highlight: Boolean = false, onClick: () -> Unit
) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .background(if (highlight) Color(0xFFF1F8FC) else Color.White)
        .border(1.dp, if (highlight) Color(0xFFCFE4F1) else Color(0xFFE8ECF1), RoundedCornerShape(16.dp))
        .clickable(onClick = onClick).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(iconBg), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconTint)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                color = if (highlight) Blue else TextDark)
            Text(subtitle, fontSize = 11.sp, color = TextMuted)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = TextMuted)
    }
}

@Composable
private fun MaintenanceReportSheet(
    service: TechService, allServices: List<TechService>,
    onClose: () -> Unit, onPublish: (IncidentDraft) -> Unit, busy: Boolean
) {
    val failures = listOf("Falla en cajero automático", "Falla de comunicación", "Falla eléctrica", "Otro")
    var selectedServiceId by remember(service.model.id) { mutableLongStateOf(service.model.id) }
    var failure by remember(service.model.id) { mutableStateOf(if (service.model.type == "atm") failures.first() else "Otro") }
    var severity by remember { mutableStateOf(ServiceStatus.FUERA) }
    var observations by remember { mutableStateOf("") }
    var evidence by remember { mutableStateOf<Uri?>(null) }
    val duplicateNames = allServices.groupBy { it.name }
    fun labelFor(item: TechService): String = if ((duplicateNames[item.name]?.size ?: 0) > 1)
        "${item.name} (#${item.model.id})" else item.name
    var error by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        evidence = it
    }

    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("NUEVA INCIDENCIA", color = TextMuted, fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("Reportar un incidente", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = TextDark)
            }
            IconButton(onClick = onClose, modifier = Modifier.clip(CircleShape).background(BgScreen)) {
                Icon(Icons.Outlined.Close, "Cerrar")
            }
        }
        Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(16.dp))
            MaintenanceFieldLabel("Equipo afectado")
            MaintenanceDropdown(
                value = allServices.firstOrNull { it.model.id == selectedServiceId }?.let(::labelFor) ?: service.name,
                options = allServices.map(::labelFor),
                onSelect = { name -> allServices.firstOrNull { labelFor(it) == name }?.let { selectedServiceId = it.model.id } }
            )
            Spacer(Modifier.height(16.dp))
            MaintenanceFieldLabel("Tipo de falla")
            MaintenanceDropdown(failure, failures) { failure = it }
            Spacer(Modifier.height(16.dp))
            MaintenanceFieldLabel("Estado")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MaintenanceStateOption("Degradado", "Operación parcial", Amber,
                    severity == ServiceStatus.DEGRADADO, Modifier.weight(1f)) { severity = ServiceStatus.DEGRADADO }
                MaintenanceStateOption("Fuera de servicio", "Fuera de servicio", Red,
                    severity == ServiceStatus.FUERA, Modifier.weight(1f)) { severity = ServiceStatus.FUERA }
            }
            Spacer(Modifier.height(16.dp))
            MaintenanceFieldLabel("Observaciones")
            OutlinedTextField(
                value = observations, onValueChange = { if (it.length <= 240) observations = it },
                placeholder = { Text("Describe qué ocurrió, síntomas observados y contexto…", fontSize = 12.sp, color = TextMuted) },
                modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF8FAFC), focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedBorderColor = Color(0xFFE3E8EF), focusedBorderColor = Blue
                )
            )
            Text("${observations.length}/240", fontSize = 10.sp, color = TextMuted,
                textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
            Spacer(Modifier.height(12.dp))
            Row {
                MaintenanceFieldLabel("Evidencia mediante fotografía o video")
                Spacer(Modifier.width(6.dp))
                Text("Opcional", fontSize = 10.sp, color = Blue, modifier = Modifier.padding(top = 2.dp))
            }
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF8FAFC)).border(BorderStroke(1.dp, Color(0xFFD5DCE5)), RoundedCornerShape(16.dp))
                .clickable { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) }
                .padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(LightBlueBg), contentAlignment = Alignment.Center) {
                    Icon(if (evidence != null) Icons.Outlined.Check else Icons.Outlined.CameraAlt, null, tint = Blue)
                }
                Spacer(Modifier.height(8.dp))
                Text(if (evidence != null) "Archivo seleccionado" else "Agregar fotografía o video",
                    fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextDark)
                Text("Opcional · Selecciona desde la galería", fontSize = 10.sp, color = TextMuted)
            }
            Spacer(Modifier.height(16.dp))
            MaintenanceInfoBanner(Icons.Outlined.VerifiedUser, "Registro local", "Incidencia almacenada en Room",
                "La información permanece en este teléfono.")
            Spacer(Modifier.height(10.dp))
            if (error != null) { Text(error!!, color = Red, fontSize = 12.sp) }
            Spacer(Modifier.height(16.dp))
        }
        Button(onClick = {
            if (observations.isBlank()) {
                error = "Describe la falla antes de publicar la incidencia."
            } else {
                onPublish(IncidentDraft(selectedServiceId, failure, observations,
                    if (severity == ServiceStatus.FUERA) "fuera-servicio" else "degradado", evidence))
            }
        }, enabled = !busy, modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Blue)) {
            Text("Publicar incidencia", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Outlined.Check, null, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun MaintenanceFieldLabel(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextDark,
        modifier = Modifier.padding(bottom = 8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaintenanceDropdown(value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(value = value, onValueChange = {}, readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF8FAFC), focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedBorderColor = Color(0xFFE3E8EF), focusedBorderColor = Blue
            ))
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false })
            }
        }
    }
}

@Composable
private fun MaintenanceStateOption(
    title: String, subtitle: String, dot: Color, selected: Boolean,
    modifier: Modifier, onClick: () -> Unit
) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(if (selected) RedBg else Color(0xFFF8FAFC))
        .border(1.5.dp, if (selected) Color(0xFFF0A0A8) else Color(0xFFE3E8EF), RoundedCornerShape(14.dp))
        .clickable(onClick = onClick).padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(9.dp).clip(CircleShape).background(dot))
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp,
                color = if (selected) Color(0xFFB02A37) else TextDark)
        }
        Spacer(Modifier.height(4.dp))
        Text(subtitle, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
private fun MaintenanceInfoBanner(icon: ImageVector, title: String, line1: String, line2: String?) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(LightBlueBg).padding(12.dp)) {
        Box(Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFD5E8F4)),
            contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Blue, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextDark)
            Text(line1, fontSize = 11.sp, color = TextDark)
            if (line2 != null) Text(line2, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MaintenancePanelPreview() {
    val parking = Parking(
        id = 1L,
        name = "Parking Plaza Italia",
        address = "Av. Providencia 123",
        commune = "Providencia"
    )
    val service1 = Service(
        id = 1L,
        parkingId = 1L,
        name = "Barrera de entrada",
        type = "barriers",
        status = "operativo",
        detail = "Control de barreras de acceso",
        lastUpdatedAt = System.currentTimeMillis()
    )
    val service2 = Service(
        id = 2L,
        parkingId = 1L,
        name = "Cajero Automático",
        type = "atm",
        status = "fuera-servicio",
        detail = "Terminal de pagos",
        lastUpdatedAt = System.currentTimeMillis(),
        problem = "Pantalla congelada y sin respuesta táctil"
    )
    val techServices = listOf(service1, service2).map(::TechService)

    AppSabaTheme {
        MaintenancePanel(
            services = techServices,
            parkings = listOf(parking),
            selectedParkingId = 1L,
            parkingName = parking.name,
            onSelectParking = {},
            onService = {},
            onReport = {},
            onManageEquipment = {}
        )
    }
}
