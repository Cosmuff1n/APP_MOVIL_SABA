package com.example.saba.incidents.exception;

public class IncidentNotFoundException extends RuntimeException {
    public IncidentNotFoundException(long id) {
        super("No existe la incidencia " + id);
    }
}
