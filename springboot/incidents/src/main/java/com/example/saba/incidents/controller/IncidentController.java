package com.example.saba.incidents.controller;

import com.example.saba.incidents.dto.IncidentCreateDto;
import com.example.saba.incidents.dto.IncidentDto;
import com.example.saba.incidents.dto.IncidentUpdateDto;
import com.example.saba.incidents.service.IncidentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @GetMapping
    public List<IncidentDto> list(@RequestParam(required = false) Long parkingId,
                                  @RequestParam(required = false) Long serviceId) {
        return service.list(parkingId, serviceId);
    }

    @GetMapping("/{id}")
    public IncidentDto get(@PathVariable long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<IncidentDto> create(@Valid @RequestBody IncidentCreateDto request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        IncidentDto created = service.create(request, authorization);
        return ResponseEntity.created(URI.create("/api/incidents/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public IncidentDto update(@PathVariable long id, @Valid @RequestBody IncidentUpdateDto request) {
        return service.update(id, request);
    }
}
