package com.example.appsaba.ui.incidents

import com.example.appsaba.domain.model.Incident
import org.junit.Assert.*
import org.junit.Test

class IncidentUiStateTest {
    @Test
    fun defaultsKeepReportSheetClosedAndNoRemoteData() {
        val state = IncidentUiState()
        assertTrue(state.incidents.isEmpty())
        assertFalse(state.isReportSheetVisible)
        assertFalse(state.remoteBusy)
        assertFalse(state.remoteAvailable)
        assertNull(state.feedback)
    }

    @Test
    fun copyUpdatesRemoteStateWithoutDiscardingLocalTicket() {
        val incident = Incident(
            ticketCode = "T-1", parkingId = 1L, serviceId = 2L,
            failureType = "POS", description = "Falla"
        )
        val state = IncidentUiState(incidents = listOf(incident))
            .copy(remoteAvailable = true, remoteFeedback = "Sincronizado")
        assertEquals(incident, state.incidents.single())
        assertTrue(state.remoteAvailable)
        assertEquals("Sincronizado", state.remoteFeedback)
    }

    @Test
    fun ticketStatusAndOriginRemainIndependent() {
        val remote = Ticket(
            id = "T-2", equipo = "Barrera", tipoFalla = "Motor",
            estado = EstadoEquipo.DEGRADADO, observaciones = "Revisar", isRemote = true
        )
        assertTrue(remote.activo)
        assertTrue(remote.isRemote)
        assertEquals(EstadoEquipo.DEGRADADO, remote.estado)
        assertFalse(remote.copy(activo = false).activo)
    }
}
