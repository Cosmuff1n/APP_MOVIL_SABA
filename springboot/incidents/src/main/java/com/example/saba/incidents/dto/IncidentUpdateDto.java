package com.example.saba.incidents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** PUT reemplaza los datos editables; los campos de identidad no cambian. */
public record IncidentUpdateDto(
        @NotBlank @Size(max = 120) String failureType,
        @NotBlank @Size(max = 240) String description,
        @NotBlank @Pattern(regexp = "degradado|fuera-servicio") String status,
        @NotNull Boolean isResolved,
        @Size(max = 150) String resolvedByName,
        @Size(max = 300) String resolutionDescription
) { }
