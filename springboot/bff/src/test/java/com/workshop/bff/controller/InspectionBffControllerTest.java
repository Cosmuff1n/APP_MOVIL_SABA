package com.workshop.bff.controller;

import com.workshop.bff.client.InspectionClient;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyMap;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class InspectionBffControllerTest {

    @Mock
    private InspectionClient inspectionClient;

    @Test
    void getInspectionsByTicket_shouldDelegateToInspectionClient() {
        InspectionBffController controller = new InspectionBffController(inspectionClient);

        when(inspectionClient.getByTicket(20L)).thenReturn(ResponseEntity.ok(Map.of("ticketId", 20L)));

        ResponseEntity<Object> result = controller.getInspectionsByTicket(20L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(inspectionClient).getByTicket(20L);
    }

    @Test
    void getInspectionById_shouldDelegateToInspectionClient() {
        InspectionBffController controller = new InspectionBffController(inspectionClient);

        when(inspectionClient.getById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        ResponseEntity<Object> result = controller.getInspectionById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(inspectionClient).getById(1L);
    }

    @Test
    void create_shouldDelegateToInspectionClient() {
        InspectionBffController controller = new InspectionBffController(inspectionClient);
        Map<String, Object> body = Map.of("ticketId", 20L, "inspectionStatus", "CHECKED");

        when(inspectionClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(inspectionClient).create(body);
    }
}
