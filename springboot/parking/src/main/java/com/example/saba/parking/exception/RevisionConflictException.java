package com.example.saba.parking.exception;

public class RevisionConflictException extends RuntimeException {
    public RevisionConflictException() {
        super("El estacionamiento fue modificado por otro usuario. Actualice la lista e intente nuevamente.");
    }
}
