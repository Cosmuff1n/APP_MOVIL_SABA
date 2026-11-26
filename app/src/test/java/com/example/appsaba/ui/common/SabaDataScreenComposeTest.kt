package com.example.appsaba.ui.common

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.appsaba.ui.incidents.IncidentUiState
import com.example.appsaba.ui.incidents.IncidentViewModel
import com.example.appsaba.ui.maintenance.MaintenanceUiState
import com.example.appsaba.ui.maintenance.MaintenanceViewModel
import com.example.appsaba.ui.parkings.ParkingUiState
import com.example.appsaba.ui.parkings.ParkingViewModel
import com.example.appsaba.ui.users.UsersUiState
import com.example.appsaba.ui.users.UsersViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SabaDataScreenComposeTest {
    @get:Rule val compose = createComposeRule()

    private fun setUpScreen(initialTab: Int = 0, onBack: () -> Unit = {}, onLogout: () -> Unit = {}) {
        val parking = mockk<ParkingViewModel>(relaxed = true)
        val maintenance = mockk<MaintenanceViewModel>(relaxed = true)
        val incidents = mockk<IncidentViewModel>(relaxed = true)
        val users = mockk<UsersViewModel>(relaxed = true)
        every { parking.uiState } returns MutableStateFlow(ParkingUiState())
        every { maintenance.uiState } returns MutableStateFlow(MaintenanceUiState())
        every { incidents.uiState } returns MutableStateFlow(IncidentUiState())
        every { users.uiState } returns MutableStateFlow(UsersUiState())
        val owner = ScreenTestViewModelOwner(parking, maintenance, incidents, users)
        compose.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                SabaDataScreen(onBack = onBack, initialTab = initialTab, onLogout = onLogout)
            }
        }
    }

    @Test fun pintaPestanasYDatosLocales() {
        setUpScreen()
        compose.onNodeWithText("Datos locales de Saba").assertExists()
        compose.onNodeWithText("Estacionamientos").assertExists()
        compose.onNodeWithText("Equipos").assertExists()
        compose.onNodeWithText("Incidencias").assertExists()
        compose.onNodeWithText("Usuarios").assertExists()
    }

    @Test fun cambiarPestanaPresentaFormularioDeUsuarios() {
        setUpScreen()
        compose.onNodeWithText("Usuarios").performClick()
        compose.onNodeWithText("4. Perfiles locales").assertExists()
    }

    @Test fun volverInvocaCallback() {
        var back = 0
        setUpScreen(onBack = { back++ })
        compose.onNodeWithText("← Inicio").performClick()
        assertEquals(1, back)
    }

    @Test fun cerrarSesionInvocaCallback() {
        var logout = 0
        setUpScreen(onLogout = { logout++ })
        compose.onNodeWithText("Cerrar sesión").performClick()
        assertEquals(1, logout)
    }
}
