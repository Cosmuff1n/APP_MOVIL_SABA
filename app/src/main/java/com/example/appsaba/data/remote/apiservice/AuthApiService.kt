package com.example.appsaba.data.remote.apiservice

import com.example.appsaba.data.remote.dto.LoginRequest
import com.example.appsaba.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse
}