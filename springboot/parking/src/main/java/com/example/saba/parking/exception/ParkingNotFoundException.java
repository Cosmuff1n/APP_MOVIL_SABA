package com.example.saba.parking.exception;

public class ParkingNotFoundException extends RuntimeException {

    public ParkingNotFoundException(long id) {
        super("No existe el estacionamiento " + id);
    }
}
