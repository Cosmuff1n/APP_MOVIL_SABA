package com.example.appsaba.data.local.datasource

import com.example.appsaba.data.local.dao.ServiceDao
import com.example.appsaba.data.local.entity.ServiceEntity
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

class ServiceLocalDataSourceImplTest {
    private val dao = mockk<ServiceDao>(relaxed = true)
    private val source = ServiceLocalDataSourceImpl(dao)
    private val service =
        ServiceEntity(id = 12L, parkingId = 3L, name = "Barrera", type = "barrier")

    @Test
    fun observeByParkingUsesDaoQuery() = runTest {
        every { dao.observeByParking(3L) } returns flowOf(listOf(service))
        assertEquals(listOf(service), source.observeByParking(3L).first())
        verify(exactly = 1) { dao.observeByParking(3L) }
    }

    @Test
    fun observeByIdCanProduceNull() = runTest {
        every { dao.observeById(99L) } returns flowOf(null)
        assertNull(source.observeById(99L).first())
        verify(exactly = 1) { dao.observeById(99L) }
    }

    @Test
    fun observeByIdCanProduceEntity() = runTest {
        every { dao.observeById(12L) } returns flowOf(service)
        assertEquals(service, source.observeById(12L).first())
    }

    @Test
    fun getByIdReturnsDaoEntity() = runTest {
        coEvery { dao.getById(12L) } returns service
        assertEquals(service, source.getById(12L))
        coVerify(exactly = 1) { dao.getById(12L) }
    }

    @Test
    fun insertPassesEntityToDao() = runTest {
        coEvery { dao.insert(service) } returns 31L
        assertEquals(31L, source.insert(service))
        coVerify(exactly = 1) { dao.insert(service) }
    }

    @Test
    fun updatePassesEntityToDao() = runTest {
        coEvery { dao.update(service) } returns 1
        assertEquals(1, source.update(service))
        coVerify(exactly = 1) { dao.update(service) }
    }

    @Test
    fun deletePassesEntityToDao() = runTest {
        coEvery { dao.delete(service) } returns 1
        assertEquals(1, source.delete(service))
        coVerify(exactly = 1) { dao.delete(service) }
    }

    @Test
    fun updateStatusForwardsAllValues() = runTest {
        coEvery { dao.updateStatus(12L, "degradado", "motor", 1000L) } returns 1
        assertEquals(1, source.updateStatus(12L, "degradado", "motor", 1000L))
        coVerify(exactly = 1) { dao.updateStatus(12L, "degradado", "motor", 1000L) }
    }
}
