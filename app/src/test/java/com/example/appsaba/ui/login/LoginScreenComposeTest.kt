package com.example.appsaba.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
@Config(sdk = [34], qualifiers = "w600dp-h1200dp-mdpi")
class LoginScreenComposeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun formularioComunicaCambiosAlControlador() {
        var rut = ""
        var password = ""
        compose.setContent {
            var state by remember { mutableStateOf(LoginUiState()) }
            LoginContent(state, { rut = it; state = state.copy(rut = it) },
                { password = it; state = state.copy(password = it) }, {}, {}, {})
        }
        compose.onAllNodes(hasSetTextAction())[0].performTextReplacement("12.345.678-5")
        compose.onAllNodes(hasSetTextAction())[1].performTextReplacement("clave")
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
        compose.setContent { LoginContent(LoginUiState(rut = "12.345.678-5", password = "Password123"), {}, {}, {}, { clicks++ }, {}) }
        compose.onNodeWithText("Ingresar a terreno").clickAction()
        assertEquals(1, clicks)
    }

    @Test fun cognitoOpcionInvocaCallbackSinLoginReal() {
        var clicks = 0
        compose.setContent { LoginContent(LoginUiState(), {}, {}, {}, {}, {}, onCognitoClick = { clicks++ }) }
        compose.onNodeWithText("Probar inicio de sesión con Cognito").clickAction()
        assertEquals(1, clicks)
    }

    @Test fun loginScreenPermiteSimularViewModelSinEjecutarCognito() {
        val vm = mockk<LoginViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(LoginUiState())
        compose.setContent { LoginScreen(viewModel = vm) }
        compose.onNodeWithText("App Saba").assertExists()
    }
}

/** Ejecuta el click semántico de un control habilitado, sin depender de coordenadas. */
private fun SemanticsNodeInteraction.clickAction() {
    assertIsEnabled()
    performSemanticsAction(SemanticsActions.OnClick) { action -> check(action()) }
}
