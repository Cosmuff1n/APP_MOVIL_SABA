package com.example.saba.parking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Campos parciales admitidos por Ec2ParkingPatchDto.kt. */
public record ParkingPatchDto(
        @NotNull @Min(0) Long revision,
        @Size(max = 150) String name,
        Boolean isActive
) { }
