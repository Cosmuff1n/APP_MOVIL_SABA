package com.example.appsaba.data.repository

import com.example.appsaba.data.local.datasource.ParkingLocalDataSource
import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.remote.datasource.ParkingRemoteDataSource
import com.example.appsaba.data.remote.dto.Ec2ParkingDto
import com.example.appsaba.domain.model.Parking
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ParkingRepositoryImplTest {
    private lateinit var local: ParkingLocalDataSource
    private lateinit var remote: ParkingRemoteDataSource
    private lateinit var subject: ParkingRepositoryImpl
    private val parking =
        Parking(id = -8, name = "Centro", address = "A", remoteId = 7, revision = 3)

    @Before
    fun setup() {
        local = mockk(relaxed = true); remote = mockk(relaxed = true)
        subject = ParkingRepositoryImpl(local, remote)
    }

    @Test
    fun observeAllConvertsRoomToDomain() = runTest {
        every { local.observeAll() } returns flowOf(
            listOf(
                ParkingEntity(
                    id = 2,
                    name = "Local",
                    address = "A"
                )
            )
        )
        assertEquals("Local", subject.observeAll().first().single().name)
    }

    @Test
    fun observeRemotePreservesRemoteId() = runTest {
        every { local.observeRemote() } returns flowOf(
            listOf(
                ParkingEntity(
                    id = -8,
                    name = "Centro",
                    address = "A",
                    remoteId = 7
                )
            )
        )
        assertEquals(7L, subject.observeRemote().first().single().remoteId)
    }

    @Test
    fun getByIdConvertsDomainAndMissingReturnsNull() = runTest {
        coEvery { local.getById(2) } returns ParkingEntity(id = 2, name = "Local", address = "A")
        coEvery { local.getById(3) } returns null
        assertEquals("Local", subject.getById(2)?.name)
        assertNull(subject.getById(3))
    }

    @Test
    fun localCrudDelegatesToLocalOnly() = runTest {
        coEvery { local.insert(any()) } returns 22L
        coEvery { local.update(any()) } returns 1
        coEvery { local.delete(any()) } returns 1
        val item = Parking(name = "Test", address = "A")
        assertEquals(22L, subject.insert(item))
        assertEquals(1, subject.update(item))
        assertEquals(1, subject.delete(item))
        coVerify(exactly = 1) { local.insert(match { it.name == "Test" }) }
        coVerify(exactly = 1) { local.delete(match { it.name == "Test" }) }
        coVerify(exactly = 0) { remote.list(any()) }
    }

    @Test
    fun refreshRemoteCachesEntriesWithoutDeletingOtherRows() = runTest {
        coEvery { remote.list("token") } returns listOf(
            Ec2ParkingDto(7, "A"),
            Ec2ParkingDto(8, "B")
        )
        subject.refreshRemote("token")
        coVerify(exactly = 1) { local.upsertRemote(match { it.remoteId == 7L && it.id == -8L }) }
        coVerify(exactly = 1) { local.upsertRemote(match { it.remoteId == 8L && it.id == -9L }) }
        coVerify(exactly = 0) { local.deleteCachedRemote(any()) }
    }

    @Test
    fun createRemoteRefreshesCacheAfterSuccess() = runTest {
        coEvery { remote.list(any()) } returns emptyList()
        subject.createRemote("token", "Nuevo")
        coVerifyOrder { remote.create("token", "Nuevo"); remote.list("token") }
    }

    @Test
    fun renameRemoteUsesRevisionThenRefreshes() = runTest {
        coEvery { remote.list(any()) } returns emptyList()
        subject.renameRemote("token", parking, "Norte")
        coVerify(exactly = 1) { remote.rename("token", 7, 3, "Norte") }
        coVerify(exactly = 1) { remote.list("token") }
    }

    @Test
    fun toggleRemoteInvertsStatusAndRefreshes() = runTest {
        coEvery { remote.list(any()) } returns emptyList()
        subject.toggleRemote("token", parking)
        coVerify(exactly = 1) { remote.toggle("token", 7, 3, false) }
    }

    @Test
    fun deleteRemoteInvalidatesOnlyOneCachedId() = runTest {
        coEvery { remote.list(any()) } returns emptyList()
        subject.deleteRemote("token", parking)
        coVerifyOrder {
            remote.delete(
                "token",
                7,
                3
            ); local.deleteCachedRemote(7); remote.list("token")
        }
    }

    @Test
    fun remoteChangeWithoutRemoteIdIsRejected() = runTest {
        val exception = runCatching {
            subject.renameRemote(
                "token",
                parking.copy(remoteId = null),
                "N"
            )
        }.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        coVerify(exactly = 0) { remote.rename(any(), any(), any(), any()) }
    }

    @Test
    fun remoteChangeWithoutRevisionIsRejected() = runTest {
        val exception = runCatching {
            subject.toggleRemote(
                "token",
                parking.copy(revision = null)
            )
        }.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        coVerify(exactly = 0) { remote.toggle(any(), any(), any(), any()) }
    }

    @Test
    fun remoteFailureDoesNotWriteToLocalCache() = runTest {
        coEvery { remote.list(any()) } throws IllegalStateException("BFF offline")
        assertTrue(runCatching { subject.refreshRemote("token") }.exceptionOrNull() is IllegalStateException)
        coVerify(exactly = 0) { local.upsertRemote(any()) }
    }
}
