package com.example.saba.bff.client;

import org.springframework.http.ResponseEntity;

/**
 * Contrato de delegación HTTP al microservicio parking.
 */
public interface ParkingClient {

    ResponseEntity<String> list(String authorization);

    ResponseEntity<String> get(long id, String authorization);

    ResponseEntity<String> create(String json, String authorization);

    ResponseEntity<String> patch(long id, String json, String authorization);

    ResponseEntity<String> delete(long id, String ifMatch, String authorization);
}
