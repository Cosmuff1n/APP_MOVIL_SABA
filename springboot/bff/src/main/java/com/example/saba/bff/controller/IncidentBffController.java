package com.example.saba.bff.controller;

import com.example.saba.bff.client.IncidentClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

/**
 * API pública de incidencias; el microservicio incidents conserva la lógica
 * CSR.
 */
@RestController
@RequestMapping("/api/incidents")
public class IncidentBffController {

    private final IncidentClient client;

    public IncidentBffController(IncidentClient client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity<String> list(@RequestParam(required = false) Long parkingId,
            @RequestParam(required = false) Long serviceId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return client.list(parkingId, serviceId, authorization);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return client.get(id, authorization);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody String json,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return client.create(json, authorization);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@PathVariable long id, @RequestBody String json,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return client.update(id, json, authorization);
    }
}
