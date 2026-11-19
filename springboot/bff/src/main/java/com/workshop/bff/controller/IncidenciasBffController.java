package com.workshop.bff.controller;

import com.workshop.bff.client.TicketClient;
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
@RequestMapping("/api/tickets")
@Tag(name = "Tickets BFF", description = "Ticket routes proxied to the ticket microservice")
public class TicketBffController {

    private final TicketClient ticketClient;

    public TicketBffController(TicketClient ticketClient) {
        this.ticketClient = ticketClient;
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Search ticket by ID")
    public ResponseEntity<Object> getTicketById(
            @Parameter(example = "1", schema = @Schema(defaultValue = "1")) @PathVariable Long id) {
        return ticketClient.getById(id);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Search ticket by status")
    public ResponseEntity<Object> getStatusIgnoreCase(
            @Parameter(example = "Received", schema = @Schema(defaultValue = "RECEIVED")) @PathVariable String status) {
        return ticketClient.getByStatus(status);
    }

    @GetMapping
    @Operation(summary = "Search tickets by customer ID")
    public ResponseEntity<Object> getTicketsByCustomer(
            @Parameter(example = "1", schema = @Schema(defaultValue = "1")) @RequestParam Long customerId) {
        return ticketClient.getByCustomer(customerId);
    }

    @PostMapping
    @Operation(summary = "Create ticket")
    public ResponseEntity<Object> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Ticket JSON body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
{"customerId":1,"model":"PlayStation 5","serialNumber":"PS5-001","brand":"Sony","reportedIssue":"Does not turn on","status":"Received"}
                    """)
                    )
            )
            @RequestBody Map<String, Object> body) {
        return ticketClient.create(body);
    }
}
