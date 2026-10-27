package com.example.appsaba.ui.incidents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import com.example.appsaba.data.model.Service
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val fallas = listOf(
    "Falla en cajero automático",
    "Barrera no abre",
    "Sensor defectuoso",
    "Otro"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidentSheet(
    onDismiss: () -> Unit,
    onSubmit: (Ticket) -> Unit,
    services: List<Service> = emptyList()
) {
    var selectedServiceId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(services) {
        if (services.none { it.id == selectedServiceId }) {
            selectedServiceId = services.firstOrNull()?.id
        }
    }
    val equipo = services.firstOrNull { it.id == selectedServiceId }
    var falla by remember { mutableStateOf(fallas[0]) }
    var estado by remember { mutableStateOf(EstadoEquipo.DEGRADADO) }
    var obs by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "NUEVA INCIDENCIA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Reportar un incidente",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ScreenBg)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                }
            }

            FormField("Equipo afectado") {
                if (services.isEmpty()) {
                    Text("No hay equipos en este parking. Agrégalos en Administrar datos locales.", color = TextMuted)
                } else {
                    // Usar ID verdadero de Room, nunca nombres ficticios del Preview.
                    val labels = services.map { "${it.name} (#${it.id})" }
                    val selectedLabel = equipo?.let { "${it.name} (#${it.id})" }.orEmpty()
                    SimpleDropdown(labels, selectedLabel) { label ->
                        selectedServiceId = services.firstOrNull {
                            "${it.name} (#${it.id})" == label
                        }?.id
                    }
                }
            }

            FormField("Tipo de falla") {
                SimpleDropdown(fallas, falla) { falla = it }
            }

            FormField("Estado") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusCard(
                        title = "Degradado",
                        subtitle = "Operación parcial",
                        dot = WarnDot,
                        selectedBg = WarnBg,
                        selectedBorder = WarnBorder,
                        selected = estado == EstadoEquipo.DEGRADADO,
                        modifier = Modifier.weight(1f)
                    ) { estado = EstadoEquipo.DEGRADADO }

                    StatusCard(
                        title = "Fuera de servicio",
                        subtitle = "Fuera de servicio",
                        dot = DangerDot,
                        selectedBg = Color(0xFFFFF0F0),
                        selectedBorder = DangerDot,
                        selected = estado == EstadoEquipo.FUERA_DE_SERVICIO,
                        modifier = Modifier.weight(1f)
                    ) { estado = EstadoEquipo.FUERA_DE_SERVICIO }
                }
            }

            FormField("Observaciones") {
                OutlinedTextField(
                    value = obs,
                    onValueChange = { if (it.length <= 240) obs = it },
                    placeholder = { Text("Describe qué ocurrió, síntomas observados y contexto…") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(14.dp),
                    supportingText = {
                        Text(
                            "${obs.length}/240",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                )
            }

            FormField("Evidencia mediante fotografía o video (Opcional)") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, BlueBorder), RoundedCornerShape(16.dp))
                        .clickable { /* TODO: cámara / galería */ }
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = BlueDark)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Agregar fotografía o video",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Opcional · Captura o selecciona desde la galería",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            InfoCard("Registro automático", "Ticket generado y guardado en Room (SQLite)")
            InfoCard(
                "Actualización en tiempo real",
                "Al publicar, la incidencia se mostrará en el historial local."
            )

            Button(
                onClick = {
                    onSubmit(
                        Ticket(
                            id = selectedServiceId?.toString().orEmpty(),
                            equipo = equipo?.name.orEmpty(),
                            tipoFalla = falla,
                            estado = estado,
                            observaciones = obs
                        )
                    )
                },
                enabled = selectedServiceId != null && falla.isNotBlank() && obs.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text("Publicar incidencia", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FormField(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdown(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    subtitle: String,
    dot: Color,
    selectedBg: Color,
    selectedBorder: Color,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) selectedBg else ScreenBg)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) selectedBorder else FieldBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dot)
            )
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
        Text(subtitle, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
private fun InfoCard(title: String, detail: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BlueSoft)
            .padding(14.dp)
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(detail, fontSize = 11.sp, color = TextMuted)
    }
}