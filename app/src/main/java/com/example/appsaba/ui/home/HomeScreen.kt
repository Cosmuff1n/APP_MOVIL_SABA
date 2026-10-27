package com.example.appsaba.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appsaba.BuildConfig
import com.example.appsaba.core.jwt.SessionUser
import com.example.appsaba.data.model.Parking
import com.example.appsaba.data.model.Service
import com.example.appsaba.data.repository.SabaRepositories
import com.example.appsaba.ui.theme.AppSabaTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun HomeScreen(
    accessToken: String,
    cognitoAccessToken: String? = null,
    onLogout: () -> Unit = {},
    onOpenLocalData: () -> Unit = {},
    onOpenGuides: () -> Unit = {},
    onReportIncident: () -> Unit = {}
) {
    // El ViewModel ORIGINAL del proyecto principal queda intacto.
    val appContext = LocalContext.current.applicationContext
    val repositories = remember(appContext) { SabaRepositories(appContext) }
    val factory = remember(accessToken, repositories) {
        HomeViewModelFactory(accessToken, repositories)
    }
    val viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = factory
    )
    val state by viewModel.uiState.collectAsState()

    // La interfaz de la compañera consulta Room SOLO PARA LEER, sin cambiar el ViewModel.
    val parkingFlow = remember(repositories) { repositories.parkingRepository.observeActive() }
    val parkings by parkingFlow.collectAsState(initial = emptyList())
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    val selectedParking = parkings.firstOrNull { it.id == selectedId } ?: parkings.firstOrNull()
    val visibleId = selectedParking?.id

    // key reinicia la observacion cuando cambia el estacionamiento; evita datos anteriores.
    key(visibleId) {
        val servicesFlow: Flow<List<Service>> = remember(repositories, visibleId) {
            if (visibleId == null) flowOf(emptyList())
            else repositories.serviceRepository.observeByParking(visibleId)
        }
        val services by servicesFlow.collectAsState(initial = emptyList())
        HomeContent(
            state = state,
            cognitoAccessToken = cognitoAccessToken,
            parkings = parkings,
            selectedParkingId = visibleId,
            services = services,
            onSelectParking = { selectedId = it },
            onLogout = onLogout,
            onOpenLocalData = onOpenLocalData,
            onOpenGuides = onOpenGuides,
            onReportIncident = onReportIncident
        )
    }
}

@Composable
fun HomeContent(
    state: HomeUiState,
    cognitoAccessToken: String? = null,
    parkings: List<Parking> = emptyList(),
    selectedParkingId: Long? = null,
    services: List<Service> = emptyList(),
    onSelectParking: (Long) -> Unit = {},
    onLogout: () -> Unit = {},
    onOpenLocalData: () -> Unit = {},
    onOpenGuides: () -> Unit = {},
    onReportIncident: () -> Unit = {}
) {
    var detail by remember { mutableStateOf<Service?>(null) }
    val parking = parkings.firstOrNull { it.id == selectedParkingId }
    val ownServices = services.filter { it.parkingId == selectedParkingId }

    // Estructura visual, estilos y acciones del Home de la compañera.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Estado operacional",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (parkings.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                parkings.forEach { p ->
                    FilterChip(
                        selected = p.id == selectedParkingId,
                        onClick = { onSelectParking(p.id) },
                        label = { Text(p.name) }
                    )
                }
            }
        }

        if (parking == null) {
            EmptyState(onOpenLocalData)
        } else {
            ParkingStatusCard(parking, ownServices, onServiceClick = { detail = it })
            Button(
                onClick = onReportIncident,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Reportar falla crítica") }
        }

        SessionCard(state, onOpenLocalData, onLogout)
        AmazonExtra(cognitoAccessToken)
    }

    detail?.let { service ->
        ServiceDetailDialog(
            service = service,
            onDismiss = { detail = null },
            onProcedure = { detail = null; onOpenGuides() },
            onReport = { detail = null; onReportIncident() },
            onContactSau = { detail = null; onOpenGuides() }
        )
    }
}

@Composable
private fun ParkingStatusCard(
    parking: Parking,
    services: List<Service>,
    onServiceClick: (Service) -> Unit
) {
    val banner = when {
        services.any { it.status == "fuera-servicio" } -> "Con servicios fuera de servicio"
        services.any { it.status == "degradado" } -> "Con servicios degradados"
        services.isEmpty() -> "Sin servicios registrados"
        else -> "Todo operativo"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                parking.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(banner, style = MaterialTheme.typography.bodyMedium)
            services.forEach { service ->
                ServiceRow(service, onClick = { onServiceClick(service) })
            }
        }
    }
}

@Composable
private fun ServiceRow(service: Service, onClick: () -> Unit) {
    val look = statusLook(service.status)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(look.icon, contentDescription = look.label, tint = look.color)
        Spacer(Modifier.width(12.dp))
        Text(
            service.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(look.label, color = look.color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ServiceDetailDialog(
    service: Service,
    onDismiss: () -> Unit,
    onProcedure: () -> Unit,
    onReport: () -> Unit,
    onContactSau: () -> Unit
) {
    val look = statusLook(service.status)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(service.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Estado: ${look.label}", color = look.color, fontWeight = FontWeight.SemiBold)
                Text("Última actualización: ${formatTime(service.lastUpdatedAt)}")
                service.problem?.takeIf { it.isNotBlank() }?.let { Text("Problema: $it") }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onProcedure, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver procedimiento de primera respuesta")
                }
                OutlinedButton(
                    onClick = onReport,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Reportar incidencia") }
                OutlinedButton(
                    onClick = onContactSau,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Contactar SAU") }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
private fun EmptyState(onOpenLocalData: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Aún no hay estacionamientos registrados.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text("Crea uno (y sus servicios) en la sección de datos locales para ver su estado aquí.")
            Button(onClick = onOpenLocalData) { Text("Administrar datos locales") }
        }
    }
}


@Composable
private fun SessionCard(state: HomeUiState, onOpenLocalData: () -> Unit, onLogout: () -> Unit) {
    var showDetails by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val user = state.user
            if (user != null) {
                Text("Usuario: ${user.rut}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Roles: ${user.roles.joinToString()}",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { showDetails = !showDetails }) {
                    Text(if (showDetails) "Ocultar detalles de sesión" else "Ver detalles de sesión")
                }
                if (showDetails) {
                    Text("ID (sub): ${user.sub}", style = MaterialTheme.typography.bodySmall)
                    Text("Emisor: ${user.iss}", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "Emitido: ${formatEpoch(user.iat)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Expira: ${formatEpoch(user.exp)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (user.isExpired) Text(
                    "Sesión expirada, vuelve a ingresar.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(state.decodeError ?: "Cargando sesión...")
            }
            OutlinedButton(onClick = onOpenLocalData, modifier = Modifier.fillMaxWidth()) {
                Text("Administrar datos locales (Room)")
            }
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cerrar sesión") }
        }
    }
}

private fun formatEpoch(epochSeconds: Long): String {
    if (epochSeconds <= 0L) return "-"
    return SimpleDateFormat(
        "dd-MM-yyyy HH:mm",
        Locale.getDefault()
    ).format(Date(epochSeconds * 1000))
}

/** Complemento visual, no sustituye el Home de la compañera. */
@Composable
private fun AmazonExtra(accessToken: String?) {
    if (BuildConfig.DEBUG && !LocalInspectionMode.current) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Amazon EC2 · Retrofit",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Estacionamientos del servidor y caché Room",
                    style = MaterialTheme.typography.bodySmall
                )
                Ec2ParkingsSection(accessToken = accessToken)
            }
        }
    }
}

private data class StatusLook(val label: String, val color: Color, val icon: ImageVector)

private fun statusLook(status: String): StatusLook = when (status) {
    "operativo" -> StatusLook("Operativo", Color(0xFF2E7D32), Icons.Default.CheckCircle)
    "degradado" -> StatusLook("Degradado", Color(0xFFEF6C00), Icons.Default.Warning)
    else -> StatusLook("Fuera de servicio", Color(0xFFC62828), Icons.Default.Cancel)
}

private fun formatTime(epochMillis: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(epochMillis))

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    val parking = Parking(
        id = 1,
        name = "Parking Plaza Italia",
        address = "Plaza Italia",
        commune = "Providencia"
    )
    AppSabaTheme {
        HomeContent(
            state = HomeUiState(
                user = SessionUser("1234567", listOf("superadmin"), "iss", "1", 0, 0)
            ),
            parkings = listOf(parking),
            selectedParkingId = 1,
            services = listOf(
                Service(1, 1, "Barreras de entrada", "barriers"),
                Service(2, 1, "Lectura LPR", "lpr"),
                Service(3, 1, "Terminales POS", "pos"),
                Service(
                    4,
                    1,
                    "Cajero automático #2",
                    "atm",
                    status = "fuera-servicio",
                    problem = "Pantalla congelada"
                )
            )
        )
    }
}
