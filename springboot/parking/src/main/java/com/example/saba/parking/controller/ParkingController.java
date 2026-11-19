package com.example.saba.parking.controller;

import com.example.saba.parking.dto.ParkingCreateDto;
import com.example.saba.parking.dto.ParkingDto;
import com.example.saba.parking.dto.ParkingPatchDto;
import com.example.saba.parking.exception.InvalidParkingException;
import com.example.saba.parking.service.ParkingService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parkings")
public class ParkingController {

    private final ParkingService service;

    public ParkingController(ParkingService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParkingDto> list() {
        return service.list();
    }

    // Ruta interna adicional para verificar un estacionamiento desde Incidencias.
    @GetMapping("/{id}")
    public ParkingDto get(@PathVariable long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<ParkingDto> create(@Valid @RequestBody ParkingCreateDto request) {
        ParkingDto created = service.create(request);
        return ResponseEntity.created(URI.create("/api/parkings/" + created.id())).body(created);
    }

    // PATCH y no PUT: es el método declarado por Ec2ParkingApiService.kt.
    @PatchMapping("/{id}")
    public ParkingDto patch(@PathVariable long id, @Valid @RequestBody ParkingPatchDto request) {
        return service.patch(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id,
            @RequestHeader("If-Match") String revision) {
        final long parsed;
        try {
            parsed = Long.parseLong(revision.trim());
        } catch (NumberFormatException ex) {
            throw new InvalidParkingException("If-Match debe contener una revisión numérica");
        }
        service.delete(id, parsed);
        return ResponseEntity.noContent().build();
    }
}
