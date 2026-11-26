package com.example.appsaba.ui.home

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service
import org.junit.Assert.*
import org.junit.Test

class HomeUiStateTest {
    private val selected = Parking(id = 3, name = "Centro", address = "A")
    @Test
    fun noSelectedParkingWithoutMatch() {
        assertNull(
            HomeUiState(
                parkings = listOf(selected),
                selectedParkingId = 999
            ).selectedParking
        )
    }

    @Test
    fun selectedParkingLooksUpLocalId() {
        assertEquals(
            selected,
            HomeUiState(parkings = listOf(selected), selectedParkingId = 3).selectedParking
        )
    }

    @Test
    fun outageFlagDetectsOutOfService() {
        val s = Service(parkingId = 3, name = "POS", type = "pos", status = "fuera-servicio")
        assertTrue(HomeUiState(services = listOf(s)).hasOutage)
    }

    @Test
    fun degradedFlagDoesNotConfuseOperationalStatus() {
        val s = Service(parkingId = 3, name = "POS", type = "pos", status = "operativo")
        assertFalse(HomeUiState(services = listOf(s)).hasDegraded)
        assertFalse(HomeUiState(services = listOf(s)).hasOutage)
    }

    @Test
    fun degradedFlagDetectsDegradedEquipment() {
        val s = Service(parkingId = 3, name = "POS", type = "pos", status = "degradado")
        assertTrue(HomeUiState(services = listOf(s)).hasDegraded)
    }
}
