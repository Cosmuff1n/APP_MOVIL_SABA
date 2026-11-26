@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.appsaba.ui.home

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service
import com.example.appsaba.domain.repository.ParkingRepository
import com.example.appsaba.domain.repository.ServiceRepository
import com.example.appsaba.testing.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()
    private lateinit var parkings: ParkingRepository
    private lateinit var services: ServiceRepository
    private lateinit var vm: HomeViewModel
    private val local = Parking(id = 2, name = "Local", address = "A")
    private val remote =
        Parking(id = -7, name = "Remoto", address = "B", remoteId = 6, revision = 1)
    private val device = Service(id = 3, parkingId = 2, name = "POS", type = "pos")
    @Before
    fun setup() {
        parkings = mockk(relaxed = true); services = mockk(relaxed = true)
        every { parkings.observeActive() } returns MutableStateFlow(listOf(local))
        every { parkings.observeRemote() } returns MutableStateFlow(listOf(remote))
        every { services.observeByParking(any()) } returns flowOf(listOf(device))
        vm = HomeViewModel(parkings, services)
    }

    @Test
    fun initiallyShowsActiveParkingAndItsServices() = runTest {
        runCurrent()
        assertEquals(2L, vm.uiState.value.selectedParkingId)
        assertEquals("Local", vm.uiState.value.parkings.single().name)
        assertEquals("POS", vm.uiState.value.services.single().name)
    }

    @Test
    fun initiallyShowsCachedRemoteParkings() = runTest {
        runCurrent()
        assertEquals(6L, vm.uiState.value.remoteParkings.single().remoteId)
    }

    @Test
    fun selectionUpdatesVisibleParking() = runTest {
        runCurrent(); vm.selectParking(99)
        assertEquals(99L, vm.uiState.value.selectedParkingId)
    }

    @Test
    fun blankRemoteTokenDoesNotContactNetwork() = runTest {
        runCurrent(); vm.setRemoteToken(null); vm.refreshRemote(); runCurrent()
        coVerify(exactly = 0) { parkings.refreshRemote(any()) }
    }

    @Test
    fun tokenTriggersInitialRefresh() = runTest {
        runCurrent(); vm.setRemoteToken("token"); runCurrent()
        coVerify(exactly = 1) { parkings.refreshRemote("token") }
        assertFalse(vm.uiState.value.remoteBusy)
        assertEquals("Sincronizacion correcta con BFF", vm.uiState.value.remoteFeedback)
    }

    @Test
    fun sameTokenDoesNotTriggerAnotherRefresh() = runTest {
        runCurrent(); vm.setRemoteToken("token"); runCurrent(); vm.setRemoteToken("token"); runCurrent()
        coVerify(exactly = 1) { parkings.refreshRemote("token") }
    }

    @Test
    fun bffErrorKeepsLocalCacheAndDisplaysFeedback() = runTest {
        coEvery { parkings.refreshRemote("token") } throws IllegalStateException("offline")
        runCurrent(); vm.setRemoteToken("token"); runCurrent()
        assertTrue(vm.uiState.value.remoteFeedback.contains("Cache Room conservada"))
        assertEquals(6L, vm.uiState.value.remoteParkings.single().remoteId)
    }

    @Test
    fun createRemoteCallsRepositoryWithSessionToken() = runTest {
        runCurrent(); vm.setRemoteToken("token"); runCurrent()
        vm.createRemote("Centro"); runCurrent()
        coVerify { parkings.createRemote("token", "Centro") }
    }

    @Test
    fun renameRemotePassesParkingAndName() = runTest {
        runCurrent(); vm.setRemoteToken("token"); runCurrent()
        vm.renameRemote(remote, "Nuevo"); runCurrent()
        coVerify { parkings.renameRemote("token", remote, "Nuevo") }
    }

    @Test
    fun toggleAndDeleteUseRemoteRepository() = runTest {
        runCurrent(); vm.setRemoteToken("token"); runCurrent()
        vm.toggleRemote(remote); runCurrent(); vm.deleteRemote(remote); runCurrent()
        coVerify { parkings.toggleRemote("token", remote) }
        coVerify { parkings.deleteRemote("token", remote) }
    }
}
