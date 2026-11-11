package com.example.appsaba.ui.users
import com.example.appsaba.domain.model.User

data class UsersUiState(val users: List<User> = emptyList(), val busy: Boolean = false, val feedback: String? = null)
