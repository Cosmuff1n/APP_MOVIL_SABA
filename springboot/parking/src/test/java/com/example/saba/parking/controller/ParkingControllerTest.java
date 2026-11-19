package com.example.saba.parking.controller;

import com.example.saba.parking.dto.ParkingDto;
import com.example.saba.parking.exception.ApiExceptionHandler;
import com.example.saba.parking.exception.ParkingNotFoundException;
import com.example.saba.parking.service.ParkingService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ParkingControllerTest {
    ParkingService service = mock(ParkingService.class);
    MockMvc mvc;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new ParkingController(service))
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test void listReturnsJsonCompatibleWithAndroid() throws Exception {
        when(service.list()).thenReturn(List.of(new ParkingDto(15L, "Centro", "Calle 1",
                "Santiago", 120, 42, true, 2L, 1795000000000L, "Activo")));
        mvc.perform(get("/api/parkings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].revision").value(2))
                .andExpect(jsonPath("$[0].updatedAt").value(1795000000000L))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test void createReturns201() throws Exception {
        when(service.create(any())).thenReturn(new ParkingDto(1L, "A", "", "", 0, 0, true,
                0L, 1000L, "Activo"));
        mvc.perform(post("/api/parkings").contentType(MediaType.APPLICATION_JSON)
                .content("{"name":"A"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/parkings/1"));
    }

    @Test void patchUsesPatchNotPut() throws Exception {
        when(service.patch(eq(1L), any())).thenReturn(new ParkingDto(1L, "Nuevo", "", "",
                0, 0, true, 1L, 1000L, "Activo"));
        mvc.perform(patch("/api/parkings/1").contentType(MediaType.APPLICATION_JSON)
                .content("{"revision":0,"name":"Nuevo"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Nuevo"));
    }

    @Test void deleteUsesAndroidIfMatchHeader() throws Exception {
        mvc.perform(delete("/api/parkings/1").header("If-Match", "2"))
                .andExpect(status().isNoContent());
        verify(service).delete(1L, 2L);
    }

    @Test void missingParkingReturns404() throws Exception {
        when(service.getById(99L)).thenThrow(new ParkingNotFoundException(99L));
        mvc.perform(get("/api/parkings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PARKING_NOT_FOUND"));
    }

    @Test void missingRevisionHeaderReturns400() throws Exception {
        mvc.perform(delete("/api/parkings/1"))
                .andExpect(status().isBadRequest());
    }
}
