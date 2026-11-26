package com.example.appsaba.ui.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.appsaba.ui.common.ScreenTestViewModelOwner
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Ec2ParkingsSectionComposeTest {
    @get:Rule val compose = createComposeRule()
    private val vm = mockk<HomeViewModel>(relaxed = true)

    private fun show(token: String?, state: HomeUiState = HomeUiState()) {
        every { vm.uiState } returns MutableStateFlow(state)
        val owner = ScreenTestViewModelOwner(vm)
        compose.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                Ec2ParkingsSection(token)
            }
        }
    }

    @Test fun sinCognitoInformaQueNoPuedeOperarRemotamente() {
        show(null)
        compose.onNodeWithText("Para operaciones remotas entra con Cognito. Room local sigue disponible.").assertExists()
        compose.onNodeWithText("Actualizar desde BFF").assertDoesNotExist()
    }

    @Test fun conTokenSeOfrecenAccionesYActualiza() {
        show("token-simulado")
        compose.onNodeWithText("Actualizar desde BFF").performClick()
        verify(exactly = 1) { vm.refreshRemote() }
    }
}
