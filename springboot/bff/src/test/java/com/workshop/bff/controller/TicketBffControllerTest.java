package com.workshop.bff.controller;

import com.workshop.bff.client.TicketClient;
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
class TicketBffControllerTest {

    @Mock
    private TicketClient ticketClient;

    @Test
    void getTicketById_shouldDelegateToTicketClient() {
        TicketBffController controller = new TicketBffController(ticketClient);

        when(ticketClient.getById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        ResponseEntity<Object> result = controller.getTicketById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(ticketClient).getById(1L);
    }

    @Test
    void getTicketsByCustomer_shouldDelegateToTicketClient() {
        TicketBffController controller = new TicketBffController(ticketClient);

        when(ticketClient.getByCustomer(10L)).thenReturn(ResponseEntity.ok(Map.of("customerId", 10L)));

        ResponseEntity<Object> result = controller.getTicketsByCustomer(10L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(ticketClient).getByCustomer(10L);
    }

    @Test
    void create_shouldDelegateToTicketClient() {
        TicketBffController controller = new TicketBffController(ticketClient);
        Map<String, Object> body = Map.of("customerId", 10L, "status", "RECEIVED");

        when(ticketClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(ticketClient).create(body);
    }
}
