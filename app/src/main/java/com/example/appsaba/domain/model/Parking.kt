package com.example.appsaba.domain.model

/** Modelo puro de dominio: no depende de Room ni de Android. */
data class Parking(
    val id: Long = 0L,
    val name: String,
    val address: String,
    val commune: String = "",
    val totalSpaces: Int = 0,
    val freeSpaces: Int = 0,
    val isActive: Boolean = true
)
