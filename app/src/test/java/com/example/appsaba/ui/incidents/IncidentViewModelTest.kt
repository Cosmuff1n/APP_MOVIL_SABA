@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.appsaba.ui.incidents

import android.content.Context
import com.example.appsaba.domain.model.Incident
import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service
import com.example.appsaba.domain.repository.IncidentRepository
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

class IncidentViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()
    private lateinit var parkings: ParkingRepository
    private lateinit var services: ServiceRepository
    private lateinit var repository: IncidentRepository
    private lateinit var vm: IncidentViewModel
    private val localPark = Parking(id = 3, name = "Local", address = "A")
    private val remotePark = Parking(id = -34, name = "BFF", address = "B", remoteId = 33)
    private val service = Service(id = 5, parkingId = 3, name = "Barrera", type = "barrier")
    private val localIncident = Incident(
        id = 11, ticketCode = "L-11", parkingId = 3, serviceId = 5,
        failureType = "Motor", description = "Error"
    )
    private val remoteIncident = Incident(
        remoteId = 55, ticketCode = "R-55", parkingId = 33, serviceId = 5,
        failureType = "POS", description = "Sin red"
    )

    @Before
    fun setup() {
        parkings = mockk(relaxed = true); services = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        val context: Context = mockk(relaxed = true)
        every { parkings.observeAll() } returns MutableStateFlow(listOf(localPark))
        every { parkings.observeRemote() } returns MutableStateFlow(listOf(remotePark))
        every { services.observeByParking(any()) } returns flowOf(listOf(service))
        every { repository.observeByParking(any()) } returns flowOf(listOf(localIncident))
        vm = IncidentViewModel(parkings, services, repository, context)
    }

    private fun kotlinx.coroutines.test.TestScope.subscribe() =
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

    @Test
    fun selectingParkingShowsOnlyMatchingLocalIncidents() = runTest {
        subscribe(); vm.selectParking(3); runCurrent()
        assertEquals(11L, vm.uiState.value.incidents.single().id)
        assertEquals(5L, vm.uiState.value.services.single().id)
    }

    @Test
    fun noSelectionHasNoVisibleIncidents() = runTest {
        subscribe(); runCurrent()
        assertTrue(vm.uiState.value.incidents.isEmpty())
    }

    @Test
    fun sheetVisibilityCanBeChanged() = runTest {
        subscribe(); vm.openSheet(); runCurrent()
        assertTrue(vm.uiState.value.isReportSheetVisible)
        vm.closeSheet(); runCurrent()
        assertFalse(vm.uiState.value.isReportSheetVisible)
    }

    @Test
    fun invalidReportDoesNotInsertIntoRoom() = runTest {
        subscribe(); vm.reportIncident(null, 5, "Motor", "Falla", "degradado", "Ana"); runCurrent()
        vm.reportIncident(3, 5, "Motor", "x".repeat(241), "degradado", "Ana"); runCurrent()
        vm.reportIncident(3, 5, "Motor", "Falla", "no-admitido", "Ana"); runCurrent()
        coVerify(exactly = 0) { repository.insert(any()) }
        assertNotNull(vm.uiState.value.feedback)
    }

    @Test
    fun validReportCreatesLocalTicketWithoutEvidence() = runTest {
        subscribe(); coEvery { repository.insert(any()) } returns 77
        vm.reportIncident(3, 5, "  Motor  ", "  Falla  ", "degradado", "  Ana  "); runCurrent()
        coVerify { repository.insert(match { it.ticketCode.startsWith("SABA-") && it.parkingId == 3L && it.serviceId == 5L && it.failureType == "Motor" && it.description == "Falla" && it.reporterName == "Ana" && it.evidenceUri == null }) }
    }

    @Test
    fun blankTokenDoesNotCallBff() = runTest {
        subscribe(); vm.setRemoteToken(" "); vm.refreshRemote(); runCurrent()
        coVerify(exactly = 0) { repository.listRemote(any()) }
    }

    @Test
    fun tokenRefreshesRemoteIncidentsButDoesNotInsertIntoRoom() = runTest {
        subscribe(); coEvery { repository.listRemote("token") } returns listOf(remoteIncident)
        vm.selectParking(-34); vm.setRemoteToken("token"); runCurrent()
        assertTrue(vm.uiState.value.remoteAvailable)
        assertEquals(55L, vm.uiState.value.incidents.single().remoteId)
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun remoteFailureIsReportedWithoutLocalMutation() = runTest {
        subscribe(); coEvery { repository.listRemote("token") } throws IllegalStateException("sin red")
        vm.setRemoteToken("token"); runCurrent()
        assertTrue(vm.uiState.value.remoteFeedback!!.contains("Sin conexión"))
        assertFalse(vm.uiState.value.remoteBusy)
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun changingTokenClearsOldRemoteData() = runTest {
        subscribe(); coEvery { repository.listRemote("token") } returns listOf(remoteIncident)
        vm.selectParking(-34); vm.setRemoteToken("token"); runCurrent()
        assertEquals(1, vm.uiState.value.incidents.size)
        vm.setRemoteToken(null); runCurrent()
        assertFalse(vm.uiState.value.remoteAvailable)
        assertTrue(vm.uiState.value.incidents.isEmpty())
    }

    @Test
    fun resolvingLocalIncidentUpdatesOnlyRoom() = runTest {
        subscribe(); vm.resolveIncident(localIncident, " Ana ", " Reinicio "); runCurrent()
        coVerify { repository.update(match { it.id == 11L && it.isResolved && it.resolvedByName == "Ana" && it.resolutionDescription == "Reinicio" }) }
        coVerify(exactly = 0) { repository.resolveRemote(any(), any(), any(), any()) }
    }

    @Test
    fun invalidResolutionIsRejected() = runTest {
        subscribe(); vm.resolveIncident(localIncident, "Ana", "x".repeat(301)); runCurrent()
        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun resolvingRemoteTicketUsesBffAndRefreshes() = runTest {
        subscribe(); coEvery { repository.listRemote("token") } returns listOf(remoteIncident)
        coEvery {
            repository.resolveRemote(
                any(),
                any(),
                any(),
                any()
            )
        } returns remoteIncident.copy(isResolved = true)
        vm.setRemoteToken("token"); runCurrent()
        vm.resolveIncident(remoteIncident, "Ana", "Listo"); runCurrent()
        coVerify { repository.resolveRemote("token", remoteIncident, "Ana", "Listo") }
        coVerify(exactly = 2) { repository.listRemote("token") }
    }

    @Test
    fun remoteDeleteIsBlockedBecauseEndpointDoesNotExist() = runTest {
        subscribe(); vm.deleteIncident(remoteIncident); runCurrent()
        assertEquals("El backend de Incidencias no dispone de DELETE", vm.uiState.value.feedback)
        coVerify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun localDeleteUsesRoom() = runTest {
        subscribe(); vm.deleteIncident(localIncident); runCurrent()
        coVerify(exactly = 1) { repository.delete(localIncident) }
    }
}
