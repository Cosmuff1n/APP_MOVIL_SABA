package com.example.appsaba.ui.parkings

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.appsaba.domain.model.Parking
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ParkingScreenComposeTest {
    @get:Rule val compose = createComposeRule()
    private val vm = mockk<ParkingViewModel>(relaxed = true)
    private val row = Parking(id = 7, name = "La Florida", address = "Calle A", commune = "La Florida", totalSpaces = 50, freeSpaces = 20)

    @Test fun pantallaVaciaMuestraTituloYMensaje() {
        compose.setContent { ParkingScreen(emptyList(), false, vm) }
        compose.onNodeWithText("1. Estacionamientos").assertExists()
        compose.onNodeWithText("Aún no hay estacionamientos en SQLite.").assertExists()
    }

    @Test fun formularioEnviaCamposAlViewModel() {
        compose.setContent { ParkingScreen(emptyList(), false, vm) }
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Centro")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("Calle 123")
        compose.onAllNodes(hasSetTextAction())[2].performTextInput("Santiago")
        // Los campos de plazas comienzan con "0"; reemplazar con Clear-and-Type no forma parte de esta prueba.
        compose.onNodeWithText("Agregar").performClick()
        verify(exactly = 1) { vm.saveParking(null, "Centro", "Calle 123", "Santiago", "0", "0") }
    }

    @Test fun busyDeshabilitaGuardar() {
        compose.setContent { ParkingScreen(emptyList(), true, vm) }
        compose.onNodeWithText("Agregar").assertIsNotEnabled()
    }

    @Test fun muestraRegistroYPermiteCambiarEstado() {
        compose.setContent { ParkingScreen(listOf(row), false, vm) }
        compose.onNodeWithText("La Florida").assertExists()
        compose.onNodeWithText("Desactivar").performClick()
        verify(exactly = 1) { vm.toggleParking(row) }
    }

    @Test fun editarCargaCamposYGuardaUsandoIdExistente() {
        compose.setContent { ParkingScreen(listOf(row), false, vm) }
        compose.onNodeWithText("Editar").performClick()
        compose.onNodeWithText("Guardar cambios").performClick()
        verify(exactly = 1) { vm.saveParking(7L, "La Florida", "Calle A", "La Florida", "50", "20") }
    }

    @Test fun eliminarPideConfirmacionAntesDeBorrar() {
        compose.setContent { ParkingScreen(listOf(row), false, vm) }
        compose.onNodeWithText("Eliminar").performClick()
        compose.onNodeWithText("Confirmar eliminación").assertExists()
        verify(exactly = 0) { vm.deleteParking(any()) }
        compose.onNodeWithText("Cancelar").performClick()
        verify(exactly = 0) { vm.deleteParking(any()) }
    }

    @Test fun eliminarConfirmadoInvocaViewModel() {
        compose.setContent { ParkingScreen(listOf(row), false, vm) }
        compose.onNodeWithText("Eliminar").performClick()
        // El diálogo y la tarjeta contienen dos botones "Eliminar"; seleccionar el último.
        compose.onAllNodes(androidx.compose.ui.test.hasText("Eliminar"))[1].performClick()
        verify(exactly = 1) { vm.deleteParking(row) }
    }

    @Test fun limpiarRestableceModoCreacion() {
        compose.setContent { ParkingScreen(listOf(row), false, vm) }
        compose.onNodeWithText("Editar").performClick()
        compose.onNodeWithText("Limpiar").performClick()
        compose.onNodeWithText("Agregar").assertExists()
        compose.onNodeWithText("Guardar cambios").assertDoesNotExist()
    }
}
