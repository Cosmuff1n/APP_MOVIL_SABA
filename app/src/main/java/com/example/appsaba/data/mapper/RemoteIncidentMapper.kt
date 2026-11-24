package com.example.appsaba.data.mapper

import com.example.appsaba.data.remote.dto.BffIncidentDto
import com.example.appsaba.domain.model.RemoteIncident

/** Convierte el DTO del BFF en un modelo remoto sin tocar la Entity Room. */
fun BffIncidentDto.toRemoteDomain(): RemoteIncident = RemoteIncident(
    id = requireNotNull(id) { "Incidencia remota sin id" },
    ticketCode = requireNotNull(ticketCode) { "Incidencia remota sin ticketCode" },
    parkingId = requireNotNull(parkingId) { "Incidencia remota sin parkingId" },
    serviceId = requireNotNull(serviceId) { "Incidencia remota sin serviceId" },
    reporterName = reporterName.orEmpty(),
    failureType = failureType.orEmpty(),
    description = description.orEmpty(),
    status = status.orEmpty(),
    reportedAt = reportedAt ?: 0L,
    isResolved = isResolved == true,
    resolvedByName = resolvedByName,
    resolutionDescription = resolutionDescription,
    resolvedAt = resolvedAt
)
