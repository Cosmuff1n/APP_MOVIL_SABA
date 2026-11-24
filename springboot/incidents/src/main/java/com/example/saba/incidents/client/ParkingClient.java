package com.example.saba.incidents.client;

/**
 * Contrato interno: verificar la existencia de un estacionamiento REMOTO.
 */
public interface ParkingClient {

    void assertExists(long remoteParkingId, String authorizationHeader);
}
