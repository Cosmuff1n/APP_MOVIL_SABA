package com.example.appsaba.data.repository

import com.example.appsaba.data.remote.AuthApiService
import com.example.appsaba.data.remote.LoginRequest
import com.example.appsaba.domain.model.LoginSession
import com.example.appsaba.domain.repository.LoginRepository
import javax.inject.Inject

/** DTO HTTP se transforma en modelo puro de dominio antes de llegar al ViewModel. */
class LoginRepositoryImpl @Inject constructor(private val api: AuthApiService) : LoginRepository {
    override suspend fun login(rut: String, password: String): Result<LoginSession> =
        runCatching { LoginSession(api.login(LoginRequest(rut.trim(), password)).access_token) }
}
