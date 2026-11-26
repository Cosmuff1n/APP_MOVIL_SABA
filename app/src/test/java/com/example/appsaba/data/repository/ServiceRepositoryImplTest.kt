package com.example.appsaba.data.repository

import com.example.appsaba.data.local.datasource.ServiceLocalDataSource
import com.example.appsaba.data.local.entity.ServiceEntity
import com.example.appsaba.domain.model.Service
import com.example.appsaba.data.mapper.toEntity
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ServiceRepositoryImplTest {
    private lateinit var local: ServiceLocalDataSource
    private lateinit var subject: ServiceRepositoryImpl
    private val service =
        Service(id = 4, parkingId = 2, name = "LPR", type = "lpr", lastUpdatedAt = 100)

    @Before
    fun setup() {
        local = mockk(relaxed = true); subject = ServiceRepositoryImpl(local)
    }

    @Test
    fun observeByParkingMapsRowsToDomain() = runTest {
        every { local.observeByParking(2) } returns flowOf(listOf(service.toEntity()))
        assertEquals(service, subject.observeByParking(2).first().single())
    }

    @Test
    fun observeByIdSupportsMissingService() = runTest {
        every { local.observeById(88) } returns flowOf(null)
        assertNull(subject.observeById(88).first())
    }

    @Test
    fun getByIdMapsExistingService() = runTest {
        coEvery { local.getById(4) } returns service.toEntity()
        assertEquals(service, subject.getById(4))
    }

    @Test
    fun crudCallsLocalStorageOnly() = runTest {
        coEvery { local.insert(any()) } returns 10L
        coEvery { local.update(any()) } returns 1
        coEvery { local.delete(any()) } returns 1
        assertEquals(10L, subject.insert(service))
        assertEquals(1, subject.update(service))
        assertEquals(1, subject.delete(service))
        coVerify(exactly = 1) { local.insert(match { it.name == "LPR" }) }
    }

    @Test
    fun updateStatusForwardsStatusAndProblem() = runTest {
        coEvery { local.updateStatus(any(), any(), any(), any()) } returns 1
        assertEquals(1, subject.updateStatus(4, "degradado", "Cámara"))
        coVerify { local.updateStatus(4, "degradado", "Cámara", match { it > 0 }) }
    }
}
