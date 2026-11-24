package com.example.appsaba.domain.model

/** Incidencia almacenada en PostgreSQL; su id NO es una clave primaria de Room. */
data class RemoteIncident(
    val id: Long,
    val ticketCode: String,
    val parkingId: Long,
    val serviceId: Long,
    val reporterName: String,
    val failureType: String,
    val description: String,
    val status: String,
    val reportedAt: Long,
    val isResolved: Boolean,
    val resolvedByName: String?,
    val resolutionDescription: String?,
    val resolvedAt: Long?
)
