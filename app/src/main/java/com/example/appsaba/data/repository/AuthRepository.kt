package com.example.appsaba.data.repository

import com.example.appsaba.data.remote.AuthApiService
import com.example.appsaba.data.remote.LoginRequest
import com.example.appsaba.data.remote.LoginResponse

// runCatching = ejecuta el bloque y atrapa cualquier excepción devolviendo Result:
// - si api.login() ok -> Result.success(LoginResponse)
// - si falla (sin red, 401, timeout, JSON roto) -> Result.failure(excepción), sin try/catch explícito.
// El ViewModel luego hace result.onSuccess { } / .onFailure { } para pasar a isSuccess o loginError.
class AuthRepository(private val api: AuthApiService) {
    suspend fun login(rut: String, password: String): Result<LoginResponse> =
        runCatching {
            api.login(LoginRequest(rut.trim(), password))
        }
}