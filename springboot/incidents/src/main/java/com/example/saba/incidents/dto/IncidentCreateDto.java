package com.example.saba.incidents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** El cliente debe enviar parkingId remoto, no el ID autogenerado por Room. */
public record IncidentCreateDto(
        @NotBlank @Size(max = 60) String ticketCode,
        @NotNull @Positive Long parkingId,
        @NotNull @Positive Long serviceId,
        @Size(max = 150) String reporterName,
        @NotBlank @Size(max = 120) String failureType,
        @NotBlank @Size(max = 240) String description,
        @Pattern(regexp = "degradado|fuera-servicio", message = "Estado no permitido") String status,
        String evidenceUri,
        String evidenceFileName,
        String evidenceMimeType
) { }
