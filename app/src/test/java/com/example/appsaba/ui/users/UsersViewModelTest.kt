@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.appsaba.ui.users

import com.example.appsaba.domain.model.User
import com.example.appsaba.domain.repository.UserRepository
import com.example.appsaba.testing.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UsersViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()
    private lateinit var repo: UserRepository
    private lateinit var vm: UsersViewModel
    private val example = User(id = 4, name = "Ana", rut = null, isActive = true)
    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        every { repo.observeAll() } returns MutableStateFlow(listOf(example))
        vm = UsersViewModel(repo)
    }

    private fun kotlinx.coroutines.test.TestScope.subscribe() =
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

    @Test
    fun observesLocalUsers() = runTest {
        subscribe(); runCurrent()
        assertEquals("Ana", vm.uiState.value.users.single().name)
    }

    @Test
    fun refusesEmptyNameOrRole() = runTest {
        subscribe(); vm.saveUser(null, "", "123", "admin", null); runCurrent()
        vm.saveUser(null, "Ana", "123", "", null); runCurrent()
        coVerify(exactly = 0) { repo.insert(any()) }
    }

    @Test
    fun createsUserWithTrimmedNameAndNullableRut() = runTest {
        subscribe(); vm.saveUser(null, "  Carlos ", "  ", " operador ", 3); runCurrent()
        coVerify { repo.insert(match { it.name == "Carlos" && it.rut == null && it.role == "operador" && it.assignedParkingId == 3L }) }
    }

    @Test
    fun updatesExistingUserPreservingId() = runTest {
        subscribe(); coEvery { repo.getById(4) } returns example
        vm.saveUser(4, "  Ana  ", "44", "admin", null); runCurrent()
        coVerify { repo.update(match { it.id == 4L && it.rut == "44" && it.role == "admin" }) }
    }

    @Test
    fun missingUserShowsError() = runTest {
        subscribe(); coEvery { repo.getById(99) } returns null
        vm.saveUser(99, "Ana", "", "admin", null); runCurrent()
        assertTrue(vm.uiState.value.feedback!!.contains("no encontrado"))
    }

    @Test
    fun toggleUserReversesActiveStatus() = runTest {
        subscribe(); vm.toggleUser(example); runCurrent()
        coVerify { repo.update(match { !it.isActive && it.id == 4L }) }
    }

    @Test
    fun deletesOnlyRequestedUser() = runTest {
        subscribe(); vm.deleteUser(example); runCurrent()
        coVerify { repo.delete(example) }
    }
}
