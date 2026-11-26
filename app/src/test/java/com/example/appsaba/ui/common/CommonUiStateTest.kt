package com.example.appsaba.ui.common

import org.junit.Assert.*
import org.junit.Test

class CommonUiStateTest {
    @Test
    fun initialStateIsIdle() {
        assertFalse(CommonUiState().busy)
        assertNull(CommonUiState().feedback)
    }

    @Test
    fun changingBusyFlagDoesNotLoseFeedback() {
        val state = CommonUiState(busy = true, feedback = "En progreso").copy(busy = false)
        assertEquals("En progreso", state.feedback)
        assertFalse(state.busy)
    }
}
