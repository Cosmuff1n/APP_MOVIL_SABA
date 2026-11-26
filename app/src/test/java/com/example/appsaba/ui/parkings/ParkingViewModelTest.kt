@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.appsaba.ui.parkings

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.repository.ParkingRepository
import com.example.appsaba.testing.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ParkingViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()
    private lateinit var repo: ParkingRepository
    private lateinit var records: MutableStateFlow<List<Parking>>
    private lateinit var vm: ParkingViewModel
    private val sample = Parking(id = 1, name = "Centro", address = "A", isActive = true)
    @Before
    fun setUp() {
        repo = mockk(relaxed = true)
        records = MutableStateFlow(listOf(sample))
        every { repo.observeAll() } returns records
        vm = ParkingViewModel(repo)
    }

    private fun kotlinx.coroutines.test.TestScope.subscribe() =
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

    @Test
    fun observesRepositoryChanges() = runTest {
        subscribe(); runCurrent()
        assertEquals("Centro", vm.uiState.value.parkings.single().name)
        records.value = listOf(sample.copy(name = "Sur")); runCurrent()
        assertEquals("Sur", vm.uiState.value.parkings.single().name)
    }

    @Test
    fun rejectsBlankNameWithoutWriting() = runTest {
        subscribe(); vm.saveParking(null, "  ", "A", "", "10", "5"); runCurrent()
        assertNotNull(vm.uiState.value.feedback)
        coVerify(exactly = 0) { repo.insert(any()) }
    }

    @Test
    fun rejectsNegativeCapacity() = runTest {
        subscribe(); vm.saveParking(null, "N", "A", "", "-1", "0"); runCurrent()
        coVerify(exactly = 0) { repo.insert(any()) }
    }

    @Test
    fun rejectsMoreFreeSpacesThanTotal() = runTest {
        subscribe(); vm.saveParking(null, "N", "A", "", "3", "4"); runCurrent()
        coVerify(exactly = 0) { repo.insert(any()) }
    }

    @Test
    fun rejectsNonNumericCapacity() = runTest {
        subscribe(); vm.saveParking(null, "N", "A", "", "abc", "4"); runCurrent()
        coVerify(exactly = 0) { repo.insert(any()) }
    }

    @Test
    fun createsParkingWithTrimmedFields() = runTest {
        subscribe(); coEvery { repo.insert(any()) } returns 55L
        vm.saveParking(null, "  Nuevo  ", "  Calle  ", "  Ñuñoa  ", "12", "3"); runCurrent()
        coVerify(exactly = 1) { repo.insert(match { it.name == "Nuevo" && it.address == "Calle" && it.commune == "Ñuñoa" && it.totalSpaces == 12 && it.freeSpaces == 3 }) }
        assertFalse(vm.uiState.value.busy)
    }

    @Test
    fun updatesExistingParkingWithoutCreatingAnother() = runTest {
        subscribe(); coEvery { repo.getById(1) } returns sample
        vm.saveParking(1, " Nuevo ", "C", "", "8", "2"); runCurrent()
        coVerify(exactly = 1) { repo.update(match { it.id == 1L && it.name == "Nuevo" }) }
        coVerify(exactly = 0) { repo.insert(any()) }
    }

    @Test
    fun missingParkingShowsActionError() = runTest {
        subscribe(); coEvery { repo.getById(55) } returns null
        vm.saveParking(55, "Nuevo", "C", "", "8", "2"); runCurrent()
        assertTrue(vm.uiState.value.feedback!!.contains("no encontrado"))
    }

    @Test
    fun togglesActiveStatus() = runTest {
        subscribe(); vm.toggleParking(sample); runCurrent()
        coVerify(exactly = 1) { repo.update(match { !it.isActive && it.id == 1L }) }
    }

    @Test
    fun deletesOnlyRequestedParking() = runTest {
        subscribe(); vm.deleteParking(sample); runCurrent()
        coVerify(exactly = 1) { repo.delete(sample) }
    }

    @Test
    fun feedbackCanBeCleared() = runTest {
        subscribe(); vm.saveParking(null, "", "", "", "", ""); runCurrent()
        assertNotNull(vm.uiState.value.feedback)
        vm.feedbackShown(); runCurrent()
        assertNull(vm.uiState.value.feedback)
    }
}
