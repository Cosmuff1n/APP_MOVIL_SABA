package com.example.appsaba.ui.guides

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GuidesUiStateTest {
    @Test
    fun inicialSinGuiasExpandidas() {
        assertTrue(GuidesUiState().expandedGuides.isEmpty())
    }

    @Test
    fun estadoInmutableConservaLasGuiasElegidas() {
        val state = GuidesUiState().copy(
            expandedGuides = setOf(
                "Reinicio de cajeros",
                "Falla de terminal POS"
            )
        )
        assertEquals(2, state.expandedGuides.size)
        assertTrue("Falla de terminal POS" in state.expandedGuides)
    }
}
