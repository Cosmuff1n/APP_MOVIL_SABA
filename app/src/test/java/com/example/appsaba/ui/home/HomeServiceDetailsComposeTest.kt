package com.example.appsaba.ui.home

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Cubre los estados operacionales y el diálogo de acciones de HomeScreen. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeServiceDetailsComposeTest {
    @get:Rule
    val compose = createComposeRule()
    private val parking = Parking(id = 9, name = "Local Central", address = "Calle 9")
    private fun service(status: String = "operativo", name: String = "Lector de salida") =
        Service(id = 4, parkingId = 9, name = name, type = "lpr", status = status)

    @Test
    fun sinServiciosMuestraEstadoCorrespondiente() {
        compose.setContent {
            HomeContent(
                state = HomeUiState(),
                parkings = listOf(parking),
                selectedParkingId = 9
            )
        }
        compose.onNodeWithText("Sin servicios registrados").assertExists()
    }

    @Test
    fun todosOperativosPresentaEstadoCorrecto() {
        compose.setContent {
            HomeContent(
                state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service())
            )
        }
        compose.onNodeWithText("Todo operativo").assertExists()
        compose.onNodeWithText("Lector de salida").assertExists()
    }

    @Test
    fun unServicioDegradadoAlteraElBanner() {
        compose.setContent {
            HomeContent(
                state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service("degradado"))
            )
        }
        compose.onNodeWithText("Con servicios degradados").assertExists()
    }

    @Test
    fun fueraDeServicioTienePrioridadSobreDegradado() {
        compose.setContent {
            HomeContent(
                state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service("degradado"), service("fuera-servicio", "POS de entrada"))
            )
        }
        compose.onNodeWithText("Con servicios fuera de servicio").assertExists()
    }

    @Test
    fun abrirDetalleYVerProcedimientoLlamaGuia() {
        var guias = 0
        compose.setContent {
            HomeContent(
                state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service()), onOpenGuides = { guias++ })
        }
        compose.onNodeWithText("Lector de salida").performClick()
        compose.onNodeWithText("Ver procedimiento de primera respuesta").performClick()
        assertEquals(1, guias)
    }

    @Test
    fun abrirDetalleYReportarLlamaReporte() {
        var reportes = 0
        compose.setContent {
            HomeContent(
                state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service()), onReportIncident = { reportes++ })
        }
        compose.onNodeWithText("Lector de salida").performClick()
        compose.onNodeWithText("Reportar incidencia").performClick()
        assertEquals(1, reportes)
    }

    @Test
    fun abrirDetalleYContactarSauLlamaGuia() {
        var guias = 0
        compose.setContent {
            HomeContent(
                state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service()), onOpenGuides = { guias++ })
        }
        compose.onNodeWithText("Lector de salida").performClick()
        compose.onNodeWithText("Contactar SAU").performClick()
        assertEquals(1, guias)
    }

    @Test
    fun cerrarDetalleMantienePantallaPrincipal() {
        compose.setContent {
            HomeContent(
                state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service())
            )
        }
        compose.onNodeWithText("Lector de salida").performClick()
        compose.onNodeWithText("Cerrar").performClick()
        compose.onNodeWithText("Estado operacional").assertExists()
    }
}
