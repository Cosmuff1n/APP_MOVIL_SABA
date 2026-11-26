package com.example.appsaba.ui.home

import kotlinx.coroutines.flow.MutableStateFlow
import io.mockk.mockk
import io.mockk.every
import com.example.appsaba.ui.common.ScreenTestViewModelOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
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
@Config(sdk = [34], qualifiers = "w600dp-h1200dp-mdpi")
class HomeServiceDetailsComposeTest {
    @get:Rule val compose = createComposeRule()

    private fun setHomeContent(content: @Composable () -> Unit) {
        val vm = mockk<HomeViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(HomeUiState())
        val owner = ScreenTestViewModelOwner(vm)
        compose.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                content()
            }
        }
    }
    private val parking = Parking(id = 9, name = "Local Central", address = "Calle 9")
    private fun service(status: String = "operativo", name: String = "Lector de salida") =
        Service(id = 4, parkingId = 9, name = name, type = "lpr", status = status)

    @Test fun sinServiciosMuestraEstadoCorrespondiente() {
        setHomeContent { HomeContent(state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9) }
        compose.onNodeWithText("Sin servicios registrados").assertExists()
    }

    @Test fun todosOperativosPresentaEstadoCorrecto() {
        setHomeContent {
            HomeContent(state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service()))
        }
        compose.onNodeWithText("Todo operativo").assertExists()
        compose.onNodeWithText("Lector de salida").assertExists()
    }

    @Test fun unServicioDegradadoAlteraElBanner() {
        setHomeContent {
            HomeContent(state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service("degradado")))
        }
        compose.onNodeWithText("Con servicios degradados").assertExists()
    }

    @Test fun fueraDeServicioTienePrioridadSobreDegradado() {
        setHomeContent {
            HomeContent(state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service("degradado"), service("fuera-servicio", "POS de entrada")))
        }
        compose.onNodeWithText("Con servicios fuera de servicio").assertExists()
    }

    @Test fun abrirDetalleYVerProcedimientoLlamaGuia() {
        var guias = 0
        setHomeContent {
            HomeContent(state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service()), onOpenGuides = { guias++ })
        }
        compose.onNodeWithText("Lector de salida").clickAction()
        compose.onNodeWithText("Ver procedimiento de primera respuesta").clickAction()
        assertEquals(1, guias)
    }

    @Test fun abrirDetalleYReportarLlamaReporte() {
        var reportes = 0
        setHomeContent {
            HomeContent(state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service()), onReportIncident = { reportes++ })
        }
        compose.onNodeWithText("Lector de salida").clickAction()
        compose.onNodeWithText("Reportar incidencia").clickAction()
        assertEquals(1, reportes)
    }

    @Test fun abrirDetalleYContactarSauLlamaGuia() {
        var guias = 0
        setHomeContent {
            HomeContent(state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service()), onOpenGuides = { guias++ })
        }
        compose.onNodeWithText("Lector de salida").clickAction()
        compose.onNodeWithText("Contactar SAU").clickAction()
        assertEquals(1, guias)
    }

    @Test fun cerrarDetalleMantienePantallaPrincipal() {
        setHomeContent {
            HomeContent(state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 9,
                services = listOf(service()))
        }
        compose.onNodeWithText("Lector de salida").clickAction()
        compose.onNodeWithText("Cerrar").clickAction()
        compose.onNodeWithText("Estado operacional").assertExists()
    }
}

/** Ejecuta el click semántico de un control habilitado, sin depender de coordenadas. */
private fun SemanticsNodeInteraction.clickAction() {
    assertIsEnabled()
    performSemanticsAction(SemanticsActions.OnClick) { action -> check(action()) }
}
