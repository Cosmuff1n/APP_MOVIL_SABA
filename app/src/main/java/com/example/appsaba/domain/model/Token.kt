package com.example.appsaba.domain.model

data class Token(
    val idToken: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val expiresIn: Long? = null,
)

