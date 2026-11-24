package com.example.appsaba.data.remote.dto

/** Contrato HTTP de Incidencias. Campos nullable para GET, POST y PUT con un solo DTO. */
data class BffIncidentDto(
    val id: Long? = null,
    val ticketCode: String? = null,
    val parkingId: Long? = null,
    val serviceId: Long? = null,
    val reporterName: String? = null,
    val failureType: String? = null,
    val description: String? = null,
    val status: String? = null,
    val reportedAt: Long? = null,
    val isResolved: Boolean? = null,
    val resolvedByName: String? = null,
    val resolutionDescription: String? = null,
    val resolvedAt: Long? = null
)
