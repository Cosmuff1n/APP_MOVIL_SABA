package com.example.saba.bff.client;


import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class IncidenciaClient {

    private final RestClient restClient;

    public IncidenciaClient(@Value("${api.incidencia.baseUrl}") String incidenciaBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(incidenciaBaseUrl)
                .build();
    }

    public ResponseEntity<Object> getById(Long id) {
        return restClient.get()
                .uri("/incidencias/id/{id}", id)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByStatus(String status) {
        return restClient.get()
                .uri("/incidencias/status/{status}", status)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByCustomer(Long customerId) {
        return restClient.get()
                .uri("/incidencias?customerId={customerId}", customerId)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> create(Map<String, Object> body) {
        return restClient.post()
                .uri("/incidencias")
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }
}
