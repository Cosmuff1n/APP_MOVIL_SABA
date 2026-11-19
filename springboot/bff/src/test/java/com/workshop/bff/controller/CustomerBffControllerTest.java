package com.workshop.bff.controller;

import com.workshop.bff.client.CustomerClient;
import java.util.List;
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
class CustomerBffControllerTest {

    @Mock
    private CustomerClient customerClient;

    @Test
    void listAll_shouldDelegateToCustomerClient() {
        CustomerBffController controller = new CustomerBffController(customerClient);
        ResponseEntity<Object> response = ResponseEntity.ok(List.of(Map.of("name", "Charlie")));

        when(customerClient.getAll()).thenReturn(response);

        ResponseEntity<Object> result = controller.listAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(customerClient).getAll();
    }

    @Test
    void getCustomerById_shouldDelegateToCustomerClient() {
        CustomerBffController controller = new CustomerBffController(customerClient);
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", 1L));

        when(customerClient.getById(1L)).thenReturn(response);

        ResponseEntity<Object> result = controller.getCustomerById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(customerClient).getById(1L);
    }

    @Test
    void create_shouldDelegateToCustomerClient() {
        CustomerBffController controller = new CustomerBffController(customerClient);
        Map<String, Object> body = Map.of("name", "Charlie", "email", "charlie@test.com");

        when(customerClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(customerClient).create(body);
    }
}
