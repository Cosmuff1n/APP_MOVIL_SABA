package com.example.appsaba.ui.incidents

import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Comprueba los caminos de los tickets sin ejecutar Room ni Retrofit. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w600dp-h1200dp-mdpi")
class IncidentTicketActionsComposeTest {
    @get:Rule val compose = createComposeRule()

    private fun ticket(active: Boolean = true, remote: Boolean = false) = Ticket(
        id = "ticket-5", equipo = "Barrera 5", tipoFalla = "No abre", estado = EstadoEquipo.DEGRADADO,
        observaciones = "", activo = active, isRemote = remote
    )

    @Test fun sinTicketsExponeAvisoVacio() {
        compose.setContent { IncidentsContent(emptyList(), false, {}, {}, {}) }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Sin tickets registrados"))
        compose.onNodeWithText("Sin tickets registrados").assertExists()
    }

    @Test fun ticketResueltoNoOfreceBotonResolver() {
        compose.setContent { IncidentsContent(listOf(ticket(active = false)), false, {}, {}, {}) }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Resuelto"))
        compose.onNodeWithText("Resuelto").assertExists()
        compose.onNodeWithText("Resolver").assertDoesNotExist()
    }

    @Test fun ticketRemotoNoOfreceEliminacionLocal() {
        compose.setContent { IncidentsContent(listOf(ticket(remote = true)), false, {}, {}, {}) }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Barrera 5"))
        compose.onNodeWithText("Barrera 5").assertExists()
        compose.onNodeWithText("Eliminar ticket").assertDoesNotExist()
    }

    @Test fun enBusyResolverYEliminarEstanDeshabilitados() {
        compose.setContent { IncidentsContent(listOf(ticket()), false, {}, {}, {}, busy = true) }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Barrera 5"))
        compose.onNodeWithText("Resolver").assertIsNotEnabled()
        compose.onNodeWithText("Eliminar ticket").assertIsNotEnabled()
    }

    @Test fun resolverValidaCamposYEnvíaDatos() {
        var resolvedBy = ""
        var solution = ""
        var resolves = 0
        val row = ticket()
        compose.setContent {
            IncidentsContent(listOf(row), false, {}, {}, {},
                onResolveTicket = { t, by, fix ->
                    assertEquals(row, t)
                    resolvedBy = by
                    solution = fix
                    resolves++
                })
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Barrera 5"))
        compose.onNodeWithText("Resolver").clickAction()
        compose.onNodeWithText("Guardar resolución").assertIsNotEnabled()
        compose.onAllNodes(hasSetTextAction())[0].performTextReplacement("Técnico")
        compose.onAllNodes(hasSetTextAction())[1].performTextReplacement("Cambió el motor")
        compose.onNodeWithText("Guardar resolución").clickAction()
        assertEquals(1, resolves)
        assertEquals("Técnico", resolvedBy)
        assertEquals("Cambió el motor", solution)
    }

    @Test fun cancelarResolucionNoModificaTicket() {
        var resolves = 0
        compose.setContent {
            IncidentsContent(listOf(ticket()), false, {}, {}, {}, onResolveTicket = { _, _, _ -> resolves++ })
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Barrera 5"))
        compose.onNodeWithText("Resolver").clickAction()
        compose.onNodeWithText("Cancelar").clickAction()
        assertEquals(0, resolves)
        compose.onNodeWithText("Resolver").assertExists()
    }

    @Test fun eliminarTicketLocalRequiereConfirmacion() {
        val row = ticket()
        var deletes = 0
        compose.setContent {
            IncidentsContent(listOf(row), false, {}, {}, {}, onDeleteTicket = {
                assertEquals(row, it); deletes++
            })
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Barrera 5"))
        compose.onNodeWithText("Eliminar ticket").clickAction()
        assertEquals(0, deletes)
        compose.onNodeWithText("Eliminar").clickAction()
        assertEquals(1, deletes)
    }

    @Test fun cancelarEliminacionConservaTicket() {
        var deletes = 0
        compose.setContent {
            IncidentsContent(listOf(ticket()), false, {}, {}, {}, onDeleteTicket = { deletes++ })
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Barrera 5"))
        compose.onNodeWithText("Eliminar ticket").clickAction()
        compose.onNodeWithText("Cancelar").clickAction()
        assertEquals(0, deletes)
    }

    @Test fun remoteBusyDeshabilitaActualizarBff() {
        compose.setContent {
            IncidentsContent(emptyList(), false, {}, {}, {}, remoteAvailable = true, remoteBusy = true)
        }
        compose.onNodeWithText("Consultando...").assertIsNotEnabled()
    }

    @Test fun remoteFeedbackSeMuestraAlUsuario() {
        compose.setContent {
            IncidentsContent(emptyList(), false, {}, {}, {}, remoteAvailable = true,
                remoteFeedback = "Respuesta de prueba")
        }
        compose.onNodeWithText("Respuesta de prueba").assertExists()
    }
}

/** Ejecuta el click semántico de un control habilitado, sin depender de coordenadas. */
private fun SemanticsNodeInteraction.clickAction() {
    assertIsEnabled()
    performSemanticsAction(SemanticsActions.OnClick) { action -> check(action()) }
}
