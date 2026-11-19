package com.workshop.bff.controller;

import com.workshop.bff.client.TechnicianClient;
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
class TechnicianBffControllerTest {

    @Mock
    private TechnicianClient technicianClient;

    @Test
    void listAll_shouldDelegateToTechnicianClient() {
        TechnicianBffController controller = new TechnicianBffController(technicianClient);

        when(technicianClient.getAll()).thenReturn(ResponseEntity.ok(List.of(Map.of("name", "Alex"))));

        ResponseEntity<Object> result = controller.listAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(technicianClient).getAll();
    }

    @Test
    void getTechnicianById_shouldDelegateToTechnicianClient() {
        TechnicianBffController controller = new TechnicianBffController(technicianClient);

        when(technicianClient.getById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        ResponseEntity<Object> result = controller.getTechnicianById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(technicianClient).getById(1L);
    }

    @Test
    void create_shouldDelegateToTechnicianClient() {
        TechnicianBffController controller = new TechnicianBffController(technicianClient);
        Map<String, Object> body = Map.of("name", "Alex", "specialty", "PlayStation");

        when(technicianClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(technicianClient).create(body);
    }
}
