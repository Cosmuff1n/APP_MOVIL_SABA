package com.example.saba.bff.client;

import java.time.Duration;
import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Delega en parking mediante REST; nunca accede a su PostgreSQL.
 */
@Component
public class ParkingClientImpl implements ParkingClient {

    private final RestClient client;

    @Autowired
    public ParkingClientImpl(@Value("${saba.services.parking.base-url}") String baseUrl) {
        // JDK HttpClient soporta PATCH; HttpURLConnection/SimpleClientHttpRequestFactory no.
        var httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    // Constructor para pruebas con servidor HTTP simulado.
    ParkingClientImpl(RestClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<String> list(String authorization) {
        return client.get().uri("/api/parking")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve().toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> get(long id, String authorization) {
        return client.get().uri("/api/parking/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve().toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> create(String json, String authorization) {
        return client.post().uri("/api/parking")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).body(json)
                .retrieve().toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> patch(long id, String json, String authorization) {
        return client.patch().uri("/api/parking/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).body(json)
                .retrieve().toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> delete(long id, String ifMatch, String authorization) {
        return client.delete().uri("/api/parking/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header(HttpHeaders.IF_MATCH, ifMatch)
                .retrieve().toEntity(String.class);
    }
}
