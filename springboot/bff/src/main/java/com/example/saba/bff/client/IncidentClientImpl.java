package com.example.saba.bff.client;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Delega en incidents mediante REST; no consulta su PostgreSQL.
 */
@Component
public class IncidentClientImpl implements IncidentClient {

    private final RestClient client;

    @Autowired
    public IncidentClientImpl(@Value("${saba.services.incidents.base-url}") String baseUrl) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    IncidentClientImpl(RestClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<String> list(Long parkingId, Long serviceId, String authorization) {
        return client.get().uri(builder -> {
            builder.path("/api/incidents");
            if (parkingId != null) {
                builder.queryParam("parkingId", parkingId);
            }
            if (serviceId != null) {
                builder.queryParam("serviceId", serviceId);
            }
            return builder.build();
        })
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve().toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> get(long id, String authorization) {
        return client.get().uri("/api/incidents/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve().toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> create(String json, String authorization) {
        return client.post().uri("/api/incidents")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).body(json)
                .retrieve().toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> update(long id, String json, String authorization) {
        return client.put().uri("/api/incidents/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).body(json)
                .retrieve().toEntity(String.class);
    }
}
