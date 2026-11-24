package com.example.saba.incidents.serviceimpl;

import com.example.saba.incidents.client.ParkingClient;
import com.example.saba.incidents.dto.IncidentDto;
import com.example.saba.incidents.exception.IncidentConflictException;
import com.example.saba.incidents.exception.InvalidIncidentException;
import com.example.saba.incidents.model.IncidentEntity;
import com.example.saba.incidents.repository.IncidentRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IncidentServiceImplTest {
    private IncidentRepository repository;
    private ParkingClient parking;
    private IncidentServiceImpl service;

    @BeforeEach void setup() {
        repository = mock(IncidentRepository.class);
        parking = mock(ParkingClient.class);
        Clock clock = Clock.fixed(Instant.ofEpochMilli(1_700_000_000_000L), ZoneOffset.UTC);
        service = new IncidentServiceImpl(repository, parking, clock);
        when(repository.saveAndFlush(any(IncidentEntity.class))).thenAnswer(i -> i.getArgument(0));
    }

    private IncidentDto valid() {
        return createRequest("SABA-A1234567", 15L, 3L, "Operador A", "Barrera",
                "La barrera de entrada no responde", "fuera-servicio");
    }

    private static IncidentDto createRequest(String code, Long parkingId, Long serviceId,
             String reporter, String failure, String description, String status) {
        return new IncidentDto(null, code, parkingId, serviceId, reporter, failure,
                description, status, null, null, null, null, null);
    }

    private static IncidentDto updateRequest(String failure, String description, String status,
             Boolean resolved, String by, String resolution) {
        return new IncidentDto(null, null, null, null, null, failure, description,
                status, null, resolved, by, resolution, null);
    }

    private IncidentEntity existing() {
        return new IncidentEntity("SABA-A1234567", 15L, 3L, "Operador A", "Barrera",
                "La barrera no responde", "fuera-servicio", 1_690_000_000_000L);
    }

    @Test void createValidatesParkingAndPersists() {
        var result = service.create(valid(), "Bearer test-token");
        verify(parking).assertExists(15L, "Bearer test-token");
        verify(repository).saveAndFlush(any(IncidentEntity.class));
        assertEquals("SABA-A1234567", result.ticketCode());
        assertEquals(1_700_000_000_000L, result.reportedAt());
        assertFalse(result.isResolved());
    }

    @Test void duplicateTicketIsRejectedBeforeRemoteCall() {
        when(repository.existsByTicketCode("SABA-A1234567")).thenReturn(true);
        assertThrows(IncidentConflictException.class,
                () -> service.create(valid(), "Bearer test-token"));
        verifyNoInteractions(parking);
    }

    @Test void rejectsServerManagedFieldsInCreate() {
        var request = new IncidentDto(999L, "SABA-A1234567", 15L, 3L, "A",
                "Barrera", "Falla", "degradado", null, null, null, null, null);
        assertThrows(InvalidIncidentException.class,
                () -> service.create(request, "Bearer token"));
        verifyNoInteractions(parking);
    }

    @Test void resolvesIncidentWithServerTimestamp() {
        var entity = existing();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        var request = updateRequest("Barrera", "Reemplazo de sensor", "fuera-servicio",
                true, "Técnico", "Se reemplazó el sensor");
        var result = service.update(1L, request);
        assertTrue(result.isResolved());
        assertEquals(1_700_000_000_000L, result.resolvedAt());
        assertEquals("Técnico", result.resolvedByName());
        verify(repository).saveAndFlush(entity);
    }

    @Test void resolvedIncidentCannotBeModifiedAgain() {
        var entity = existing();
        entity.resolve("Técnico", "Solucionado", 1_680_000_000_000L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        assertThrows(IncidentConflictException.class, () -> service.update(1L,
                updateRequest("Barrera", "Falla", "degradado", false, null, null)));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test void cannotAssociateRemoteIdZero() {
        var input = createRequest("SABA-A1234567", 0L, 3L, "A",
                "Falla", "Problema", "degradado");
        assertThrows(InvalidIncidentException.class, () -> service.create(input, "Bearer token"));
    }

    @Test void updateRejectsIdentityChange() {
        var req = createRequest("SABA-OTRO", 15L, 3L, "A", "Barrera", "Falla", "degradado");
        assertThrows(InvalidIncidentException.class, () -> service.update(1L, req));
        verify(repository, never()).findById(anyLong());
    }

    @Test void putRequiresResolvedValue() {
        var entity = existing();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        assertThrows(InvalidIncidentException.class, () -> service.update(1L,
                updateRequest("Barrera", "Falla", "degradado", null, null, null)));
    }

    @Test void listUsesRemoteParkingFilter() {
        when(repository.findByParkingIdOrderByReportedAtDescIdDesc(15L))
                .thenReturn(java.util.List.of(existing()));
        var result = service.list(15L, null);
        assertEquals(1, result.size());
        verify(repository).findByParkingIdOrderByReportedAtDescIdDesc(15L);
    }
}
