package com.workshop.bff.controller;

import com.workshop.bff.client.RepairClient;
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
class RepairBffControllerTest {

    @Mock
    private RepairClient repairClient;

    @Test
    void getRepairsByTicket_shouldDelegateToRepairClient() {
        RepairBffController controller = new RepairBffController(repairClient);

        when(repairClient.getByTicket(20L)).thenReturn(ResponseEntity.ok(Map.of("ticketId", 20L)));

        ResponseEntity<Object> result = controller.getRepairsByTicket(20L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(repairClient).getByTicket(20L);
    }

    @Test
    void getRepairById_shouldDelegateToRepairClient() {
        RepairBffController controller = new RepairBffController(repairClient);

        when(repairClient.getById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        ResponseEntity<Object> result = controller.getRepairById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(repairClient).getById(1L);
    }

    @Test
    void create_shouldDelegateToRepairClient() {
        RepairBffController controller = new RepairBffController(repairClient);
        Map<String, Object> body = Map.of("ticketId", 20L, "status", "IN_PROGRESS");

        when(repairClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(repairClient).create(body);
    }

    @Test
    void updateStatus_shouldDelegateToRepairClient() {
        RepairBffController controller = new RepairBffController(repairClient);
        Map<String, Object> body = Map.of("status", "REPAIRED");

        when(repairClient.updateStatus(1L, body)).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.updateStatus(1L, body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(repairClient).updateStatus(1L, body);
    }
}
