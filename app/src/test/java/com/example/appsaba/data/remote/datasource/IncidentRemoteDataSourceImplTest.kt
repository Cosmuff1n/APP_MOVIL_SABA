package com.example.appsaba.data.remote.datasource

import com.example.appsaba.data.remote.apiservice.BffIncidentApiService
import com.example.appsaba.data.remote.dto.BffIncidentDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class IncidentRemoteDataSourceImplTest {
    private lateinit var api: BffIncidentApiService
    private lateinit var subject: IncidentRemoteDataSourceImpl
    private val dto = BffIncidentDto(id = 1, ticketCode = "INC-1", parkingId = 2, serviceId = 3)

    @Before
    fun setUp() {
        api = mockk(); subject = IncidentRemoteDataSourceImpl(api)
    }

    @Test
    fun listingUsesBearerHeader() = runTest {
        coEvery { api.list("Bearer token", null) } returns listOf(dto)
        assertEquals(dto, subject.list("token").single())
        coVerify { api.list("Bearer token", null) }
    }

    @Test
    fun createUsesBearerAndSameDto() = runTest {
        coEvery { api.create(any(), any()) } returns dto
        assertEquals(dto, subject.create("abc", dto))
        coVerify(exactly = 1) { api.create("Bearer abc", dto) }
    }

    @Test
    fun updateUsesBearerIdAndDto() = runTest {
        coEvery { api.update(any(), any(), any()) } returns dto
        assertEquals(dto, subject.update("abc", 1, dto))
        coVerify(exactly = 1) { api.update("Bearer abc", 1L, dto) }
    }

    @Test
    fun remoteFailureIsNotSwallowed() = runTest {
        coEvery { api.list(any(), null) } throws IllegalStateException("offline")
        assertTrue(runCatching { subject.list("token") }.exceptionOrNull() is IllegalStateException)
    }
}
