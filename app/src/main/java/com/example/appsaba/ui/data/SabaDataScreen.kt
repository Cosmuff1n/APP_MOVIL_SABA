package com.example.appsaba.ui.data

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
fun SabaDataScreen(onBack: () -> Unit, initialTab: Int = 0) {
    val context = LocalContext.current
    val factory = remember(context) { SabaDataViewModelFactory(context) }
    val model: SabaDataViewModel = viewModel(factory = factory)
    val parkings by model.parkings.collectAsState()
    val services by model.services.collectAsState()
    val incidents by model.incidents.collectAsState()
    val users by model.users.collectAsState()
    val selectedId by model.selectedParkingId.collectAsState()
    val busy by model.busy.collectAsState()
    val feedback by model.feedback.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var tab by remember(initialTab) { mutableIntStateOf(initialTab.coerceIn(0, 3)) }

    LaunchedEffect(parkings, selectedId) {
        if (parkings.isEmpty()) return@LaunchedEffect
        if (selectedId == null || parkings.none { it.id == selectedId }) {
            model.selectParking(parkings.first().id)
        }
    }
    LaunchedEffect(feedback) {
        feedback?.let {
            snackbar.showSnackbar(it)
            model.feedbackShown()
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
                ParkingSelector(parkings, selectedId, model::selectParking)
            }
            when (tab) {
                0 -> ParkingsSection(parkings, busy, model)
                1 -> ServicesSection(services, selectedId, busy, model)
                2 -> IncidentsSection(incidents, services, selectedId, busy, model)
                3 -> UsersSection(users, parkings, selectedId, busy, model)
            }
        }
    }
}
