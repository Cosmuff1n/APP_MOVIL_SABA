package com.example.appsaba.ui.users

import androidx.lifecycle.viewModelScope
import com.example.appsaba.domain.model.User
import com.example.appsaba.domain.repository.UserRepository
import com.example.appsaba.ui.common.ActionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository
) : ActionViewModel() {
    val uiState: StateFlow<UsersUiState> = combine(repository.observeAll(), status) { list, action ->
        UsersUiState(list, action.busy, action.feedback)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UsersUiState())

    fun saveUser(id: Long?, name: String, rut: String, role: String, parkingId: Long?) {
        if (name.isBlank() || role.isBlank()) {
            reject("Nombre y rol son obligatorios.")
            return
        }
        execute("Perfil local guardado en SQLite") {
            if (id == null) repository.insert(User(name = name.trim(), rut = rut.trim().ifBlank { null },
                role = role.trim(), assignedParkingId = parkingId))
            else {
                val previous = requireNotNull(repository.getById(id)) { "Perfil no encontrado" }
                repository.update(previous.copy(name = name.trim(), rut = rut.trim().ifBlank { null },
                    role = role.trim(), assignedParkingId = parkingId))
            }
        }
    }
    fun toggleUser(user: User) = execute("Perfil local actualizado") {
        repository.update(user.copy(isActive = !user.isActive))
    }
    fun deleteUser(user: User) = execute("Perfil local eliminado") { repository.delete(user) }
}
