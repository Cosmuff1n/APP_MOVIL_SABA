package com.example.appsaba.ui.incidents

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

// Pantalla
@Composable
fun IncidentsScreen(vm: IncidentsViewModel = viewModel()) {
    val tickets by vm.tickets.collectAsStateWithLifecycle()
    val showSheet by vm.showSheet.collectAsStateWithLifecycle()

    IncidentsContent(
        tickets = tickets,
        showSheet = showSheet,
        onOpenSheet = vm::openSheet,
        onCloseSheet = vm::closeSheet,
        onCreateTicket = vm::crearTicket
    )
}

@Composable
internal fun IncidentsContent(
    tickets: List<Ticket>,
    showSheet: Boolean,
    onOpenSheet: () -> Unit,
    onCloseSheet: () -> Unit,
    onCreateTicket: (Ticket) -> Unit
) {
    val activos = tickets.count { it.activo }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { IncidentsHeader() }
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
                TicketItem(ticket)
            }
        }
    }

    // Comenta este bloque si todavía no creas ReportIncidentSheet.kt
    if (showSheet) {
        ReportIncidentSheet(
            onDismiss = onCloseSheet,
            onSubmit = onCreateTicket
        )
    }
}

@Composable
private fun IncidentsHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "PARKING PLAZA ITALIA",
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
private fun TicketItem(ticket: Ticket) {
    val dotColor = when (ticket.estado) {
        EstadoEquipo.FUNCIONAL -> SuccessGreen
        EstadoEquipo.DEGRADADO -> WarnDot
        EstadoEquipo.FUERA_DE_SERVICIO -> DangerDot
    }
    val estadoTexto = when (ticket.estado) {
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
        }
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