package com.example.appsaba.ui.parkings

import com.example.appsaba.domain.model.Parking
import org.junit.Assert.*
import org.junit.Test

class ParkingUiStateTest {
    @Test
    fun initialStateIsEmptyAndIdle() {
        val state = ParkingUiState()
        assertTrue(state.parkings.isEmpty())
        assertFalse(state.busy)
        assertNull(state.feedback)
    }

    @Test
    fun savingStatePreservesVisibleItems() {
        val parking = Parking(id = 3, name = "Centro", address = "A")
        val state = ParkingUiState(parkings = listOf(parking)).copy(busy = true)
        assertEquals(parking, state.parkings.single())
        assertTrue(state.busy)
    }
}
