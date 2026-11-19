package com.example.saba.parking.service;

import com.example.saba.parking.dto.ParkingCreateDto;
import com.example.saba.parking.dto.ParkingDto;
import com.example.saba.parking.dto.ParkingPatchDto;
import java.util.List;

/** Contrato de operaciones, separado de la implementación CSR. */
public interface ParkingService {
    List<ParkingDto> list();
    ParkingDto getById(long id);
    ParkingDto create(ParkingCreateDto request);
    ParkingDto patch(long id, ParkingPatchDto request);
    void delete(long id, long expectedRevision);
}
