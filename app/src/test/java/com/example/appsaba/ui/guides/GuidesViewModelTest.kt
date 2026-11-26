package com.example.appsaba.ui.guides

import org.junit.Assert.*
import org.junit.Test

class GuidesViewModelTest {
    @Test
    fun startsWithNoExpandedGuides() {
        assertTrue(GuidesViewModel().uiState.value.expandedGuides.isEmpty())
    }

    @Test
    fun firstToggleExpandsOnlyChosenGuide() {
        val vm = GuidesViewModel()
        vm.toggle("Acceso")
        assertTrue("Acceso" in vm.uiState.value.expandedGuides)
    }

    @Test
    fun secondToggleClosesSameGuide() {
        val vm = GuidesViewModel()
        vm.toggle("Acceso"); vm.toggle("Acceso")
        assertFalse("Acceso" in vm.uiState.value.expandedGuides)
    }

    @Test
    fun openingTwoGuidesDoesNotCollapseOthers() {
        val vm = GuidesViewModel()
        vm.toggle("A"); vm.toggle("B")
        assertEquals(setOf("A", "B"), vm.uiState.value.expandedGuides)
    }
}
