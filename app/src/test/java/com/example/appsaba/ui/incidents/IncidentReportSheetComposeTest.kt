package com.example.appsaba.ui.incidents

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.appsaba.domain.model.Service
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IncidentReportSheetComposeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun sinEquiposImpidePublicar() {
        compose.setContent { ReportIncidentSheet(onDismiss = {}, onSubmit = {}) }
        compose.onNodeWithText("No hay equipos en este parking. Agrégalos en Administrar datos locales.").assertExists()
        compose.onNodeWithText("Publicar incidencia").assertIsNotEnabled()
    }

    @Test fun cerrarEjecutaCallback() {
        var dismiss = 0
        compose.setContent { ReportIncidentSheet(onDismiss = { dismiss++ }, onSubmit = {}) }
        compose.onNodeWithContentDescription("Cerrar").performClick()
        assertEquals(1, dismiss)
    }

    @Test fun formularioPublicaIdDeEquipoReal() {
        val service = Service(id = 51, parkingId = 7, name = "Barrera", type = "barriers")
        var published: Ticket? = null
        compose.setContent {
            ReportIncidentSheet(onDismiss = {}, onSubmit = { published = it }, services = listOf(service))
        }
        compose.waitForIdle()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Falla reproducible")
        compose.onNodeWithText("Publicar incidencia").performClick()
        assertNotNull(published)
        assertEquals("51", published?.id)
        assertEquals("Barrera", published?.equipo)
        assertEquals("Falla reproducible", published?.observaciones)
    }
}
