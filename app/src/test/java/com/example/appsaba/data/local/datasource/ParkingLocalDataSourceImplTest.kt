package com.example.appsaba.data.local.datasource

import com.example.appsaba.data.local.dao.ParkingDao
import com.example.appsaba.data.local.entity.ParkingEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParkingLocalDataSourceImplTest {
    private val dao = mockk<ParkingDao>(relaxed = true)
    private val source = ParkingLocalDataSourceImpl(dao)
    private val parking = ParkingEntity(id = 7L, name = "Centro", address = "Av. Central")

    @Test
    fun observeAllReturnsDaoFlow() = runTest {
        every { dao.observeAll() } returns flowOf(listOf(parking))
        assertEquals(listOf(parking), source.observeAll().first())
        verify(exactly = 1) { dao.observeAll() }
    }

    @Test
    fun observeActiveUsesFilteredDaoQuery() = runTest {
        every { dao.observeActive() } returns flowOf(listOf(parking))
        assertEquals(listOf(parking), source.observeActive().first())
        verify(exactly = 1) { dao.observeActive() }
    }

    @Test
    fun observeRemoteUsesRemoteDaoQuery() = runTest {
        val remote = parking.copy(remoteId = 42L, revision = 2L)
        every { dao.observeRemote() } returns flowOf(listOf(remote))
        assertEquals(remote, source.observeRemote().first().single())
        verify(exactly = 1) { dao.observeRemote() }
    }

    @Test
    fun getByIdForwardsIdAndReturnsEntity() = runTest {
        coEvery { dao.getById(7L) } returns parking
        assertEquals(parking, source.getById(7L))
        coVerify(exactly = 1) { dao.getById(7L) }
    }

    @Test
    fun getByIdCanReturnNull() = runTest {
        coEvery { dao.getById(999L) } returns null
        assertNull(source.getById(999L))
    }

    @Test
    fun insertDelegatesAndReturnsGeneratedId() = runTest {
        coEvery { dao.insert(parking) } returns 81L
        assertEquals(81L, source.insert(parking))
        coVerify(exactly = 1) { dao.insert(parking) }
    }

    @Test
    fun updateDelegatesAndReturnsAffectedRows() = runTest {
        coEvery { dao.update(parking) } returns 1
        assertEquals(1, source.update(parking))
        coVerify(exactly = 1) { dao.update(parking) }
    }

    @Test
    fun deleteDelegatesAndReturnsAffectedRows() = runTest {
        coEvery { dao.delete(parking) } returns 1
        assertEquals(1, source.delete(parking))
        coVerify(exactly = 1) { dao.delete(parking) }
    }

    @Test
    fun upsertRemoteUsesExistingDao() = runTest {
        val remote = parking.copy(remoteId = 44L)
        source.upsertRemote(remote)
        coVerify(exactly = 1) { dao.upsertRemote(remote) }
    }

    @Test
    fun deletingRemoteCacheUsesRemoteIdNotLocalId() = runTest {
        source.deleteCachedRemote(44L)
        coVerify(exactly = 1) { dao.deleteCachedRemote(44L) }
    }
}
