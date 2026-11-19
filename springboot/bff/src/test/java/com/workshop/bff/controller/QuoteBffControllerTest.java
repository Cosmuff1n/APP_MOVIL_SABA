package com.workshop.bff.controller;

import com.workshop.bff.client.QuoteClient;
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
class QuoteBffControllerTest {

    @Mock
    private QuoteClient quoteClient;

    @Test
    void getQuotesByTicket_shouldDelegateToQuoteClient() {
        QuoteBffController controller = new QuoteBffController(quoteClient);

        when(quoteClient.getByTicket(20L)).thenReturn(ResponseEntity.ok(Map.of("ticketId", 20L)));

        ResponseEntity<Object> result = controller.getQuotesByTicket(20L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(quoteClient).getByTicket(20L);
    }

    @Test
    void getQuoteById_shouldDelegateToQuoteClient() {
        QuoteBffController controller = new QuoteBffController(quoteClient);

        when(quoteClient.getById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        ResponseEntity<Object> result = controller.getQuoteById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(quoteClient).getById(1L);
    }

    @Test
    void create_shouldDelegateToQuoteClient() {
        QuoteBffController controller = new QuoteBffController(quoteClient);
        Map<String, Object> body = Map.of("ticketId", 20L, "estimatedCost", 45000);

        when(quoteClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(quoteClient).create(body);
    }

    @Test
    void approve_shouldDelegateToQuoteClient() {
        QuoteBffController controller = new QuoteBffController(quoteClient);

        when(quoteClient.approve(1L)).thenReturn(ResponseEntity.ok(Map.of("status", "APPROVED")));

        ResponseEntity<Object> result = controller.approve(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(quoteClient).approve(1L);
    }

    @Test
    void reject_shouldDelegateToQuoteClient() {
        QuoteBffController controller = new QuoteBffController(quoteClient);

        when(quoteClient.reject(1L)).thenReturn(ResponseEntity.ok(Map.of("status", "REJECTED")));

        ResponseEntity<Object> result = controller.reject(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(quoteClient).reject(1L);
    }
}
