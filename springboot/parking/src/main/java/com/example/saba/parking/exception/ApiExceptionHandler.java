package com.example.saba.parking.exception;

import java.time.Instant;
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
    public record ApiError(String code, String message, String timestamp) { }

    @ExceptionHandler(ParkingNotFoundException.class)
    public ResponseEntity<ApiError> notFound(ParkingNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, "PARKING_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler({RevisionConflictException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ApiError> conflict(Exception ex) {
        return response(HttpStatus.CONFLICT, "REVISION_CONFLICT",
                "El registro cambió; vuelva a consultar antes de editar o eliminar");
    }

    @ExceptionHandler(InvalidParkingException.class)
    public ResponseEntity<ApiError> invalid(InvalidParkingException ex) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_PARKING", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalidBody(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst().orElse("Solicitud no válida");
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ApiError> invalidRequest(Exception ex) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Cuerpo, ruta o encabezado no válido");
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(code, message, Instant.now().toString()));
    }
}
