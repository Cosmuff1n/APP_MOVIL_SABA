package com.example.saba.parking.service;

import com.example.saba.parking.dto.ParkingDto;
import java.util.List;

/**
 * Contrato de operaciones, separado de la implementación CSR.
 */
public interface ParkingService {

    List<ParkingDto> list();

    ParkingDto getById(long id);

    ParkingDto create(ParkingDto request);

    ParkingDto patch(long id, ParkingDto request);

    void delete(long id, long expectedRevision);
}
