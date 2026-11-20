package com.example.saba.parking.serviceimpl;

import com.example.saba.parking.dto.ParkingDto;
import com.example.saba.parking.exception.InvalidParkingException;
import com.example.saba.parking.exception.ParkingNotFoundException;
import com.example.saba.parking.exception.RevisionConflictException;
import com.example.saba.parking.model.ParkingEntity;
import com.example.saba.parking.repository.ParkingRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceImplTest {

    @Mock
    ParkingRepository repository;
    ParkingServiceImpl service;

    @BeforeEach
    void setup() {
        service = new ParkingServiceImpl(repository);
    }

    // El mismo ParkingDto sirve para POST, PATCH y respuestas GET.
    private static ParkingDto createRequest(String name, String address, String commune,
                                            Integer total, Integer free, Boolean active) {
        return new ParkingDto(null, name, address, commune, total, free,
                active, null, null, null);
    }

    private static ParkingDto patchRequest(Long revision, String name, Boolean active) {
        return new ParkingDto(null, name, null, null, null, null,
                active, revision, null, null);
    }

    @Test
    void createTrimsNameAndDefaults() {
        when(repository.saveAndFlush(any())).thenAnswer(a -> a.getArgument(0));
        var dto = service.create(createRequest(" Central ", null, null, null, null, null));
        assertEquals("Central", dto.name());
        assertEquals("", dto.address());
        assertEquals(0, dto.totalSpaces());
        assertEquals("Activo", dto.status());
        verify(repository).saveAndFlush(any(ParkingEntity.class));
    }

    @Test
    void createRejectsInvalidSpaces() {
        assertThrows(InvalidParkingException.class, ()
                -> service.create(createRequest("Centro", "", "", 10, 12, true)));
        verifyNoInteractions(repository);
    }

    @Test
    void listReturnsDto() {
        when(repository.findAllByOrderByNameAsc()).thenReturn(List.of(
                new ParkingEntity("A", "Av 1", "Santiago", 10, 8, true, 1000L)));
        var result = service.list();
        assertEquals(1, result.size());
        assertEquals("A", result.getFirst().name());
        assertEquals("Activo", result.getFirst().status());
    }

    @Test
    void getMissingReturns404Exception() {
        when(repository.findById(8L)).thenReturn(Optional.empty());
        assertThrows(ParkingNotFoundException.class, () -> service.getById(8L));
    }

    @Test
    void patchChecksRevision() {
        ParkingEntity e = new ParkingEntity("Original", "", "", 0, 0, true, 1);
        when(repository.findById(4L)).thenReturn(Optional.of(e));
        assertThrows(RevisionConflictException.class, ()
                -> service.patch(4L, patchRequest(25L, "Nuevo", null)));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void patchRenamesAndChangesActive() {
        ParkingEntity e = new ParkingEntity("Antes", "", "", 0, 0, true, 1);
        ReflectionTestUtils.setField(e, "revision", 2L);
        when(repository.findById(4L)).thenReturn(Optional.of(e));
        when(repository.saveAndFlush(e)).thenAnswer(a -> {
            ReflectionTestUtils.setField(e, "revision", 3L);
            return e;
        });
        var result = service.patch(4L, patchRequest(2L, " Despues ", false));
        assertEquals("Despues", result.name());
        assertFalse(result.isActive());
        assertEquals("Inactivo", result.status());
        assertEquals(3L, result.revision());
    }

    @Test
    void deleteWhenRevisionMatches() {
        ParkingEntity e = new ParkingEntity("A", "", "", 0, 0, true, 1);
        ReflectionTestUtils.setField(e, "revision", 2L);
        when(repository.findById(4L)).thenReturn(Optional.of(e));
        service.delete(4L, 2L);
        verify(repository).delete(e);
        verify(repository).flush();
    }

    @Test
    void patchRejectsEmptyChange() {
        ParkingEntity e = new ParkingEntity("Original", "", "", 0, 0, true, 1);
        when(repository.findById(4L)).thenReturn(Optional.of(e));
        ReflectionTestUtils.setField(e, "revision", 2L);
        assertThrows(InvalidParkingException.class, ()
                -> service.patch(4L, patchRequest(2L, null, null)));
    }

    @Test
    void createRejectsServerManagedFields() {
        assertThrows(InvalidParkingException.class,
                () -> service.create(new ParkingDto(99L, "A", null, null,
                        null, null, null, null, null, null)));
        verifyNoInteractions(repository);
    }

    @Test
    void patchRejectsFieldsNotIncludedInPatchContract() {
        assertThrows(InvalidParkingException.class,
                () -> service.patch(4L, new ParkingDto(null, "A", "Otra dirección", null,
                        null, null, null, 0L, null, null)));
        verifyNoInteractions(repository);
    }

    @Test
    void deleteRejectsWrongRevision() {
        ParkingEntity e = new ParkingEntity("A", "", "", 0, 0, true, 1);
        when(repository.findById(4L)).thenReturn(Optional.of(e));
        assertThrows(RevisionConflictException.class, () -> service.delete(4L, 3L));
        verify(repository, never()).delete(any());
    }
}
