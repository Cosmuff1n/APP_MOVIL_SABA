package com.workshop.bff.controller;

import com.workshop.bff.client.PaymentClient;
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
class PaymentBffControllerTest {

    @Mock
    private PaymentClient paymentClient;

    @Test
    void getPaymentsByCustomer_shouldDelegateToPaymentClient() {
        PaymentBffController controller = new PaymentBffController(paymentClient);

        when(paymentClient.getByCustomer(10L)).thenReturn(ResponseEntity.ok(Map.of("customerId", 10L)));

        ResponseEntity<Object> result = controller.getPaymentsByCustomer(10L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(paymentClient).getByCustomer(10L);
    }

    @Test
    void getPaymentsByTicket_shouldDelegateToPaymentClient() {
        PaymentBffController controller = new PaymentBffController(paymentClient);

        when(paymentClient.getByTicket(20L)).thenReturn(ResponseEntity.ok(Map.of("ticketId", 20L)));

        ResponseEntity<Object> result = controller.getPaymentsByTicket(20L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(paymentClient).getByTicket(20L);
    }

    @Test
    void getPaymentById_shouldDelegateToPaymentClient() {
        PaymentBffController controller = new PaymentBffController(paymentClient);

        when(paymentClient.getById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        ResponseEntity<Object> result = controller.getPaymentById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(paymentClient).getById(1L);
    }

    @Test
    void create_shouldDelegateToPaymentClient() {
        PaymentBffController controller = new PaymentBffController(paymentClient);
        Map<String, Object> body = Map.of("customerId", 10L, "amount", 45000);

        when(paymentClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(paymentClient).create(body);
    }

    @Test
    void markAsPaid_shouldDelegateToPaymentClient() {
        PaymentBffController controller = new PaymentBffController(paymentClient);

        when(paymentClient.markAsPaid(1L)).thenReturn(ResponseEntity.ok(Map.of("status", "PAID")));

        ResponseEntity<Object> result = controller.markAsPaid(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(paymentClient).markAsPaid(1L);
    }
}
