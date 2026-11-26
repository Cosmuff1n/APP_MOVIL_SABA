package com.example.appsaba.ui.maintenance

import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service
import com.example.appsaba.ui.common.ScreenTestViewModelOwner
import com.example.appsaba.ui.incidents.IncidentUiState
import com.example.appsaba.ui.incidents.IncidentViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Ejercita selección, navegación, procedimiento y actualización de estado. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w600dp-h1200dp-mdpi")
class MaintenanceDetailsComposeTest {
    @get:Rule val compose = createComposeRule()

    private val parking = Parking(id = 21L, name = "Plaza Norte", address = "A")
    private val service = Service(id = 34L, parkingId = 21L, name = "Barrera entrada", type = "barriers",
        status = "degradado", detail = "Entrada A")
    private val maintenance = mockk<MaintenanceViewModel>(relaxed = true)
    private val incidents = mockk<IncidentViewModel>(relaxed = true)

    private fun show() {
        every { maintenance.uiState } returns MutableStateFlow(
            MaintenanceUiState(parkings = listOf(parking), selectedParkingId = 21L, services = listOf(service))
        )
        every { incidents.uiState } returns MutableStateFlow(IncidentUiState())
        val owner = ScreenTestViewModelOwner(maintenance, incidents)
        compose.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) { MaintenanceScreen() }
        }
    }

    @Test fun estadoOperacionalMuestraServicioDegradado() {
        show()
        compose.onNodeWithText("Barrera entrada").assertExists()
        compose.onNodeWithText("Reportar falla crítica").assertExists()
    }

    @Test fun detallePermiteVerYVolverDelProcedimiento() {
        show()
        compose.onNodeWithText("Barrera entrada").clickAction()
        compose.onNodeWithText("Detalle del servicio").assertExists()
        compose.onNodeWithText("Ver procedimiento").clickAction()
        compose.onNodeWithText("Primera respuesta (guía general)").assertExists()
        compose.onNodeWithText("Volver al estado operacional").clickAction()
        compose.onNodeWithText("Panel de mantenimiento").assertExists()
    }

    @Test fun detalleCambiaEstadoEnViewModel() {
        show()
        compose.onNodeWithText("Barrera entrada").clickAction()
        compose.onNodeWithText("Operativo").clickAction()
        verify(exactly = 1) { maintenance.setServiceStatus(service, "operativo") }
    }

    @Test fun detalleOfreceFormularioDeIncidencia() {
        show()
        compose.onNodeWithText("Barrera entrada").clickAction()
        compose.onNodeWithText("Reportar incidencia").clickAction()
        compose.onNodeWithText("Reportar un incidente").assertExists()
    }
}

/** Ejecuta el click semántico de un control habilitado, sin depender de coordenadas. */
private fun SemanticsNodeInteraction.clickAction() {
    assertIsEnabled()
    performSemanticsAction(SemanticsActions.OnClick) { action -> check(action()) }
}
