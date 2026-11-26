package com.example.appsaba.ui.maintenance

import com.example.appsaba.domain.model.Service
import org.junit.Assert.*
import org.junit.Test

class MaintenanceUiStateTest {
    @Test
    fun emptySelectionHasNoService() {
        val state = MaintenanceUiState()
        assertNull(state.selectedParkingId)
        assertTrue(state.services.isEmpty())
        assertFalse(state.busy)
    }

    @Test
    fun serviceListAndMessageArePreservedByCopy() {
        val service = Service(id = 5, parkingId = 2, name = "Barrera", type = "barrier")
        val state = MaintenanceUiState(selectedParkingId = 2, services = listOf(service))
            .copy(feedback = "Guardado")
        assertEquals(2L, state.selectedParkingId)
        assertEquals(service, state.services.single())
        assertEquals("Guardado", state.feedback)
    }
}
