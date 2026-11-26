package com.example.appsaba.data.remote.datasource

import com.example.appsaba.data.remote.apiservice.Ec2ParkingApiService
import com.example.appsaba.data.remote.dto.Ec2ParkingDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.HttpException
import okhttp3.ResponseBody.Companion.toResponseBody

class ParkingRemoteDataSourceImplTest {
    private lateinit var api: Ec2ParkingApiService
    private lateinit var subject: ParkingRemoteDataSourceImpl

    @Before
    fun setUp() {
        api = mockk(); subject = ParkingRemoteDataSourceImpl(api)
    }

    @Test
    fun listIncludesBearerAuthorization() = runTest {
        coEvery { api.getParkings("Bearer token") } returns listOf(
            Ec2ParkingDto(
                id = 1,
                name = "Centro"
            )
        )
        assertEquals(1, subject.list("token").size)
        coVerify(exactly = 1) { api.getParkings("Bearer token") }
    }

    @Test
    fun createTrimsNameBeforeCallingApi() = runTest {
        coEvery { api.createParking(any(), any()) } returns Ec2ParkingDto(id = 10)
        subject.create("abc", "  Oriente  ")
        coVerify(exactly = 1) { api.createParking("Bearer abc", match { it.name == "Oriente" }) }
    }

    @Test
    fun renameSendsRevisionAndTrimmedName() = runTest {
        coEvery { api.patchParking(any(), any(), any()) } returns Ec2ParkingDto(id = 7)
        subject.rename("abc", 7, 4, "  Norte ")
        coVerify(exactly = 1) {
            api.patchParking(
                "Bearer abc",
                7,
                match { it.revision == 4L && it.name == "Norte" && it.isActive == null })
        }
    }

    @Test
    fun toggleSendsBooleanAndRevision() = runTest {
        coEvery { api.patchParking(any(), any(), any()) } returns Ec2ParkingDto(id = 7)
        subject.toggle("abc", 7, 4, false)
        coVerify(exactly = 1) {
            api.patchParking(
                "Bearer abc",
                7,
                match { it.revision == 4L && it.isActive == false && it.name == null })
        }
    }

    @Test
    fun deleteUsesIfMatchRevision() = runTest {
        coEvery { api.deleteParking(any(), any(), any()) } returns Response.success(Unit)
        subject.delete("abc", 7, 14)
        coVerify(exactly = 1) { api.deleteParking("Bearer abc", "14", 7) }
    }

    @Test
    fun deletePropagatesHttpError() = runTest {
        coEvery { api.deleteParking(any(), any(), any()) } returns Response.error(
            409,
            "conflict".toResponseBody(null)
        )
        assertTrue(runCatching { subject.delete("abc", 7, 14) }.exceptionOrNull() is HttpException)
    }
}
