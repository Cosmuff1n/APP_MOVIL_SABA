package com.example.appsaba.data.mapper

import com.example.appsaba.domain.model.Incident
import com.example.appsaba.data.local.entity.IncidentEntity

/** Conversión explícita: Domain no conoce las anotaciones de Room. */
fun IncidentEntity.toDomain(): Incident = Incident(id = id, ticketCode = ticketCode, parkingId = parkingId, serviceId = serviceId, reporterUserId = reporterUserId, reporterName = reporterName, failureType = failureType, description = description, status = status, reportedAt = reportedAt, evidenceUri = evidenceUri, evidenceFileName = evidenceFileName, evidenceMimeType = evidenceMimeType, isResolved = isResolved, resolvedByUserId = resolvedByUserId, resolvedByName = resolvedByName, resolutionDescription = resolutionDescription, resolvedAt = resolvedAt)
fun Incident.toEntity(): IncidentEntity = IncidentEntity(id = id, ticketCode = ticketCode, parkingId = parkingId, serviceId = serviceId, reporterUserId = reporterUserId, reporterName = reporterName, failureType = failureType, description = description, status = status, reportedAt = reportedAt, evidenceUri = evidenceUri, evidenceFileName = evidenceFileName, evidenceMimeType = evidenceMimeType, isResolved = isResolved, resolvedByUserId = resolvedByUserId, resolvedByName = resolvedByName, resolutionDescription = resolutionDescription, resolvedAt = resolvedAt)


/** DTO HTTP -> mismo modelo de dominio; NO se convierte automáticamente en Entity de Room.
 *  Las referencias parkingId/serviceId aquí siguen siendo IDs del servidor. */
fun com.example.appsaba.data.remote.dto.BffIncidentDto.toDomain(): Incident = Incident(
    id = 0L,
    remoteId = requireNotNull(id) { "Incidencia del BFF sin id" },
    ticketCode = requireNotNull(ticketCode) { "Incidencia del BFF sin ticketCode" },
    parkingId = requireNotNull(parkingId) { "Incidencia del BFF sin parkingId" },
    serviceId = requireNotNull(serviceId) { "Incidencia del BFF sin serviceId" },
    reporterName = reporterName.orEmpty(),
    failureType = requireNotNull(failureType) { "Incidencia del BFF sin failureType" },
    description = requireNotNull(description) { "Incidencia del BFF sin description" },
    status = requireNotNull(status) { "Incidencia del BFF sin status" },
    reportedAt = reportedAt ?: 0L,
    isResolved = isResolved == true,
    resolvedByName = resolvedByName,
    resolutionDescription = resolutionDescription,
    resolvedAt = resolvedAt
)
