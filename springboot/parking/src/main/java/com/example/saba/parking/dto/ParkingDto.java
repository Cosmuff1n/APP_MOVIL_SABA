package com.example.saba.parking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Unico contrato HTTP de Parking (GET, POST y PATCH).
 *
 * En POST el servidor asigna id/revision/updatedAt/status; el nombre es obligatorio.
 * En PATCH se admite revision junto a name o isActive, sin otros cambios.
 * Las reglas dependientes de la operacion se validan en ParkingServiceImpl.
 * Los tipos envoltorio distinguen un campo omitido de 0/false.
 */
public record ParkingDto(
        Long id,
        @Size(max = 150) String name,
        @Size(max = 255) String address,
        @Size(max = 120) String commune,
        @Min(0) Integer totalSpaces,
        @Min(0) Integer freeSpaces,
        Boolean isActive,
        @Min(0) Long revision,
        Long updatedAt,
        String status) {
}
