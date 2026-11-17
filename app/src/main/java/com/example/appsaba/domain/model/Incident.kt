package com.example.appsaba.domain.model

/** Modelo puro de dominio: no depende de Room ni de Android. */
data class Incident(
    val id: Long = 0L,
    val ticketCode: String,
    val parkingId: Long,
    val serviceId: Long,
    val reporterUserId: Long? = null,
    val reporterName: String = "",
    val failureType: String,
    val description: String,
    val status: String = "degradado",
    val reportedAt: Long = System.currentTimeMillis(),
    val evidenceUri: String? = null,
    val evidenceFileName: String? = null,
    val evidenceMimeType: String? = null,
    val isResolved: Boolean = false,
    val resolvedByUserId: Long? = null,
    val resolvedByName: String? = null,
    val resolutionDescription: String? = null,
    val resolvedAt: Long? = null
)
