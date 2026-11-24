package com.example.saba.bff.controller;

import com.example.saba.bff.client.ParkingClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API pública de parking; la persistencia y validación de negocio siguen en
 * parking.
 */
@RestController
@RequestMapping("/api/parking")
public class ParkingBffController {

    private final ParkingClient client;

    public ParkingBffController(ParkingClient client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity<String> list(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return client.list(authorization);
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

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> patch(@PathVariable long id, @RequestBody String json,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return client.patch(id, json, authorization);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id,
            @RequestHeader(HttpHeaders.IF_MATCH) String ifMatch,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return client.delete(id, ifMatch, authorization);
    }
}
