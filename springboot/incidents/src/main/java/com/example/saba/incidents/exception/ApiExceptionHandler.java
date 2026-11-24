package com.example.saba.incidents.exception;

import java.time.Instant;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

    public record ApiError(String code, String message, String timestamp) {

    }

    @ExceptionHandler(IncidentNotFoundException.class)
    public ResponseEntity<ApiError> notFound(IncidentNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, "INCIDENT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InvalidIncidentException.class)
    public ResponseEntity<ApiError> invalid(InvalidIncidentException ex) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_INCIDENT", ex.getMessage());
    }

    @ExceptionHandler({IncidentConflictException.class, OptimisticLockingFailureException.class,
        DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> conflict(Exception ex) {
        return response(HttpStatus.CONFLICT, "INCIDENT_CONFLICT",
                "Conflicto de registro o revisión; consulte la incidencia antes de reintentar");
    }

    @ExceptionHandler(DependencyUnavailableException.class)
    public ResponseEntity<ApiError> dependency(DependencyUnavailableException ex) {
        return response(HttpStatus.SERVICE_UNAVAILABLE, "PARKING_UNAVAILABLE", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalidBody(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst().orElse("Solicitud no válida");
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ApiError> invalidRequest(Exception ex) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST",
                "Cuerpo, ruta o encabezado no válido");
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(code, message, Instant.now().toString()));
    }
}
