package com.example.appsaba.domain.repository

import com.example.appsaba.domain.model.LoginSession

interface LoginRepository {
    suspend fun login(rut: String, password: String): Result<LoginSession>
}
