package com.example.appsaba.data.local.datasource

import com.example.appsaba.data.local.dao.IncidentDao
import com.example.appsaba.data.local.entity.IncidentEntity
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

class IncidentLocalDataSourceImplTest {
    private val dao = mockk<IncidentDao>(relaxed = true)
    private val source = IncidentLocalDataSourceImpl(dao)
    private val incident = IncidentEntity(
        id = 8L, ticketCode = "SABA-8", parkingId = 3L,
        serviceId = 4L, failureType = "POS", description = "Sin conexion"
    )

    @Test
    fun observeByParkingDelegatesFilter() = runTest {
        every { dao.observeByParking(3L) } returns flowOf(listOf(incident))
        assertEquals(listOf(incident), source.observeByParking(3L).first())
        verify(exactly = 1) { dao.observeByParking(3L) }
    }

    @Test
    fun observeActiveByParkingDelegatesFilter() = runTest {
        every { dao.observeActiveByParking(3L) } returns flowOf(listOf(incident))
        assertEquals(incident, source.observeActiveByParking(3L).first().single())
        verify(exactly = 1) { dao.observeActiveByParking(3L) }
    }

    @Test
    fun observeByServiceDelegatesFilter() = runTest {
        every { dao.observeByService(4L) } returns flowOf(listOf(incident))
        assertEquals(incident, source.observeByService(4L).first().single())
        verify(exactly = 1) { dao.observeByService(4L) }
    }

    @Test
    fun observeActiveCountReturnsDaoCount() = runTest {
        every { dao.observeActiveCount(3L) } returns flowOf(2)
        assertEquals(2, source.observeActiveCount(3L).first())
        verify(exactly = 1) { dao.observeActiveCount(3L) }
    }

    @Test
    fun getByIdCanReturnEntityOrNull() = runTest {
        coEvery { dao.getById(8L) } returns incident
        coEvery { dao.getById(99L) } returns null
        assertEquals(incident, source.getById(8L))
        assertNull(source.getById(99L))
        coVerify(exactly = 1) { dao.getById(8L) }
    }

    @Test
    fun insertReturnsGeneratedId() = runTest {
        coEvery { dao.insert(incident) } returns 40L
        assertEquals(40L, source.insert(incident))
        coVerify(exactly = 1) { dao.insert(incident) }
    }

    @Test
    fun updateReturnsAffectedRows() = runTest {
        coEvery { dao.update(incident) } returns 1
        assertEquals(1, source.update(incident))
        coVerify(exactly = 1) { dao.update(incident) }
    }

    @Test
    fun deleteReturnsAffectedRows() = runTest {
        coEvery { dao.delete(incident) } returns 1
        assertEquals(1, source.delete(incident))
        coVerify(exactly = 1) { dao.delete(incident) }
    }
}
