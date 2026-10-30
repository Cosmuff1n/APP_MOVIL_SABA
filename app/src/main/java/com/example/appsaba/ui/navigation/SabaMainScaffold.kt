package com.example.appsaba.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.appsaba.ui.data.SabaDataScreen
import com.example.appsaba.ui.guides.GuidesScreen
import com.example.appsaba.ui.home.HomeScreen
import com.example.appsaba.ui.incidents.IncidentsScreen
import com.example.appsaba.ui.maintenance.MaintenanceScreen

/**
 * Navegación visual aportada por el grupo, adaptada al flujo de sesión original.
 * No crea un segundo login ni sustituye el manejo de tokens de MainActivity.
 */
private enum class MainSection { HOME, INCIDENTS, GUIDES, MAINTENANCE, ALL_DATA }

private data class MainTab(
    val label: String,
    val section: MainSection,
    val icon: ImageVector
)

private val mainTabs = listOf(
    MainTab("Inicio", MainSection.HOME, Icons.Default.Home),
    MainTab("Incidencias", MainSection.INCIDENTS, Icons.Default.Warning),
    MainTab("Guías", MainSection.GUIDES, Icons.Default.Info),
    MainTab("Mantenimiento", MainSection.MAINTENANCE, Icons.Default.Build)
)

@Composable
fun SabaMainScaffold(accessToken: String, cognitoAccessToken: String? = null, onLogout: () -> Unit) {
    var currentSection by rememberSaveable { mutableStateOf(MainSection.HOME) }
    var dataInitialTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            // El formulario de login vive fuera de este Scaffold. El panel completo
            // de datos tiene su propia navegación de regreso a Inicio.
            if (currentSection != MainSection.ALL_DATA) {
                NavigationBar {
                    mainTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentSection == tab.section,
                            onClick = { currentSection = tab.section },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentSection) {
                MainSection.HOME -> HomeScreen(
                    accessToken = accessToken,
                    cognitoAccessToken = cognitoAccessToken,
                    onLogout = onLogout,
                    onOpenLocalData = { dataInitialTab = 0; currentSection = MainSection.ALL_DATA },
                    onOpenGuides = { currentSection = MainSection.GUIDES },
                    onReportIncident = { currentSection = MainSection.INCIDENTS }
                )
                // Pantalla visual del compañero + CRUD original de Room.
                MainSection.INCIDENTS -> IncidentsScreen()
                MainSection.GUIDES -> GuidesScreen()
                MainSection.MAINTENANCE -> MaintenanceScreen(
                    onManageEquipment = { dataInitialTab = 1; currentSection = MainSection.ALL_DATA }
                )
                MainSection.ALL_DATA -> SabaDataScreen(
                    onBack = { currentSection = MainSection.HOME },
                    initialTab = dataInitialTab,
                    onLogout = onLogout
                )
            }
        }
    }
}
