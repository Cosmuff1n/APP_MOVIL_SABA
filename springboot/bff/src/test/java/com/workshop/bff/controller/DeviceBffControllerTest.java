package com.workshop.bff.controller;

import com.workshop.bff.client.DeviceClient;
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
class DeviceBffControllerTest {

    @Mock
    private DeviceClient deviceClient;

    @Test
    void getDevicesByCustomer_shouldDelegateToDeviceClient() {
        DeviceBffController controller = new DeviceBffController(deviceClient);

        when(deviceClient.getByCustomer(10L)).thenReturn(ResponseEntity.ok(Map.of("customerId", 10L)));

        ResponseEntity<Object> result = controller.getDevicesByCustomer(10L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(deviceClient).getByCustomer(10L);
    }

    @Test
    void getDeviceById_shouldDelegateToDeviceClient() {
        DeviceBffController controller = new DeviceBffController(deviceClient);

        when(deviceClient.getById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        ResponseEntity<Object> result = controller.getDeviceById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(deviceClient).getById(1L);
    }

    @Test
    void create_shouldDelegateToDeviceClient() {
        DeviceBffController controller = new DeviceBffController(deviceClient);
        Map<String, Object> body = Map.of("customerId", 10L, "model", "PlayStation 5");

        when(deviceClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(deviceClient).create(body);
    }
}
