package com.example.saba.incidents.dto;

/**
 * Contrato JSON remoto inspirado en los nombres de domain/model/Incident.kt.
 * Los identificadores de usuario de Room y las URI de archivos del teléfono NO
 * se consideran referencias remotas; por eso esos campos están sin resolver.
 */
public record IncidentDto(
        Long id,
        String ticketCode,
        Long parkingId,
        Long serviceId,
        Long reporterUserId,
        String reporterName,
        String failureType,
        String description,
        String status,
        Long reportedAt,
        String evidenceUri,
        String evidenceFileName,
        String evidenceMimeType,
        boolean isResolved,
        Long resolvedByUserId,
        String resolvedByName,
        String resolutionDescription,
        Long resolvedAt
) { }
