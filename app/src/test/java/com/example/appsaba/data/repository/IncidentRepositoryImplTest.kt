package com.example.appsaba.data.repository

import com.example.appsaba.data.local.datasource.IncidentLocalDataSource
import com.example.appsaba.data.local.entity.IncidentEntity
import com.example.appsaba.data.remote.datasource.IncidentRemoteDataSource
import com.example.appsaba.data.remote.dto.BffIncidentDto
import com.example.appsaba.domain.model.Incident
import com.example.appsaba.domain.model.Service
import com.example.appsaba.domain.repository.ServiceRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class IncidentRepositoryImplTest {
    private lateinit var local: IncidentLocalDataSource
    private lateinit var services: ServiceRepository
    private lateinit var remote: IncidentRemoteDataSource
    private lateinit var subject: IncidentRepositoryImpl
    private val incident = Incident(
        id = 1, ticketCode = "T-1", parkingId = 4,
        serviceId = 3, failureType = "POS", description = "Error"
    )
    private val dto = BffIncidentDto(
        id = 5, ticketCode = "T-5", parkingId = 4,
        serviceId = 3, failureType = "POS", description = "Error", status = "degradado"
    )

    @Before
    fun setup() {
        local = mockk(relaxed = true); remote = mockk(relaxed = true); services =
            mockk(relaxed = true)
        subject = IncidentRepositoryImpl(local, services, remote)
    }

    private fun validService() = Service(id = 3, parkingId = 4, name = "POS", type = "pos")
    @Test
    fun observesLocalOnlyWithRoomIdentity() = runTest {
        every { local.observeByParking(4) } returns flowOf(
            listOf(
                IncidentEntity(
                    1,
                    "T-1",
                    4,
                    3,
                    failureType = "POS",
                    description = "Error"
                )
            )
        )
        val result = subject.observeByParking(4).first().single()
        assertEquals(1L, result.id)
        assertNull(result.remoteId)
    }

    @Test
    fun insertValidatesServiceParkingOwnership() = runTest {
        coEvery { services.getById(3) } returns validService()
        coEvery { local.insert(any()) } returns 100
        assertEquals(100L, subject.insert(incident))
        coVerify { local.insert(match { it.ticketCode == "T-1" }) }
    }

    @Test
    fun insertWithoutExistingServiceIsRejected() = runTest {
        coEvery { services.getById(3) } returns null
        assertTrue(runCatching { subject.insert(incident) }.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { local.insert(any()) }
    }

    @Test
    fun insertWithServiceFromAnotherParkingIsRejected() = runTest {
        coEvery { services.getById(3) } returns validService().copy(parkingId = 99)
        assertTrue(runCatching { subject.insert(incident) }.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { local.insert(any()) }
    }

    @Test
    fun remoteIncidentCannotBeWrittenIntoRoom() = runTest {
        assertTrue(runCatching { subject.insert(incident.copy(remoteId = 22)) }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { subject.update(incident.copy(remoteId = 22)) }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { subject.delete(incident.copy(remoteId = 22)) }.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { local.insert(any()) }
        coVerify(exactly = 0) { local.update(any()) }
        coVerify(exactly = 0) { local.delete(any()) }
    }

    @Test
    fun localUpdateAndDeleteDelegateCorrectly() = runTest {
        coEvery { services.getById(3) } returns validService()
        coEvery { local.update(any()) } returns 1
        coEvery { local.delete(any()) } returns 1
        assertEquals(1, subject.update(incident))
        assertEquals(1, subject.delete(incident))
        coVerify(exactly = 1) { local.update(any()) }
        coVerify(exactly = 1) { local.delete(any()) }
    }

    @Test
    fun listRemoteKeepsPostgresIdentity() = runTest {
        coEvery { remote.list("token") } returns listOf(dto)
        assertEquals(5L, subject.listRemote("token").single().remoteId)
        coVerify(exactly = 0) { local.insert(any()) }
    }

    @Test
    fun createRemoteValidatesIdentifiersAndText() = runTest {
        val error1 = runCatching {
            subject.createRemote(
                "token",
                0,
                1,
                "",
                "POS",
                "Falla",
                "degradado"
            )
        }.exceptionOrNull()
        val error2 = runCatching {
            subject.createRemote(
                "token",
                1,
                0,
                "",
                "POS",
                "Falla",
                "degradado"
            )
        }.exceptionOrNull()
        val error3 = runCatching {
            subject.createRemote(
                "token",
                1,
                2,
                "",
                "",
                "Falla",
                "degradado"
            )
        }.exceptionOrNull()
        val error4 = runCatching {
            subject.createRemote(
                "token",
                1,
                2,
                "",
                "POS",
                "Falla",
                "cerrado"
            )
        }.exceptionOrNull()
        assertTrue(listOf(error1, error2, error3, error4).all { it is IllegalArgumentException })
        coVerify(exactly = 0) { remote.create(any(), any()) }
    }

    @Test
    fun createRemoteRejectsTooLongDescription() = runTest {
        assertTrue(runCatching {
            subject.createRemote(
                "t",
                1,
                2,
                "",
                "POS",
                "x".repeat(241),
                "degradado"
            )
        }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun createRemoteCreatesTicketAndTrimsText() = runTest {
        coEvery { remote.create("token", any()) } returns dto
        val result =
            subject.createRemote("token", 4, 3, "  Ana  ", "  POS ", " Falla ", "degradado")
        assertEquals(5L, result.remoteId)
        coVerify {
            remote.create(
                "token",
                match { it.ticketCode?.startsWith("SABA-") == true && it.reporterName == "Ana" && it.failureType == "POS" && it.description == "Falla" })
        }
    }

    @Test
    fun resolveRemoteRejectsResolvedOrIncomplete() = runTest {
        val withId = incident.copy(remoteId = 5)
        assertTrue(runCatching {
            subject.resolveRemote(
                "t",
                withId.copy(isResolved = true),
                "Ana",
                "Listo"
            )
        }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching {
            subject.resolveRemote(
                "t",
                withId,
                "",
                "Listo"
            )
        }.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { remote.update(any(), any(), any()) }
    }

    @Test
    fun resolveRemoteRejectsLocalIncident() = runTest {
        assertTrue(runCatching {
            subject.resolveRemote(
                "t",
                incident,
                "Ana",
                "Listo"
            )
        }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun resolveRemoteSendsUpdateAndPreservesOriginalProblem() = runTest {
        coEvery { remote.update("t", 5, any()) } returns dto.copy(isResolved = true)
        val result = subject.resolveRemote("t", incident.copy(remoteId = 5), "  Ana ", " Solución ")
        assertTrue(result.isResolved)
        coVerify {
            remote.update(
                "t",
                5,
                match { it.isResolved == true && it.failureType == "POS" && it.resolvedByName == "Ana" && it.resolutionDescription == "Solución" })
        }
    }
}
