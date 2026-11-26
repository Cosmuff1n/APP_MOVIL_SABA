package com.example.appsaba.data.repository

import com.example.appsaba.data.remote.apiservice.AuthApiService
import com.example.appsaba.data.remote.dto.LoginResponse
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginRepositoryImplTest {
    private lateinit var api: AuthApiService
    private lateinit var subject: LoginRepositoryImpl

    @Before
    fun setup() {
        api = mockk(); subject = LoginRepositoryImpl(api)
    }

    @Test
    fun trimsRutAndReturnsDomainSession() = runTest {
        coEvery { api.login(any()) } returns LoginResponse("access-token")
        val result = subject.login("  12-3  ", "pass123")
        assertTrue(result.isSuccess)
        assertEquals("access-token", result.getOrThrow().token)
        coVerify { api.login(match { it.rut == "12-3" && it.password == "pass123" }) }
    }

    @Test
    fun apiFailureBecomesResultFailure() = runTest {
        coEvery { api.login(any()) } throws IllegalStateException("offline")
        val result = subject.login("12-3", "pass123")
        assertTrue(result.isFailure)
        assertEquals("offline", result.exceptionOrNull()?.message)
    }

    @Test
    fun repositoryDoesNotModifyPassword() = runTest {
        coEvery { api.login(any()) } returns LoginResponse("token")
        subject.login("123", "  secret  ")
        coVerify { api.login(match { it.password == "  secret  " }) }
    }
}
