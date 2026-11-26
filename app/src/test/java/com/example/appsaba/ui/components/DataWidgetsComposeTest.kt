package com.example.appsaba.ui.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.appsaba.domain.model.Parking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DataWidgetsComposeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun selectorSinParkingsMuestraAviso() {
        compose.setContent { ParkingSelector(emptyList(), null) {} }
        compose.onNodeWithText("Primero crea un estacionamiento en la primera pestaña.").assertExists()
    }

    @Test fun selectorConParkingsMuestraNombresYEnviaIdReal() {
        var idSeleccionado: Long? = null
        val parkings = listOf(Parking(id = 9, name = "Centro", address = "A"), Parking(id = 12, name = "Sur", address = "B"))
        compose.setContent { ParkingSelector(parkings, 9) { idSeleccionado = it } }
        compose.onNodeWithText("Centro").assertExists()
        compose.onNodeWithText("Sur").performClick()
        assertEquals(12L, idSeleccionado)
    }

    @Test fun selectorVacioNoMuestraChips() {
        compose.setContent { ParkingSelector(emptyList(), null) {} }
        compose.onNodeWithText("Centro").assertDoesNotExist()
    }

    @Test fun recordCardMuestraTituloYContenido() {
        compose.setContent { RecordCard("Tarjeta de prueba") { Text("Valor visible") } }
        compose.onNodeWithText("Tarjeta de prueba").assertExists()
        compose.onNodeWithText("Valor visible").assertExists()
    }

    @Test fun confirmDeleteConfirmarEjecutaSoloConfirmacion() {
        var confirmadas = 0
        var canceladas = 0
        compose.setContent { ConfirmDelete("registro de prueba", { confirmadas++ }, { canceladas++ }) }
        compose.onNodeWithText("Confirmar eliminación").assertExists()
        compose.onNodeWithText("Eliminar").performClick()
        assertEquals(1, confirmadas)
        assertEquals(0, canceladas)
    }

    @Test fun confirmDeleteCancelarNoElimina() {
        var confirmadas = 0
        var canceladas = 0
        compose.setContent { ConfirmDelete("registro de prueba", { confirmadas++ }, { canceladas++ }) }
        compose.onNodeWithText("Cancelar").performClick()
        assertEquals(0, confirmadas)
        assertEquals(1, canceladas)
    }
}
