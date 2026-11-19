package com.example.saba.parking.serviceimpl;

import com.example.saba.parking.dto.ParkingCreateDto;
import com.example.saba.parking.dto.ParkingDto;
import com.example.saba.parking.dto.ParkingPatchDto;
import com.example.saba.parking.exception.InvalidParkingException;
import com.example.saba.parking.exception.ParkingNotFoundException;
import com.example.saba.parking.exception.RevisionConflictException;
import com.example.saba.parking.model.ParkingEntity;
import com.example.saba.parking.repository.ParkingRepository;
import com.example.saba.parking.service.ParkingService;
import java.time.Clock;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParkingServiceImpl implements ParkingService {
    private final ParkingRepository repository;
    private final Clock clock;

    @Autowired
    public ParkingServiceImpl(ParkingRepository repository) {
        this(repository, Clock.systemUTC());
    }

    // Constructor alternativo para pruebas de tiempo determinista.
    ParkingServiceImpl(ParkingRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingDto> list() {
        return repository.findAllByOrderByNameAsc().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingDto getById(long id) {
        return toDto(find(id));
    }

    @Override
    public ParkingDto create(ParkingCreateDto request) {
        String name = requiredName(request.name());
        String address = trimOptional(request.address());
        String commune = trimOptional(request.commune());
        int total = request.totalSpaces() == null ? 0 : request.totalSpaces();
        int free = request.freeSpaces() == null ? 0 : request.freeSpaces();
        boolean active = request.isActive() == null || request.isActive();
        validateSpaces(total, free);
        ParkingEntity entity = new ParkingEntity(name, address, commune,
                total, free, active, clock.millis());
        return toDto(repository.saveAndFlush(entity));
    }

    @Override
    public ParkingDto patch(long id, ParkingPatchDto request) {
        ParkingEntity entity = find(id);
        verifyRevision(entity, request.revision());
        if (request.name() == null && request.isActive() == null) {
            throw new InvalidParkingException("Debe especificar name o isActive");
        }
        if (request.name() != null) {
            entity.setName(requiredName(request.name()));
        }
        if (request.isActive() != null) {
            entity.setActive(request.isActive());
        }
        entity.setUpdatedAt(clock.millis());
        // @Version impide una actualización perdida si otro usuario cambió la fila
        // después de verifyRevision. Flush fuerza la comprobación antes de responder.
        return toDto(repository.saveAndFlush(entity));
    }

    @Override
    public void delete(long id, long expectedRevision) {
        ParkingEntity entity = find(id);
        verifyRevision(entity, expectedRevision);
        repository.delete(entity);
        repository.flush();
    }

    private ParkingEntity find(long id) {
        if (id <= 0) {
            throw new InvalidParkingException("id debe ser positivo");
        }
        return repository.findById(id).orElseThrow(() -> new ParkingNotFoundException(id));
    }

    private void verifyRevision(ParkingEntity entity, Long expected) {
        if (expected == null || expected < 0) {
            throw new InvalidParkingException("revision debe ser un número no negativo");
        }
        if (!expected.equals(entity.getRevision())) {
            throw new RevisionConflictException();
        }
    }

    private static String requiredName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidParkingException("El nombre es obligatorio");
        }
        String normalized = name.trim();
        if (normalized.length() > 150) {
            throw new InvalidParkingException("El nombre no puede superar 150 caracteres");
        }
        return normalized;
    }

    private static String trimOptional(String value) {
        return value == null ? "" : value.trim();
    }

    private static void validateSpaces(int total, int free) {
        if (total < 0 || free < 0 || free > total) {
            throw new InvalidParkingException("Las plazas libres deben estar entre 0 y totalSpaces");
        }
    }

    private ParkingDto toDto(ParkingEntity e) {
        // status describe únicamente activación administrativa; NO la salud de equipos.
        return new ParkingDto(e.getId(), e.getName(), e.getAddress(), e.getCommune(),
                e.getTotalSpaces(), e.getFreeSpaces(), e.isActive(), e.getRevision(),
                e.getUpdatedAt(), e.isActive() ? "Activo" : "Inactivo");
    }
}
