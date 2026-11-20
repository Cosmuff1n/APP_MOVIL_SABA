package com.example.saba.bff.exception;

import java.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

/** Conserva estado/cuerpo de errores upstream, y controla fallos de conectividad. */
@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ApiError(String code, String message, String timestamp) { }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<String> upstreamHttp(RestClientResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.getResponseBodyAsString());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiError> unavailable(ResourceAccessException ex) {
        // No revelar direcciones, tokens ni configuración interna en la respuesta.
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                new ApiError("DOWNSTREAM_UNAVAILABLE",
                        "No se pudo contactar el microservicio requerido", Instant.now().toString()));
    }
}
