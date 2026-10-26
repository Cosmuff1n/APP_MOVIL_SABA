package com.example.appsaba.navigation

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appsaba.ui.screens.DetailScreen
import com.example.appsaba.ui.screens.GuidesScreen
import com.example.appsaba.ui.screens.HomeScreen
import com.example.appsaba.ui.screens.IncidentsScreen
import com.example.appsaba.ui.screens.LoginScreen
import com.example.appsaba.ui.screens.MaintenanceScreen
import com.example.appsaba.ui.viewmodel.LoginViewModel

// Cada pestaña de la barra inferior: texto, ruta e ícono.
private data class TabItem(val label: String, val route: String, val icon: ImageVector)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Nos dice en qué pantalla estamos ahora mismo.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabs = listOf(
        TabItem("Inicio", Routes.HOME, Icons.Default.Home),
        TabItem("Incidencias", Routes.INCIDENTS, Icons.Default.Warning),
        TabItem("Guías", Routes.GUIDES, Icons.Default.Info),
        TabItem("Mantenimiento", Routes.MAINTENANCE, Icons.Default.Build)
    )

    Scaffold(
        bottomBar = {
            // La barra solo se ve en las 4 pantallas principales.
            // En Login y Detalle no aparece (igual que en Figma).
            if (tabs.any { it.route == currentRoute }) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    // Evita apilar la misma pantalla muchas veces.
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // innerPadding evita que el contenido quede tapado por la barra inferior.
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Routes.LOGIN) {
                val loginViewModel: LoginViewModel = viewModel()
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            // Saca el login del historial: atrás no vuelve a él.
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onViewDetail = { serviceId ->
                        navController.navigate(Routes.detail(serviceId))
                    }
                )
            }

            composable(Routes.INCIDENTS) { IncidentsScreen() }
            composable(Routes.GUIDES) { GuidesScreen() }
            composable(Routes.MAINTENANCE) { MaintenanceScreen() }

            // Ruta con argumento: "detail/atm2"
            composable(
                route = Routes.DETAIL_PATTERN,
                arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
            ) { entry ->
                val serviceId = entry.arguments?.getString("serviceId") ?: ""
                DetailScreen(
                    serviceId = serviceId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
