package com.example.appsaba.ui.incidents

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.appsaba.data.model.Parking
import com.example.appsaba.data.model.Service
import com.example.appsaba.ui.data.SabaDataViewModel
import com.example.appsaba.ui.data.SabaDataViewModelFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsaba.ui.theme.AppSabaTheme
import androidx.lifecycle.viewmodel.compose.viewModel

// colores

internal val BluePrimary = Color(0xFF0B6FB8)
internal val BlueDark = Color(0xFF0A5A94)
internal val BlueSoft = Color(0xFFEAF4FB)
internal val BlueBorder = Color(0xFFBFDDF1)
internal val ScreenBg = Color(0xFFF5F7FA)
internal val TextDark = Color(0xFF111827)
internal val TextMuted = Color(0xFF6B7280)
internal val FieldBorder = Color(0xFFD9E2EC)
internal val WarnBg = Color(0xFFFFF6DD)
internal val WarnBorder = Color(0xFFF2B01E)
internal val WarnDot = Color(0xFFF2B01E)
internal val DangerDot = Color(0xFFE5484D)
internal val SuccessGreen = Color(0xFF1E9E6A)
internal val SuccessBg = Color(0xFFE6F6EF)

// Pantalla: el diseño de tu compañero queda intacto; los datos y las acciones
// provienen del MISMO SabaDataViewModel que ya usa Administrar datos locales.
@Composable
fun IncidentsScreen() {
    val context = LocalContext.current
    val factory = remember(context) { SabaDataViewModelFactory(context.applicationContext) }
    val model: SabaDataViewModel = viewModel(factory = factory)
    val parkings by model.parkings.collectAsStateWithLifecycle()
    val selectedId by model.selectedParkingId.collectAsStateWithLifecycle()
    val allServices by model.services.collectAsStateWithLifecycle()
    val allIncidents by model.incidents.collectAsStateWithLifecycle()
    val feedback by model.feedback.collectAsStateWithLifecycle()
    val busy by model.busy.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }

    // Elegir un parking real y no mostrar datos cruzados cuando cambie.
    LaunchedEffect(parkings, selectedId) {
        val valid = selectedId?.takeIf { id -> parkings.any { it.id == id } }
        val target = valid ?: parkings.firstOrNull()?.id
        if (target != null && target != selectedId) model.selectParking(target)
    }
    LaunchedEffect(feedback) {
        feedback?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            model.feedbackShown()
        }
    }
    val selected = parkings.firstOrNull { it.id == selectedId }
    val services = allServices.filter { it.parkingId == selectedId }
    val incidents = allIncidents.filter { it.parkingId == selectedId }
    val tickets = incidents.map { incident ->
        Ticket(
            id = incident.id.toString(),
            equipo = services.firstOrNull { it.id == incident.serviceId }?.name
                ?: "Equipo #${incident.serviceId}",
            tipoFalla = incident.failureType,
            estado = when (incident.status) {
                "fuera-servicio" -> EstadoEquipo.FUERA_DE_SERVICIO
                "degradado" -> EstadoEquipo.DEGRADADO
                else -> EstadoEquipo.FUNCIONAL
            },
            observaciones = incident.description,
            activo = !incident.isResolved
        )
    }
    IncidentsContent(
        tickets = tickets,
        showSheet = showSheet,
        onOpenSheet = { showSheet = true },
        onCloseSheet = { showSheet = false },
        onCreateTicket = { ticket ->
            model.reportIncident(
                selectedId,
                services.firstOrNull { it.id == ticket.id.toLongOrNull() }?.id,
                ticket.tipoFalla,
                ticket.observaciones,
                when (ticket.estado) {
                    EstadoEquipo.FUERA_DE_SERVICIO -> "fuera-servicio"
                    else -> "degradado"
                },
                ""
            )
            showSheet = false
        },
        parkings = parkings,
        selectedParkingId = selectedId,
        services = services,
        onSelectParking = model::selectParking,
        onResolveTicket = { ticket, resolver, solution ->
            incidents.firstOrNull { it.id.toString() == ticket.id }?.let {
                model.resolveIncident(it, resolver, solution)
            }
        },
        onDeleteTicket = { ticket ->
            incidents.firstOrNull { it.id.toString() == ticket.id }?.let(model::deleteIncident)
        },
        busy = busy,
        parkingName = selected?.name ?: "SIN ESTACIONAMIENTO"
    )
}

@Composable
internal fun IncidentsContent(
    tickets: List<Ticket>,
    showSheet: Boolean,
    onOpenSheet: () -> Unit,
    onCloseSheet: () -> Unit,
    onCreateTicket: (Ticket) -> Unit,
    parkings: List<Parking> = emptyList(),
    selectedParkingId: Long? = null,
    services: List<Service> = emptyList(),
    onSelectParking: (Long) -> Unit = {},
    onResolveTicket: (Ticket, String, String) -> Unit = { _, _, _ -> },
    onDeleteTicket: (Ticket) -> Unit = {},
    busy: Boolean = false,
    parkingName: String = "PARKING PLAZA ITALIA"
) {
    val activos = tickets.count { it.activo }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { IncidentsHeader(parkingName) }
        if (parkings.size > 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    parkings.forEach { parking ->
                        FilterChip(
                            selected = selectedParkingId == parking.id,
                            onClick = { onSelectParking(parking.id) },
                            label = { Text(parking.name) }
                        )
                    }
                }
            }
        }
        item { OperationalBanner(activos) }
        item { ReportButton(onClick = onOpenSheet) }
        item {
            Column {
                Text(
                    "HISTORIAL DEL EQUIPO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Todos los tickets",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
            }
        }

        if (tickets.isEmpty()) {
            item { EmptyState() }
        } else {
            items(tickets, key = { it.id }) { ticket ->
                TicketItem(
                    ticket = ticket,
                    onResolve = onResolveTicket,
                    onDelete = onDeleteTicket,
                    busy = busy
                )
            }
        }
    }

    // Comenta este bloque si todavía no creas ReportIncidentSheet.kt
    if (showSheet) {
        ReportIncidentSheet(
            onDismiss = onCloseSheet,
            onSubmit = onCreateTicket,
            services = services
        )
    }
}

@Composable
private fun IncidentsHeader(parkingName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                parkingName.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary,
                letterSpacing = 1.sp
            )
            Text(
                "Incidencias",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
        }

        // Campana de notificaciones con punto rojo
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, FieldBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Notifications, "Notificaciones", tint = TextDark)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(DangerDot)
            )
        }

        Spacer(Modifier.width(8.dp))

        // Avatar con iniciales (luego vendrá del usuario de Cognito)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BluePrimary),
            contentAlignment = Alignment.Center
        ) {
            Text("CD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun OperationalBanner(activas: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(BlueDark, BluePrimary)))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "SEGUIMIENTO OPERACIONAL",
                    color = Color(0xFF9AD7FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Incidencias del parking",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Consulta tickets activos y reparaciones completadas.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$activas",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "ACTIVAS",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ReportButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BlueSoft)
            .border(1.dp, BlueBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BluePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color.White)
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                "Reportar incidencia",
                fontWeight = FontWeight.SemiBold,
                color = BlueDark,
                fontSize = 14.sp
            )
            Text(
                "Registra una nueva falla operacional",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = BlueDark)
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, FieldBorder), RoundedCornerShape(20.dp))
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(SuccessBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = SuccessGreen)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Sin tickets registrados",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = TextDark
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Las nuevas incidencias aparecerán aquí para su seguimiento.",
            fontSize = 11.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TicketItem(
    ticket: Ticket,
    onResolve: (Ticket, String, String) -> Unit,
    onDelete: (Ticket) -> Unit,
    busy: Boolean
) {
    var showResolve by remember(ticket.id) { mutableStateOf(false) }
    var showDelete by remember(ticket.id) { mutableStateOf(false) }
    var resolver by remember(ticket.id) { mutableStateOf("") }
    var solution by remember(ticket.id) { mutableStateOf("") }
    val dotColor = if (!ticket.activo) SuccessGreen else when (ticket.estado) {
        EstadoEquipo.FUNCIONAL -> SuccessGreen
        EstadoEquipo.DEGRADADO -> WarnDot
        EstadoEquipo.FUERA_DE_SERVICIO -> DangerDot
    }
    val estadoTexto = if (!ticket.activo) "Resuelto" else when (ticket.estado) {
        EstadoEquipo.FUNCIONAL -> "Funcional"
        EstadoEquipo.DEGRADADO -> "Degradado"
        EstadoEquipo.FUERA_DE_SERVICIO -> "Fuera de servicio"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    ticket.equipo,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark,
                    modifier = Modifier.weight(1f)
                )
                Text(estadoTexto, fontSize = 11.sp, color = TextMuted)
            }
            Spacer(Modifier.height(4.dp))
            Text(ticket.tipoFalla, color = TextMuted, fontSize = 13.sp)
            if (ticket.observaciones.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(ticket.observaciones, fontSize = 13.sp, color = TextDark)
            }
            // Acciones originales de Room: resolver y eliminar registros reales.
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (ticket.activo) {
                    TextButton(enabled = !busy, onClick = { showResolve = true }) {
                        Text("Resolver")
                    }
                }
                TextButton(enabled = !busy, onClick = { showDelete = true }) {
                    Text("Eliminar ticket", color = DangerDot)
                }
            }
        }
    }
    if (showResolve) {
        AlertDialog(
            onDismissRequest = { showResolve = false },
            title = { Text("Resolver incidencia") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = resolver,
                        onValueChange = { resolver = it },
                        label = { Text("Quién resolvió") }
                    )
                    OutlinedTextField(
                        value = solution,
                        onValueChange = { if (it.length <= 300) solution = it },
                        label = { Text("Qué hizo (máx. 300 caracteres)") }
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = !busy && resolver.isNotBlank() && solution.isNotBlank(),
                    onClick = {
                        onResolve(ticket, resolver, solution)
                        showResolve = false
                    }
                ) { Text("Guardar resolución") }
            },
            dismissButton = { TextButton(onClick = { showResolve = false }) { Text("Cancelar") } }
        )
    }
    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Eliminar ticket") },
            text = { Text("¿Eliminar este ticket guardado en Room?") },
            confirmButton = {
                TextButton(enabled = !busy, onClick = {
                    onDelete(ticket)
                    showDelete = false
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancelar") } }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IncidentsContentPreview() {
    AppSabaTheme {
        IncidentsContent(
            tickets = listOf(
                Ticket(
                    id = "1",
                    equipo = "Barrera de entrada #1",
                    tipoFalla = "No lee tarjetas RFID",
                    estado = EstadoEquipo.DEGRADADO,
                    observaciones = "Revisar antena y cableado",
                    activo = true
                )
            ),
            showSheet = false,
            onOpenSheet = {},
            onCloseSheet = {},
            onCreateTicket = {}
        )
    }
}