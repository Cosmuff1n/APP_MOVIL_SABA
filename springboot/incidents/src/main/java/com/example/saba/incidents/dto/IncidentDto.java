package com.example.saba.incidents.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Unico contrato HTTP de Incidencias para GET, POST y PUT.
 *
 * Las reglas que cambian segun la operacion se validan en IncidentServiceImpl:
 * POST exige ticketCode, parkingId, serviceId, failureType y description; PUT
 * exige failureType, description, status e isResolved. id, reportedAt y
 * resolvedAt son asignados por el servidor. No se exponen IDs de usuario Room
 * ni URI de evidencia que no existen en remoto.
 */
public record IncidentDto(
        Long id,
        @Size(max = 60)
        String ticketCode,
        @Positive
        Long parkingId,
        @Positive
        Long serviceId,
        @Size(max = 150)
        String reporterName,
        @Size(max = 120)
        String failureType,
        @Size(max = 240)
        String description,
        @Pattern(regexp = "degradado|fuera-servicio", message = "Estado no permitido")
        String status,
        Long reportedAt,
        Boolean isResolved,
        @Size(max = 150)
        String resolvedByName,
        @Size(max = 300)
        String resolutionDescription,
        Long resolvedAt) {

}
