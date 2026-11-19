package com.workshop.bff.client;


import com.workshop.bff.tracing.RequestIdRestClientInterceptor;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CustomerClient {

    private final RestClient restClient;

    public CustomerClient(@Value("${api.customer.baseUrl}") String customerBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(customerBaseUrl)
                .requestInterceptor(RequestIdRestClientInterceptor::intercept)
                .build();
    }

    public ResponseEntity<Object> getAll() {
        return restClient.get()
                .uri("/customers/all")
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getById(Long id) {
        return restClient.get()
                .uri("/customers/id/{id}", id)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByEmail(String email) {
        return restClient.get()
                .uri("/customers/email/{email}", email)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByName(String name) {
        return restClient.get()
                .uri("/customers/name/{name}", name)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> create(Map<String, Object> body) {
        return restClient.post()
                .uri("/customers")
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }
}
