package com.example.saba.bff.controller;

import com.example.saba.bff.client.IncidenciaClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidencias")
@Tag(name = "Incidencias BFF", description = "Incidencia routes proxied to the incidencia microservice")
public class IncidenciaBffController {

    private final IncidenciaClient incidenciaClient;

    public IncidenciaBffController(IncidenciaClient incidenciaClient) {
        this.incidenciaClient = incidenciaClient;
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Search incidencia by ID")
    public ResponseEntity<Object> getIncidenciaById(
            @Parameter(example = "1", schema = @Schema(defaultValue = "1")) @PathVariable Long id) {
        return incidenciaClient.getById(id);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Search incidencia by status")
    public ResponseEntity<Object> getStatusIgnoreCase(
            @Parameter(example = "Received", schema = @Schema(defaultValue = "RECEIVED")) @PathVariable String status) {
        return incidenciaClient.getByStatus(status);
    }

    @GetMapping
    @Operation(summary = "Search incidencias by customer ID")
    public ResponseEntity<Object> getIncidenciasByCustomer(
            @Parameter(example = "1", schema = @Schema(defaultValue = "1")) @RequestParam Long customerId) {
        return incidenciaClient.getByCustomer(customerId);
    }

    @PostMapping
    @Operation(summary = "Create incidencia")
    public ResponseEntity<Object> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Incidencia JSON body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
{"customerId":1,"model":"PlayStation 5","serialNumber":"PS5-001","brand":"Sony","reportedIssue":"Does not turn on","status":"Received"}
                    """)
                    )
            )
            @RequestBody Map<String, Object> body) {
        return incidenciaClient.create(body);
    }
}
