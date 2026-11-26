package com.example.appsaba.ui.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthCardComposeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun renderizaTituloSubtituloYSlot() {
        compose.setContent { AuthCard("SABA", subtitle = "Datos") { Text("Contenido") } }
        compose.onNodeWithText("SABA").assertExists()
        compose.onNodeWithText("Datos").assertExists()
        compose.onNodeWithText("Contenido").assertExists()
    }

    @Test fun noRenderizaSubtituloCuandoEsNulo() {
        compose.setContent { AuthCard("SABA") { Text("Formulario") } }
        compose.onNodeWithText("Formulario").assertExists()
        compose.onNodeWithText("Datos").assertDoesNotExist()
    }

    @Test fun subtituloEnBlancoNoSeRenderizaComoTexto() {
        compose.setContent { AuthCard("SABA", subtitle = "  ") { Text("Formulario") } }
        compose.onNodeWithText("  ").assertDoesNotExist()
    }
}
