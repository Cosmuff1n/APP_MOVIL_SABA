package com.example.appsaba.data.repository

import com.example.appsaba.data.local.datasource.UserLocalDataSource
import com.example.appsaba.data.mapper.toEntity
import com.example.appsaba.domain.model.User
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {
    private lateinit var local: UserLocalDataSource
    private lateinit var subject: UserRepositoryImpl
    private val user = User(id = 4, name = "Operador", rut = "123", assignedParkingId = 2)
    @Before
    fun setup() {
        local = mockk(relaxed = true); subject = UserRepositoryImpl(local)
    }

    @Test
    fun observeAllConvertsRows() = runTest {
        every { local.observeAll() } returns flowOf(listOf(user.toEntity()))
        assertEquals(user, subject.observeAll().first().single())
    }

    @Test
    fun observeByParkingFiltersAtDaoBoundary() = runTest {
        every { local.observeByParking(2) } returns flowOf(listOf(user.toEntity()))
        assertEquals(4L, subject.observeByParking(2).first().single().id)
        verify { local.observeByParking(2) }
    }

    @Test
    fun getByIdAndRutReturnNullOnMissing() = runTest {
        coEvery { local.getById(999) } returns null
        coEvery { local.getByRut("no") } returns null
        assertNull(subject.getById(999))
        assertNull(subject.getByRut("no"))
    }

    @Test
    fun getByRutMapsExistingUser() = runTest {
        coEvery { local.getByRut("123") } returns user.toEntity()
        assertEquals(user, subject.getByRut("123"))
    }

    @Test
    fun crudDelegatesWithDataUnchanged() = runTest {
        coEvery { local.insert(any()) } returns 44
        coEvery { local.update(any()) } returns 1
        coEvery { local.delete(any()) } returns 1
        assertEquals(44L, subject.insert(user))
        assertEquals(1, subject.update(user))
        assertEquals(1, subject.delete(user))
        coVerify { local.insert(match { it.cognitoSub == null && it.assignedParkingId == 2L }) }
    }
}
