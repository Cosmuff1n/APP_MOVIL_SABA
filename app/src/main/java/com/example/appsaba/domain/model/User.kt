package com.example.appsaba.domain.model

/** Modelo puro de dominio: no depende de Room ni de Android. */
data class User(
    val id: Long = 0L,
    val name: String,
    val rut: String? = null,
    val role: String = "operador",
    val isActive: Boolean = true,
    val assignedParkingId: Long? = null,
    val cognitoSub: String? = null
)
