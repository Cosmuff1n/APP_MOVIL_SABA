package com.example.appsaba.ui.incidents

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
@Config(sdk = [34])
class IncidentScreenComposeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun vacioMuestraHistorialYPermiteAbrirFormulario() {
        var opens = 0
        compose.setContent {
            IncidentsContent(emptyList(), false, { opens++ }, {}, {})
        }
        compose.onNodeWithText("Todos los tickets").assertExists()
        compose.onNodeWithText("Reportar incidencia").performClick()
        assertEquals(1, opens)
    }

    @Test fun sinCorrespondenciaRemotaOcultaFormularioLocal() {
        compose.setContent {
            IncidentsContent(emptyList(), false, {}, {}, {}, allowLocalReport = false)
        }
        compose.onNodeWithText("Reportar incidencia").assertDoesNotExist()
    }

    @Test fun dosParkingsPermitenSeleccion() {
        val parkings = listOf(Parking(id = 1, name = "Norte", address = "A"), Parking(id = 2, name = "Sur", address = "B"))
        var selected: Long? = null
        compose.setContent {
            IncidentsContent(emptyList(), false, {}, {}, {}, parkings = parkings,
                selectedParkingId = 1, onSelectParking = { selected = it })
        }
        compose.onNodeWithText("Sur").performClick()
        assertEquals(2L, selected)
    }

    @Test fun actualizarBffInvocaCallback() {
        var refreshes = 0
        compose.setContent {
            IncidentsContent(emptyList(), false, {}, {}, {}, remoteAvailable = true,
                onRefreshRemote = { refreshes++ })
        }
        compose.onNodeWithText("Actualizar").performClick()
        assertEquals(1, refreshes)
    }

    @Test fun incidentScreenUsaElViewModelConfigurado() {
        val vm = mockk<IncidentViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(IncidentUiState())
        val owner = ScreenTestViewModelOwner(vm)
        compose.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                IncidentScreen(cognitoAccessToken = null)
            }
        }
        compose.onNodeWithText("Todos los tickets").assertExists()
        verify { vm.setRemoteToken(null) }
    }
}
