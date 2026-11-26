package com.example.appsaba.ui.login

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.appsaba.ui.common.ScreenTestViewModelOwner
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LoginScreenComposeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun formularioComunicaCambiosAlControlador() {
        var rut = ""
        var password = ""
        compose.setContent {
            LoginContent(LoginUiState(), { rut = it }, { password = it }, {}, {}, {})
        }
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("12.345.678-5")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("clave")
        assertEquals("12.345.678-5", rut)
        assertEquals("clave", password)
    }

    @Test fun muestraValidacionesYBloqueaBotonDuranteCarga() {
        compose.setContent {
            LoginContent(
                LoginUiState(isLoading = true, rutError = "RUT incorrecto", passwordError = "Clave obligatoria"),
                {}, {}, {}, {}, {}
            )
        }
        compose.onNodeWithText("RUT incorrecto").assertExists()
        compose.onNodeWithText("Clave obligatoria").assertExists()
        compose.onNodeWithText("Ingresar a terreno").assertDoesNotExist() // Durante carga el texto cambia por el indicador de progreso
    }

    @Test fun alternarVisibilidadLlamaCallback() {
        var toggles = 0
        compose.setContent { LoginContent(LoginUiState(), {}, {}, { toggles++ }, {}, {}) }
        compose.onNodeWithContentDescription("Mostrar contraseña").performClick()
        assertEquals(1, toggles)
    }

    @Test fun eventoDeIngresoSeEjecuta() {
        var clicks = 0
        compose.setContent { LoginContent(LoginUiState(), {}, {}, {}, { clicks++ }, {}) }
        compose.onNodeWithText("Ingresar a terreno").performClick()
        assertEquals(1, clicks)
    }

    @Test fun cognitoOpcionInvocaCallbackSinLoginReal() {
        var clicks = 0
        compose.setContent { LoginContent(LoginUiState(), {}, {}, {}, {}, {}, onCognitoClick = { clicks++ }) }
        compose.onNodeWithText("Probar inicio de sesión con Cognito").performClick()
        assertEquals(1, clicks)
    }

    @Test fun loginScreenPermiteSimularViewModelSinEjecutarCognito() {
        val vm = mockk<LoginViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(LoginUiState())
        compose.setContent { LoginScreen(viewModel = vm) }
        compose.onNodeWithText("App Saba").assertExists()
    }
}
