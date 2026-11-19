package com.example.saba.parking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** JSON de creación compatible con Ec2ParkingCreateDto.kt. */
public record ParkingCreateDto(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 255) String address,
        @Size(max = 120) String commune,
        @Min(0) Integer totalSpaces,
        @Min(0) Integer freeSpaces,
        Boolean isActive
) { }
