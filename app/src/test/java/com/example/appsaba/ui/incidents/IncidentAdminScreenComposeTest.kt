package com.example.appsaba.ui.incidents

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.appsaba.domain.model.Service
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IncidentAdminScreenComposeTest {
    @get:Rule
    val compose = createComposeRule()
    private val vm = mockk<IncidentViewModel>(relaxed = true)

    @Test
    fun sinParkingExplicaRequisito() {
        compose.setContent { IncidentsAdminScreen(emptyList(), emptyList(), null, false, vm) }
        compose.onNodeWithText("Primero debes crear un estacionamiento.").assertExists()
    }

    @Test
    fun sinEquiposExplicaRequisito() {
        compose.setContent { IncidentsAdminScreen(emptyList(), emptyList(), 1, false, vm) }
        compose.onNodeWithText("Este estacionamiento no tiene equipos. Crea uno en Equipos.")
            .assertExists()
    }

    @Test
    fun formularioReportaConIdRealYSuGravedad() {
        val service = Service(id = 8, parkingId = 2, name = "POS", type = "pos")
        compose.setContent { IncidentsAdminScreen(emptyList(), listOf(service), 2, false, vm) }
        compose.onNodeWithText("POS").performClick()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Sin señal")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("Falla de red")
        compose.onNodeWithText("Registrar ticket local").performClick()
        verify(exactly = 1) {
            vm.reportIncident(
                2L,
                8L,
                "Sin señal",
                "Falla de red",
                "degradado",
                ""
            )
        }
    }
}
