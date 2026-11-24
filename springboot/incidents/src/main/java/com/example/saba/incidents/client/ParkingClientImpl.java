package com.example.saba.incidents.client;

import com.example.saba.incidents.exception.DependencyUnavailableException;
import com.example.saba.incidents.exception.InvalidIncidentException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * HTTP hacia MS parking; nunca accede a su PostgreSQL directamente.
 */
@Component
public class ParkingClientImpl implements ParkingClient {

    private final RestClient client;

    @Autowired
    public ParkingClientImpl(@Value("${saba.services.parking.base-url}") String baseUrl) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    // Punto de inyección para pruebas unitarias con un servidor HTTP simulado.
    ParkingClientImpl(RestClient client) {
        this.client = client;
    }

    @Override
    public void assertExists(long remoteParkingId, String authorizationHeader) {
        if (remoteParkingId <= 0) {
            throw new InvalidIncidentException("parkingId debe ser un ID remoto positivo");
        }
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new InvalidIncidentException("Falta el access token para verificar el estacionamiento");
        }
        try {
            client.get()
                    .uri("/api/parking/{id}", remoteParkingId)
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new InvalidIncidentException("No existe el estacionamiento remoto " + remoteParkingId);
        } catch (ResourceAccessException ex) {
            throw new DependencyUnavailableException("El microservicio de Estacionamientos no responde");
        } catch (RestClientResponseException ex) {
            throw new DependencyUnavailableException("No se pudo validar el estacionamiento remoto");
        }
    }
}
