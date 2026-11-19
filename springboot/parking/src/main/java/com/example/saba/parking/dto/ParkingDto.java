package com.example.saba.parking.dto;

/** JSON de respuesta, compatible con Ec2ParkingDto.kt (updatedAt en milisegundos). */
public record ParkingDto(
        Long id,
        String name,
        String address,
        String commune,
        int totalSpaces,
        int freeSpaces,
        boolean isActive,
        Long revision,
        Long updatedAt,
        String status
) { }
