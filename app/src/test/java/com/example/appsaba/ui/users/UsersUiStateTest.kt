package com.example.appsaba.ui.users

import com.example.appsaba.domain.model.User
import org.junit.Assert.*
import org.junit.Test

class UsersUiStateTest {
    @Test
    fun initialStateIsEmptyAndIdle() {
        val state = UsersUiState()
        assertTrue(state.users.isEmpty())
        assertFalse(state.busy)
        assertNull(state.feedback)
    }

    @Test
    fun successfulActionRetainsUserList() {
        val user = User(id = 10, name = "Ana")
        val state = UsersUiState(users = listOf(user), busy = true)
            .copy(busy = false, feedback = "Actualizado")
        assertFalse(state.busy)
        assertEquals(user, state.users.single())
        assertEquals("Actualizado", state.feedback)
    }
}
