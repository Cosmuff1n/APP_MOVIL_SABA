package com.example.saba.incidents.serviceimpl;

import com.example.saba.incidents.client.ParkingClient;
import com.example.saba.incidents.dto.IncidentDto;
import com.example.saba.incidents.exception.IncidentConflictException;
import com.example.saba.incidents.exception.IncidentNotFoundException;
import com.example.saba.incidents.exception.InvalidIncidentException;
import com.example.saba.incidents.model.IncidentEntity;
import com.example.saba.incidents.repository.IncidentRepository;
import com.example.saba.incidents.service.IncidentService;
import java.time.Clock;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lógica de negocio: no contiene código de HTTP ni acceso SQL directo.
 */
@Service
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository repository;
    private final ParkingClient parkingClient;
    private final Clock clock;

    @Autowired
    public IncidentServiceImpl(IncidentRepository repository, ParkingClient parkingClient) {
        this(repository, parkingClient, Clock.systemUTC());
    }

    // Reloj inyectable para pruebas reproducibles.
    public IncidentServiceImpl(IncidentRepository repository, ParkingClient parkingClient, Clock clock) {
        this.repository = repository;
        this.parkingClient = parkingClient;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentDto> list(Long parkingId, Long serviceId) {
        if (parkingId != null && parkingId <= 0 || serviceId != null && serviceId <= 0) {
            throw new InvalidIncidentException("Los filtros parkingId y serviceId deben ser positivos");
        }
        List<IncidentEntity> result;
        if (parkingId != null && serviceId != null) {
            result = repository.findByParkingIdAndServiceIdOrderByReportedAtDescIdDesc(parkingId, serviceId);
        } else if (parkingId != null) {
            result = repository.findByParkingIdOrderByReportedAtDescIdDesc(parkingId);
        } else if (serviceId != null) {
            result = repository.findByServiceIdOrderByReportedAtDescIdDesc(serviceId);
        } else {
            result = repository.findAllByOrderByReportedAtDescIdDesc();
        }
        return result.stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentDto getById(long id) {
        return toDto(find(id));
    }

    @Override
    public IncidentDto create(IncidentDto request, String authorizationHeader) {
        if (request.id() != null || request.reportedAt() != null || request.resolvedAt() != null
                || Boolean.TRUE.equals(request.isResolved())
                || request.resolvedByName() != null || request.resolutionDescription() != null) {
            throw new InvalidIncidentException("Campos de identidad, fecha y resolución no admitidos en POST");
        }
        String code = requiredText(request.ticketCode(), "ticketCode", 60);
        String failure = requiredText(request.failureType(), "failureType", 120);
        String description = requiredText(request.description(), "description", 240);
        String state = request.status() == null ? "degradado" : validStatus(request.status());
        Long parkingId = request.parkingId();
        Long serviceId = request.serviceId();
        if (parkingId == null || parkingId <= 0 || serviceId == null || serviceId <= 0) {
            throw new InvalidIncidentException("parkingId y serviceId deben ser IDs remotos positivos");
        }
        if (repository.existsByTicketCode(code)) {
            throw new IncidentConflictException("Ya existe una incidencia con ese ticketCode");
        }
        // No grabar un ID Room arbitrario como FK remota; validar parking por HTTP.
        parkingClient.assertExists(parkingId, authorizationHeader);
        var entity = new IncidentEntity(code, parkingId, serviceId,
                optionalText(request.reporterName(), "reporterName", 150),
                failure, description, state, clock.millis());
        return toDto(repository.saveAndFlush(entity));
    }

    @Override
    public IncidentDto update(long id, IncidentDto request) {
        if (request.id() != null || request.ticketCode() != null || request.parkingId() != null
                || request.serviceId() != null || request.reporterName() != null
                || request.reportedAt() != null || request.resolvedAt() != null) {
            throw new InvalidIncidentException("PUT no permite modificar identidad, vínculos ni fechas");
        }
        IncidentEntity entity = find(id);
        if (entity.isResolved()) {
            throw new IncidentConflictException("La incidencia ya fue resuelta; no se permite reabrirla ni modificarla");
        }
        String state = validStatus(request.status());
        String failure = requiredText(request.failureType(), "failureType", 120);
        String description = requiredText(request.description(), "description", 240);
        if (request.isResolved() == null) {
            throw new InvalidIncidentException("isResolved debe ser true o false");
        }
        if (request.isResolved()) {
            String byName = requiredText(request.resolvedByName(), "resolvedByName", 150);
            String solution = requiredText(request.resolutionDescription(), "resolutionDescription", 300);
            entity.resolve(byName, solution, clock.millis());
        } else if (request.resolvedByName() != null || request.resolutionDescription() != null) {
            throw new InvalidIncidentException("Los datos de resolución solo corresponden a isResolved=true");
        }
        entity.setFailureType(failure);
        entity.setDescription(description);
        entity.setStatus(state);
        return toDto(repository.saveAndFlush(entity));
    }

    private IncidentEntity find(long id) {
        if (id <= 0) {
            throw new InvalidIncidentException("id debe ser positivo");
        }
        return repository.findById(id).orElseThrow(() -> new IncidentNotFoundException(id));
    }

    private static String requiredText(String text, String name, int maximum) {
        if (text == null || text.isBlank()) {
            throw new InvalidIncidentException(name + " es obligatorio");
        }
        String trimmed = text.trim();
        if (trimmed.length() > maximum) {
            throw new InvalidIncidentException(name + " supera " + maximum + " caracteres");
        }
        return trimmed;
    }

    private static String optionalText(String text, String name, int maximum) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        if (trimmed.length() > maximum) {
            throw new InvalidIncidentException(name + " supera " + maximum + " caracteres");
        }
        return trimmed;
    }

    private static String validStatus(String state) {
        if (!"degradado".equals(state) && !"fuera-servicio".equals(state)) {
            throw new InvalidIncidentException("status debe ser degradado o fuera-servicio");
        }
        return state;
    }

    private IncidentDto toDto(IncidentEntity e) {
        return new IncidentDto(e.getId(), e.getTicketCode(), e.getParkingId(), e.getServiceId(),
                e.getReporterName(), e.getFailureType(), e.getDescription(), e.getStatus(),
                e.getReportedAt(), e.isResolved(), e.getResolvedByName(),
                e.getResolutionDescription(), e.getResolvedAt());
    }
}
