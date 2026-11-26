@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.appsaba.ui.maintenance

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service
import com.example.appsaba.domain.repository.ParkingRepository
import com.example.appsaba.domain.repository.ServiceRepository
import com.example.appsaba.testing.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MaintenanceViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()
    private lateinit var parking: ParkingRepository
    private lateinit var services: ServiceRepository
    private lateinit var vm: MaintenanceViewModel
    private val equipment =
        Service(id = 8, parkingId = 2, name = "Barrera", type = "barrier", problem = "Motor")

    @Before
    fun setup() {
        parking = mockk(relaxed = true); services = mockk(relaxed = true)
        every { parking.observeAll() } returns MutableStateFlow(listOf(Parking(2, "Centro", "A")))
        every { services.observeByParking(any()) } returns flowOf(listOf(equipment))
        vm = MaintenanceViewModel(parking, services)
    }

    private fun kotlinx.coroutines.test.TestScope.subscribe() =
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

    @Test
    fun selectionFiltersServicesByParking() = runTest {
        subscribe(); vm.selectParking(2); runCurrent()
        assertEquals(2L, vm.uiState.value.selectedParkingId)
        assertEquals(8L, vm.uiState.value.services.single().id)
    }

    @Test
    fun noSelectionHasNoServices() = runTest {
        subscribe(); runCurrent()
        assertTrue(vm.uiState.value.services.isEmpty())
    }

    @Test
    fun rejectsMissingParkingOrName() = runTest {
        subscribe(); vm.saveService(null, null, "B", "barrier", ""); runCurrent()
        vm.saveService(null, 2, " ", "barrier", ""); runCurrent()
        coVerify(exactly = 0) { services.insert(any()) }
    }

    @Test
    fun createsTrimmedService() = runTest {
        subscribe(); vm.saveService(null, 2, "  LPR ", "  camara ", "  Entrada "); runCurrent()
        coVerify { services.insert(match { it.parkingId == 2L && it.name == "LPR" && it.type == "camara" && it.detail == "Entrada" }) }
    }

    @Test
    fun editsExistingService() = runTest {
        subscribe(); coEvery { services.getById(8) } returns equipment
        vm.saveService(8, 2, "  Barrera nueva ", "barrier", "Norte"); runCurrent()
        coVerify { services.update(match { it.id == 8L && it.name == "Barrera nueva" }) }
    }

    @Test
    fun cannotSetUnsupportedStatus() = runTest {
        subscribe(); vm.setServiceStatus(equipment, "cerrado"); runCurrent()
        assertEquals("Estado de equipo no válido", vm.uiState.value.feedback)
        coVerify(exactly = 0) { services.updateStatus(any(), any(), any()) }
    }

    @Test
    fun operationalStatusClearsProblem() = runTest {
        subscribe(); vm.setServiceStatus(equipment, "operativo"); runCurrent()
        coVerify { services.updateStatus(8, "operativo", null) }
    }

    @Test
    fun degradedStatusKeepsProblem() = runTest {
        subscribe(); vm.setServiceStatus(equipment, "degradado"); runCurrent()
        coVerify { services.updateStatus(8, "degradado", "Motor") }
    }

    @Test
    fun deletesExistingService() = runTest {
        subscribe(); vm.deleteService(equipment); runCurrent()
        coVerify { services.delete(equipment) }
    }
}
