package com.workshop.bff.controller;

import com.workshop.bff.client.InventoryClient;
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
class InventoryBffControllerTest {

    @Mock
    private InventoryClient inventoryClient;

    @Test
    void listAll_shouldDelegateToInventoryClient() {
        InventoryBffController controller = new InventoryBffController(inventoryClient);

        when(inventoryClient.getAll()).thenReturn(ResponseEntity.ok(List.of(Map.of("partName", "HDMI Port"))));

        ResponseEntity<Object> result = controller.listAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(inventoryClient).getAll();
    }

    @Test
    void getInventoryItemById_shouldDelegateToInventoryClient() {
        InventoryBffController controller = new InventoryBffController(inventoryClient);

        when(inventoryClient.getById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        ResponseEntity<Object> result = controller.getInventoryItemById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(inventoryClient).getById(1L);
    }

    @Test
    void create_shouldDelegateToInventoryClient() {
        InventoryBffController controller = new InventoryBffController(inventoryClient);
        Map<String, Object> body = Map.of("partName", "HDMI Port", "stock", 5);

        when(inventoryClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(inventoryClient).create(body);
    }

    @Test
    void updateStock_shouldDelegateToInventoryClient() {
        InventoryBffController controller = new InventoryBffController(inventoryClient);
        Map<String, Object> body = Map.of("stock", 4);

        when(inventoryClient.updateStock(1L, body)).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.updateStock(1L, body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(inventoryClient).updateStock(1L, body);
    }
}
