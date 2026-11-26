package com.example.appsaba.ui.users

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.User
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UsersScreenComposeTest {
    @get:Rule
    val compose = createComposeRule()
    private val vm = mockk<UsersViewModel>(relaxed = true)
    private val parking = Parking(id = 5, name = "Plaza", address = "Uno")
    private val user = User(id = 11, name = "Operador", role = "operador", assignedParkingId = 5)

    @Test
    fun sinUsuariosMuestraEstadoVacio() {
        compose.setContent { UsersScreen(emptyList(), emptyList(), null, false, vm) }
        compose.onNodeWithText("No hay perfiles locales registrados.").assertExists()
    }

    @Test
    fun crearPerfilInvocaViewModel() {
        compose.setContent { UsersScreen(emptyList(), emptyList(), null, false, vm) }
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Nuevo usuario")
        compose.onNodeWithText("Agregar perfil").performClick()
        verify(exactly = 1) { vm.saveUser(null, "Nuevo usuario", "", "operador", null) }
    }

    @Test
    fun asignarEstacionamientoSeleccionado() {
        compose.setContent { UsersScreen(emptyList(), listOf(parking), 5L, false, vm) }
        compose.onNodeWithText("Asignar seleccionado").performClick()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Usuario 2")
        compose.onNodeWithText("Agregar perfil").performClick()
        verify(exactly = 1) { vm.saveUser(null, "Usuario 2", "", "operador", 5L) }
    }

    @Test
    fun busyBloqueaAgregarPerfil() {
        compose.setContent { UsersScreen(emptyList(), emptyList(), null, true, vm) }
        compose.onNodeWithText("Agregar perfil").assertIsNotEnabled()
    }

    @Test
    fun editarUsuarioMantieneSuId() {
        compose.setContent { UsersScreen(listOf(user), listOf(parking), 5L, false, vm) }
        compose.onNodeWithText("Editar").performClick()
        compose.onNodeWithText("Guardar perfil").performClick()
        verify(exactly = 1) { vm.saveUser(11L, "Operador", "", "operador", 5L) }
    }

    @Test
    fun desactivarUsuarioEnviaModeloOriginal() {
        compose.setContent { UsersScreen(listOf(user), listOf(parking), 5L, false, vm) }
        compose.onNodeWithText("Desactivar").performClick()
        verify(exactly = 1) { vm.toggleUser(user) }
    }

    @Test
    fun borrarUsuarioRequiereConfirmacion() {
        compose.setContent { UsersScreen(listOf(user), listOf(parking), 5L, false, vm) }
        compose.onNodeWithText("Eliminar").performClick()
        compose.onNodeWithText("Confirmar eliminación").assertExists()
        compose.onNodeWithText("Cancelar").performClick()
        verify(exactly = 0) { vm.deleteUser(any()) }
    }
}
