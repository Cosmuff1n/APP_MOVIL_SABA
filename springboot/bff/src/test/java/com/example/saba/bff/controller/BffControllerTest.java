package com.example.saba.bff.controller;

import com.example.saba.bff.client.IncidentClient;
import com.example.saba.bff.client.ParkingClient;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffControllerTest {
    @Test void parkingControllerDelegatesWithoutBusinessLogic() {
        ParkingClient client = mock(ParkingClient.class);
        var controller = new ParkingBffController(client);
        when(client.get(9, "Bearer jwt")).thenReturn(ResponseEntity.ok("{\"id\":9}"));
        assertEquals("{\"id\":9}", controller.get(9, "Bearer jwt").getBody());
        verify(client).get(9, "Bearer jwt");
    }

    @Test void incidentControllerDelegatesCreateAndReturnsDownstreamStatus() {
        IncidentClient client = mock(IncidentClient.class);
        var controller = new IncidentBffController(client);
        when(client.create("{}", "Bearer jwt"))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("{\"id\":6}"));
        assertEquals(HttpStatus.CREATED, controller.create("{}", "Bearer jwt").getStatusCode());
        verify(client).create("{}", "Bearer jwt");
    }
}
