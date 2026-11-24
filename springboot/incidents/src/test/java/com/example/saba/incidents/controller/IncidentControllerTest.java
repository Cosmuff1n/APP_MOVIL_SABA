package com.example.saba.incidents.controller;

import com.example.saba.incidents.dto.IncidentDto;
import com.example.saba.incidents.exception.ApiExceptionHandler;
import com.example.saba.incidents.exception.IncidentNotFoundException;
import com.example.saba.incidents.service.IncidentService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class IncidentControllerTest {
    private IncidentService service;
    private MockMvc mvc;

    @BeforeEach void setup() {
        service = mock(IncidentService.class);
        mvc = MockMvcBuilders.standaloneSetup(new IncidentController(service))
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    private IncidentDto incident() {
        return new IncidentDto(9L, "SABA-12345678", 15L, 3L,
                "Operador", "Barrera", "Falla mecánica", "degradado",
                1_700_000_000_000L, false, null, null, null);
    }

    @Test void listReturnsTheDocumentedFields() throws Exception {
        when(service.list(15L, null)).thenReturn(List.of(incident()));
        mvc.perform(get("/api/incidents?parkingId=15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketCode").value("SABA-12345678"))
                .andExpect(jsonPath("$[0].isResolved").value(false))
                .andExpect(jsonPath("$[0].reportedAt").value(1_700_000_000_000L));
    }

    @Test void createAcceptsBearerAndReturns201() throws Exception {
        when(service.create(any(), eq("Bearer token"))).thenReturn(incident());
        mvc.perform(post("/api/incidents")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"ticketCode":"SABA-12345678","parkingId":15,"serviceId":3,
                         "failureType":"Barrera","description":"Falla mecánica","status":"degradado"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/incidents/9"));
    }

    @Test void invalidRequestReturns400() throws Exception {
        mvc.perform(post("/api/incidents")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"ticketCode":"SABA-12345678","parkingId":0,"serviceId":3,
                         "failureType":"Barrera","description":"Falla"}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test void updateUsesPutAndReturnsJson() throws Exception {
        when(service.update(eq(9L), any())).thenReturn(incident());
        mvc.perform(put("/api/incidents/9")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"failureType":"Barrera","description":"Falla mecánica",
                         "status":"degradado","isResolved":false}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketCode").value("SABA-12345678"));
    }

    @Test void missingIncidentReturns404() throws Exception {
        when(service.getById(999L)).thenThrow(new IncidentNotFoundException(999L));
        mvc.perform(get("/api/incidents/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("INCIDENT_NOT_FOUND"));
    }
}
