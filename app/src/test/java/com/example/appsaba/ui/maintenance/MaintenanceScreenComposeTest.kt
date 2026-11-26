package com.example.appsaba.ui.maintenance

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.appsaba.ui.common.ScreenTestViewModelOwner
import com.example.appsaba.ui.incidents.IncidentUiState
import com.example.appsaba.ui.incidents.IncidentViewModel
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
class MaintenanceScreenComposeTest {
    @get:Rule
    val compose = createComposeRule()

    private fun show(onManage: () -> Unit = {}) {
        val maintenance = mockk<MaintenanceViewModel>(relaxed = true)
        val incidents = mockk<IncidentViewModel>(relaxed = true)
        every { maintenance.uiState } returns MutableStateFlow(MaintenanceUiState())
        every { incidents.uiState } returns MutableStateFlow(IncidentUiState())
        val owner = ScreenTestViewModelOwner(maintenance, incidents)
        compose.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                MaintenanceScreen(onManageEquipment = onManage)
            }
        }
    }

    @Test
    fun muestraEstadoSinEquipos() {
        show()
        compose.onNodeWithText("Panel de mantenimiento").assertExists()
        compose.onNodeWithText("Aún no hay equipos. Registra primero un estacionamiento y sus equipos en Datos locales.")
            .assertExists()
    }

    @Test
    fun botonAdministrarEquiposInvocaNavegacion() {
        var navigations = 0
        show(onManage = { navigations++ })
        compose.onNodeWithText("Administrar equipos locales (Room)").performClick()
        assertEquals(1, navigations)
    }
}
