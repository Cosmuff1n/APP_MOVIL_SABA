@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.appsaba.ui.common

import com.example.appsaba.testing.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CommonViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private class Subject : CommonViewModel() {
        val state get() = status.value
        fun rejectInput(text: String) = reject(text)
        fun perform(block: suspend () -> Unit) = execute("Listo", block)
    }

    @Test
    fun startsIdleWithoutFeedback() {
        val vm = Subject()
        assertFalse(vm.state.busy)
        assertNull(vm.state.feedback)
    }

    @Test
    fun rejectsInvalidInputWithoutChangingBusyState() {
        val vm = Subject(); vm.rejectInput("Dato inválido")
        assertEquals("Dato inválido", vm.state.feedback)
        assertFalse(vm.state.busy)
    }

    @Test
    fun successfulOperationEmitsCompletionMessage() = runTest {
        val vm = Subject();
        var called = false
        vm.perform { called = true }; runCurrent()
        assertTrue(called)
        assertEquals("Listo", vm.state.feedback)
        assertFalse(vm.state.busy)
    }

    @Test
    fun unexpectedErrorIsConvertedToUserFeedback() = runTest {
        val vm = Subject()
        vm.perform { error("No hay conexión") }; runCurrent()
        assertTrue(vm.state.feedback!!.contains("No hay conexión"))
        assertFalse(vm.state.busy)
    }

    @Test
    fun preventsOverlappingOperations() = runTest {
        val vm = Subject()
        val unblock = CompletableDeferred<Unit>()
        var invoked = 0
        vm.perform { invoked++; unblock.await() }; runCurrent()
        assertTrue(vm.state.busy)
        vm.perform { invoked++ }; runCurrent()
        assertEquals(1, invoked)
        unblock.complete(Unit); runCurrent()
        assertFalse(vm.state.busy)
    }

    @Test
    fun feedbackShownClearsMessage() {
        val vm = Subject(); vm.rejectInput("error")
        vm.feedbackShown()
        assertNull(vm.state.feedback)
    }
}
