package com.workshop.bff.client;


import com.workshop.bff.tracing.RequestIdRestClientInterceptor;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TicketClient {

    private final RestClient restClient;

    public TicketClient(@Value("${api.ticket.baseUrl}") String ticketBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(ticketBaseUrl)
                .requestInterceptor(RequestIdRestClientInterceptor::intercept)
                .build();
    }

    public ResponseEntity<Object> getById(Long id) {
        return restClient.get()
                .uri("/tickets/id/{id}", id)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByStatus(String status) {
        return restClient.get()
                .uri("/tickets/status/{status}", status)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByCustomer(Long customerId) {
        return restClient.get()
                .uri("/tickets?customerId={customerId}", customerId)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> create(Map<String, Object> body) {
        return restClient.post()
                .uri("/tickets")
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }
}
