package com.example.appsaba.data.remote

data class LoginRequest(
    val rut: String,
    val password: String
)

data class LoginResponse(
    val access_token: String,
    val token_type: String = "Bearer",
    val expires_in: Long = 3600,
    val user: UserDto? = null
)

data class UserDto(
    val id: Int? = null,
    val rut: String? = null,
    val email: String? = null
)