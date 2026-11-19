package com.workshop.bff.controller;

import com.workshop.bff.client.AppointmentClient;
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
class AppointmentBffControllerTest {

    @Mock
    private AppointmentClient appointmentClient;

    @Test
    void getAppointmentsByCustomer_shouldDelegateToAppointmentClient() {
        AppointmentBffController controller = new AppointmentBffController(appointmentClient);

        when(appointmentClient.getByCustomer(10L)).thenReturn(ResponseEntity.ok(Map.of("customerId", 10L)));

        ResponseEntity<Object> result = controller.getAppointmentsByCustomer(10L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(appointmentClient).getByCustomer(10L);
    }

    @Test
    void getAppointmentById_shouldDelegateToAppointmentClient() {
        AppointmentBffController controller = new AppointmentBffController(appointmentClient);

        when(appointmentClient.getById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        ResponseEntity<Object> result = controller.getAppointmentById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(appointmentClient).getById(1L);
    }

    @Test
    void create_shouldDelegateToAppointmentClient() {
        AppointmentBffController controller = new AppointmentBffController(appointmentClient);
        Map<String, Object> body = Map.of("customerId", 10L, "appointmentType", "DROP_OFF");

        when(appointmentClient.create(anyMap())).thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> result = controller.create(body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(appointmentClient).create(body);
    }
}
