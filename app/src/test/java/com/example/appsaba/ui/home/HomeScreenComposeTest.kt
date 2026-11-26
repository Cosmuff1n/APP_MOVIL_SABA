package com.example.appsaba.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.appsaba.domain.model.Parking
import com.example.appsaba.ui.common.ScreenTestViewModelOwner
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w600dp-h1200dp-mdpi")
class HomeScreenComposeTest {
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

    @Test fun sinParkingMuestraEstadoVacioYNavegaADatos() {
        var clicks = 0
        setHomeContent { HomeContent(state = HomeUiState(), onOpenLocalData = { clicks++ }) }
        compose.onNodeWithText("Aún no hay estacionamientos registrados.").assertExists()
        compose.onNodeWithText("Administrar datos locales").clickAction()
        assertEquals(1, clicks)
    }

    @Test fun muestraParkingYAbreReporte() {
        var reportes = 0
        val parking = Parking(id = 2, name = "Plaza Norte", address = "Ruta 1")
        setHomeContent {
            HomeContent(
                state = HomeUiState(), parkings = listOf(parking), selectedParkingId = 2,
                onReportIncident = { reportes++ }
            )
        }
        compose.onNodeWithText("Plaza Norte").assertExists()
        compose.onNodeWithText("Reportar falla crítica").clickAction()
        assertEquals(1, reportes)
    }

    @Test fun permiteElegirParkingEntreDos() {
        var selected = 0L
        val parkings = listOf(Parking(id = 1, name = "Uno", address = "A"), Parking(id = 2, name = "Dos", address = "B"))
        setHomeContent {
            HomeContent(
                state = HomeUiState(), parkings = parkings, selectedParkingId = 1,
                onSelectParking = { selected = it }
            )
        }
        compose.onNodeWithText("Dos").clickAction()
        assertEquals(2L, selected)
    }

    @Test fun homeScreenConectaViewModelConBotonLogout() {
        val vm = mockk<HomeViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(HomeUiState())
        var closes = 0
        val owner = ScreenTestViewModelOwner(vm)
        compose.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                HomeScreen(accessToken = "token-simulado", onLogout = { closes++ })
            }
        }
        compose.onNodeWithText("Cerrar sesión").performClick()
        assertEquals(1, closes)
        verify { vm.setSessionToken("token-simulado") }
    }
}

/** Ejecuta el click semántico de un control habilitado, sin depender de coordenadas. */
private fun SemanticsNodeInteraction.clickAction() {
    assertIsEnabled()
    performSemanticsAction(SemanticsActions.OnClick) { action -> check(action()) }
}
