package com.example.appsaba.ui.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsaba.ui.parkings.ParkingsScreen
import com.example.appsaba.ui.parkings.ParkingsViewModel
import com.example.appsaba.ui.maintenance.EquipmentEditorScreen
import com.example.appsaba.ui.maintenance.MaintenanceViewModel
import com.example.appsaba.ui.incidents.IncidentsAdminScreen
import com.example.appsaba.ui.incidents.IncidentsViewModel
import com.example.appsaba.ui.users.UsersScreen
import com.example.appsaba.ui.users.UsersViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private val sections = listOf("Estacionamientos", "Equipos", "Incidencias", "Usuarios")

@Composable
fun SabaDataScreen(
    onBack: () -> Unit,
    initialTab: Int = 0,
    onLogout: (() -> Unit)? = null
) {
    val parkingVm: ParkingsViewModel = viewModel()
    val maintenanceVm: MaintenanceViewModel = viewModel()
    val incidentsVm: IncidentsViewModel = viewModel()
    val usersVm: UsersViewModel = viewModel()

    val parkingState by parkingVm.uiState.collectAsStateWithLifecycle()
    val maintenanceState by maintenanceVm.uiState.collectAsStateWithLifecycle()
    val incidentsState by incidentsVm.uiState.collectAsStateWithLifecycle()
    val usersState by usersVm.uiState.collectAsStateWithLifecycle()
    val parkings = parkingState.parkings
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    val snackbar = remember { SnackbarHostState() }
    var tab by rememberSaveable(initialTab) { mutableIntStateOf(initialTab.coerceIn(0, 3)) }
    val busy = when (tab) {
        0 -> parkingState.busy; 1 -> maintenanceState.busy
        2 -> incidentsState.busy; else -> usersState.busy
    }
    val feedback = when (tab) {
        0 -> parkingState.feedback; 1 -> maintenanceState.feedback
        2 -> incidentsState.feedback; else -> usersState.feedback
    }

    LaunchedEffect(parkings, selectedId) {
        val valid = selectedId?.takeIf { id -> parkings.any { it.id == id } }
        if (valid == null) selectedId = parkings.firstOrNull()?.id
    }
    LaunchedEffect(selectedId) {
        maintenanceVm.selectParking(selectedId)
        incidentsVm.selectParking(selectedId)
    }
    LaunchedEffect(tab, feedback) {
        feedback?.let {
            snackbar.showSnackbar(it)
            when (tab) {
                0 -> parkingVm.feedbackShown(); 1 -> maintenanceVm.feedbackShown()
                2 -> incidentsVm.feedbackShown(); else -> usersVm.feedbackShown()
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBack) { Text("← Inicio") }
                Text("Datos locales de Saba", style = MaterialTheme.typography.titleMedium)
                if (busy) CircularProgressIndicator(modifier = Modifier.padding(6.dp))
            }
            // Room conserva sus registros: cerrar sesión solo limpia la autenticación.
            if (onLogout != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onLogout) { Text("Cerrar sesión") }
                }
            }
            Text(
                "Room + SQLite · Los registros se guardan en este teléfono, no en un servidor.",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall
            )
            ScrollableTabRow(selectedTabIndex = tab) {
                sections.forEachIndexed { index, title ->
                    Tab(selected = tab == index, onClick = { tab = index },
                        text = { Text(title) })
                }
            }
            if (tab != 0) {
                ParkingSelector(parkings, selectedId) { selectedId = it }
            }
            when (tab) {
                0 -> ParkingsScreen(parkings, busy, parkingVm)
                1 -> EquipmentEditorScreen(maintenanceState.services, selectedId, busy, maintenanceVm)
                2 -> IncidentsAdminScreen(incidentsState.incidents, incidentsState.services, selectedId, busy, incidentsVm)
                3 -> UsersScreen(usersState.users, parkings, selectedId, busy, usersVm)
            }
        }
    }
}
