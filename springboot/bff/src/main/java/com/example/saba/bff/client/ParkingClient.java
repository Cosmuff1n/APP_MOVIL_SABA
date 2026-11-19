package com.example.saba.bff.client;


import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ParkingClient {

    private final RestClient restClient;

    public ParkingClient(@Value("${api.parking.baseUrl}") String parkingBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(parkingBaseUrl)
                .build();
    }

    public ResponseEntity<Object> getAll() {
        return restClient.get()
                .uri("/parkings/all")
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getById(Long id) {
        return restClient.get()
                .uri("/parkings/id/{id}", id)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByEmail(String email) {
        return restClient.get()
                .uri("/parkings/email/{email}", email)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByName(String name) {
        return restClient.get()
                .uri("/parkings/name/{name}", name)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> create(Map<String, Object> body) {
        return restClient.post()
                .uri("/parkings")
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }
}
