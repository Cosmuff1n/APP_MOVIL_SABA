package com.example.appsaba.ui.maintenance

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
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
class EquipmentEditorScreenComposeTest {
    @get:Rule
    val compose = createComposeRule()
    private val vm = mockk<MaintenanceViewModel>(relaxed = true)
    private val service =
        Service(id = 8, parkingId = 2, name = "Barrera Norte", type = "barriers", detail = "Acceso")

    @Test
    fun sinParkingSolicitaCrearlo() {
        compose.setContent { EquipmentEditorScreen(emptyList(), null, false, vm) }
        compose.onNodeWithText("Crea primero un estacionamiento en la primera pestaña.")
            .assertExists()
    }

    @Test
    fun sinEquiposMuestraEstadoVacio() {
        compose.setContent { EquipmentEditorScreen(emptyList(), 2L, false, vm) }
        compose.onNodeWithText("Todavía no existen equipos para este estacionamiento.")
            .assertExists()
    }

    @Test
    fun agregarEquipoUsaParkingReal() {
        compose.setContent { EquipmentEditorScreen(emptyList(), 2L, false, vm) }
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Cajero")
        compose.onNodeWithText("Agregar equipo").performClick()
        verify(exactly = 1) { vm.saveService(null, 2L, "Cajero", "barriers", "") }
    }

    @Test
    fun busyBloqueaGuardar() {
        compose.setContent { EquipmentEditorScreen(emptyList(), 2L, true, vm) }
        compose.onNodeWithText("Agregar equipo").assertIsNotEnabled()
    }

    @Test
    fun editarEquipoConservaId() {
        compose.setContent { EquipmentEditorScreen(listOf(service), 2L, false, vm) }
        compose.onNodeWithText("Editar").performClick()
        compose.onNodeWithText("Guardar equipo").performClick()
        verify(exactly = 1) { vm.saveService(8L, 2L, "Barrera Norte", "barriers", "Acceso") }
    }

    @Test
    fun cambiarEstadoInvocaViewModel() {
        compose.setContent { EquipmentEditorScreen(listOf(service), 2L, false, vm) }
        compose.onNodeWithText("degradado").performClick()
        verify(exactly = 1) { vm.setServiceStatus(service, "degradado") }
    }

    @Test
    fun eliminarEquipoNecesitaConfirmacion() {
        compose.setContent { EquipmentEditorScreen(listOf(service), 2L, false, vm) }
        compose.onNodeWithText("Eliminar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        verify(exactly = 0) { vm.deleteService(any()) }
    }
}
