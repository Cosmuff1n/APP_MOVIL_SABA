package com.example.saba.incidents.service;

import com.example.saba.incidents.dto.IncidentCreateDto;
import com.example.saba.incidents.dto.IncidentDto;
import com.example.saba.incidents.dto.IncidentUpdateDto;
import java.util.List;

/** Interfaz CSR: operaciones, sin lógica SQL ni de Controller. */
public interface IncidentService {
    List<IncidentDto> list(Long parkingId, Long serviceId);
    IncidentDto getById(long id);
    IncidentDto create(IncidentCreateDto request, String authorizationHeader);
    IncidentDto update(long id, IncidentUpdateDto request);
    // DELETE deliberadamente pendiente: política de trazabilidad (cap. 13.13).
}
