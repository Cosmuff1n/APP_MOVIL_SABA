package com.example.appsaba.data.local.datasource

import com.example.appsaba.data.local.dao.UserDao
import com.example.appsaba.data.local.entity.UserEntity
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

class UserLocalDataSourceImplTest {
    private val dao = mockk<UserDao>(relaxed = true)
    private val source = UserLocalDataSourceImpl(dao)
    private val user = UserEntity(id = 9L, name = "Ana", rut = "11111111-1")

    @Test
    fun observeAllUsesDao() = runTest {
        every { dao.observeAll() } returns flowOf(listOf(user))
        assertEquals(listOf(user), source.observeAll().first())
        verify(exactly = 1) { dao.observeAll() }
    }

    @Test
    fun observeByParkingUsesId() = runTest {
        every { dao.observeByParking(3L) } returns flowOf(listOf(user))
        assertEquals(user, source.observeByParking(3L).first().single())
        verify(exactly = 1) { dao.observeByParking(3L) }
    }

    @Test
    fun getByIdCanFindUser() = runTest {
        coEvery { dao.getById(9L) } returns user
        assertEquals(user, source.getById(9L))
        coVerify(exactly = 1) { dao.getById(9L) }
    }

    @Test
    fun getByRutCanReturnNull() = runTest {
        coEvery { dao.getByRut("inexistente") } returns null
        assertNull(source.getByRut("inexistente"))
        coVerify(exactly = 1) { dao.getByRut("inexistente") }
    }

    @Test
    fun getByRutFindsExpectedUser() = runTest {
        coEvery { dao.getByRut("11111111-1") } returns user
        assertEquals(user, source.getByRut("11111111-1"))
    }

    @Test
    fun insertUsesDaoAndReturnsId() = runTest {
        coEvery { dao.insert(user) } returns 10L
        assertEquals(10L, source.insert(user))
        coVerify(exactly = 1) { dao.insert(user) }
    }

    @Test
    fun updateUsesDaoAndReturnsCount() = runTest {
        coEvery { dao.update(user) } returns 1
        assertEquals(1, source.update(user))
        coVerify(exactly = 1) { dao.update(user) }
    }

    @Test
    fun deleteUsesDaoAndReturnsCount() = runTest {
        coEvery { dao.delete(user) } returns 1
        assertEquals(1, source.delete(user))
        coVerify(exactly = 1) { dao.delete(user) }
    }
}
