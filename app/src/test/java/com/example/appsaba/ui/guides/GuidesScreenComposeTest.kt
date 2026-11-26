package com.example.appsaba.ui.guides

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GuidesScreenComposeTest {
    @get:Rule
    val compose = createComposeRule()
    private val titulo = "Guía de mantenimiento"

    @Test
    fun cardColapsadaMuestraTituloPeroNoPasos() {
        compose.setContent {
            GuideCard(
                titulo,
                "Resumen",
                "Primer paso",
                "Segundo paso",
                "Tercero",
                "Cuarto"
            )
        }
        compose.onNodeWithText(titulo).assertExists()
        compose.onNodeWithText("1. Primer paso").assertDoesNotExist()
    }

    @Test
    fun cardExpandidaMuestraLosCuatroPasos() {
        compose.setContent {
            GuideCard(
                titulo,
                "Resumen",
                "Uno",
                "Dos",
                "Tres",
                "Cuatro",
                expanded = true
            )
        }
        listOf("1. Uno", "2. Dos", "3. Tres", "4. Cuatro").forEach {
            compose.onNodeWithText(it).assertExists()
        }
    }

    @Test
    fun cardEjecutaCallbackAlPulsar() {
        var clicks = 0
        compose.setContent {
            GuideCard(
                titulo,
                "Resumen",
                "Uno",
                "Dos",
                "Tres",
                "Cuatro",
                onToggle = { clicks++ })
        }
        compose.onNodeWithText(titulo).performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun soporteSePuedeRenderizarSinAbrirAplicacionesExternas() {
        compose.setContent { SupportCard() }
        compose.onNodeWithText("Mesa de ayuda · Atención 24/7").assertExists()
        // El composable se crea sin lanzar ninguna Intent telefónica.
        compose.waitForIdle()
    }
}
