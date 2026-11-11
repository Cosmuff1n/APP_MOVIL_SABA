package com.example.appsaba.data.mapper

import com.example.appsaba.domain.model.Incident
import com.example.appsaba.data.local.entity.IncidentEntity

/** Conversión explícita: Domain no conoce las anotaciones de Room. */
fun IncidentEntity.toDomain(): Incident = Incident(id = id, ticketCode = ticketCode, parkingId = parkingId, serviceId = serviceId, reporterUserId = reporterUserId, reporterName = reporterName, failureType = failureType, description = description, status = status, reportedAt = reportedAt, evidenceUri = evidenceUri, evidenceFileName = evidenceFileName, evidenceMimeType = evidenceMimeType, isResolved = isResolved, resolvedByUserId = resolvedByUserId, resolvedByName = resolvedByName, resolutionDescription = resolutionDescription, resolvedAt = resolvedAt)
fun Incident.toEntity(): IncidentEntity = IncidentEntity(id = id, ticketCode = ticketCode, parkingId = parkingId, serviceId = serviceId, reporterUserId = reporterUserId, reporterName = reporterName, failureType = failureType, description = description, status = status, reportedAt = reportedAt, evidenceUri = evidenceUri, evidenceFileName = evidenceFileName, evidenceMimeType = evidenceMimeType, isResolved = isResolved, resolvedByUserId = resolvedByUserId, resolvedByName = resolvedByName, resolutionDescription = resolutionDescription, resolvedAt = resolvedAt)
