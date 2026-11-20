package com.example.saba.incidents.service;

import com.example.saba.incidents.dto.IncidentDto;
import java.util.List;

/** Interfaz CSR: operaciones, sin lógica SQL ni de Controller. */
public interface IncidentService {
    List<IncidentDto> list(Long parkingId, Long serviceId);
    IncidentDto getById(long id);
    IncidentDto create(IncidentDto request, String authorizationHeader);
    IncidentDto update(long id, IncidentDto request);
    // DELETE deliberadamente pendiente: política de trazabilidad (cap. 13.13).
}
