package com.example.appsaba.ui.incidents

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Comprueba los caminos de los tickets sin ejecutar Room ni Retrofit. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IncidentTicketActionsComposeTest {
    @get:Rule val compose = createComposeRule()

    private fun ticket(active: Boolean = true, remote: Boolean = false) = Ticket(
        id = "ticket-5", equipo = "Barrera 5", tipoFalla = "No abre", estado = EstadoEquipo.DEGRADADO,
        observaciones = "", activo = active, isRemote = remote
    )

    @Test fun sinTicketsExponeAvisoVacio() {
        compose.setContent { IncidentsContent(emptyList(), false, {}, {}, {}) }
        compose.onNodeWithText("Sin tickets registrados").assertExists()
    }

    @Test fun ticketResueltoNoOfreceBotonResolver() {
        compose.setContent { IncidentsContent(listOf(ticket(active = false)), false, {}, {}, {}) }
        compose.onNodeWithText("Resuelto").assertExists()
        compose.onNodeWithText("Resolver").assertDoesNotExist()
    }

    @Test fun ticketRemotoNoOfreceEliminacionLocal() {
        compose.setContent { IncidentsContent(listOf(ticket(remote = true)), false, {}, {}, {}) }
        compose.onNodeWithText("Barrera 5").assertExists()
        compose.onNodeWithText("Eliminar ticket").assertDoesNotExist()
    }

    @Test fun enBusyResolverYEliminarEstanDeshabilitados() {
        compose.setContent { IncidentsContent(listOf(ticket()), false, {}, {}, {}, busy = true) }
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
        compose.onNodeWithText("Resolver").performClick()
        compose.onNodeWithText("Guardar resolución").assertIsNotEnabled()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Técnico")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("Cambió el motor")
        compose.onNodeWithText("Guardar resolución").performClick()
        assertEquals(1, resolves)
        assertEquals("Técnico", resolvedBy)
        assertEquals("Cambió el motor", solution)
    }

    @Test fun cancelarResolucionNoModificaTicket() {
        var resolves = 0
        compose.setContent {
            IncidentsContent(listOf(ticket()), false, {}, {}, {}, onResolveTicket = { _, _, _ -> resolves++ })
        }
        compose.onNodeWithText("Resolver").performClick()
        compose.onNodeWithText("Cancelar").performClick()
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
        compose.onNodeWithText("Eliminar ticket").performClick()
        assertEquals(0, deletes)
        compose.onNodeWithText("Eliminar").performClick()
        assertEquals(1, deletes)
    }

    @Test fun cancelarEliminacionConservaTicket() {
        var deletes = 0
        compose.setContent {
            IncidentsContent(listOf(ticket()), false, {}, {}, {}, onDeleteTicket = { deletes++ })
        }
        compose.onNodeWithText("Eliminar ticket").performClick()
        compose.onNodeWithText("Cancelar").performClick()
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
