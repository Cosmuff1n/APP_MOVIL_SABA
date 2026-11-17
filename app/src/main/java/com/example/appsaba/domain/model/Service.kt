package com.example.appsaba.domain.model

/** Modelo puro de dominio: no depende de Room ni de Android. */
data class Service(
    val id: Long = 0L,
    val parkingId: Long,
    val name: String,
    val type: String,
    val detail: String = "",
    val status: String = "operativo",
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val problem: String? = null
)
