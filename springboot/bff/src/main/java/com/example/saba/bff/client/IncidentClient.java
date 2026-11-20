package com.example.saba.bff.client;

import org.springframework.http.ResponseEntity;

/** Contrato de delegación HTTP al microservicio incidents. */
public interface IncidentClient {
    ResponseEntity<String> list(Long parkingId, Long serviceId, String authorization);
    ResponseEntity<String> get(long id, String authorization);
    ResponseEntity<String> create(String json, String authorization);
    ResponseEntity<String> update(long id, String json, String authorization);
}
