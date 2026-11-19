package com.workshop.bff.controller;

import com.workshop.bff.client.CustomerClient;
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
@RequestMapping("/api/customers")
@Tag(name = "Customers BFF", description = "Customer routes proxied to the customer microservice")
public class CustomerBffController {

    private final CustomerClient customerClient;

    public CustomerBffController(CustomerClient customerClient) {
        this.customerClient = customerClient;
    }

    @GetMapping("/all")
    @Operation(summary = "List all customers")
    public ResponseEntity<Object> listAll() {
        return customerClient.getAll();
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Search customer by ID")
    public ResponseEntity<Object> getCustomerById(
            @Parameter(example = "1", schema = @Schema(defaultValue = "1")) @PathVariable Long id) {
        return customerClient.getById(id);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Search customer by email")
    public ResponseEntity<Object> getByEmail(
            @Parameter(example = "charlie@test.com", schema = @Schema(defaultValue = "charlie@test.com")) @PathVariable String email) {
        return customerClient.getByEmail(email);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Search customer by name")
    public ResponseEntity<Object> getByNameContainingIgnoreCase(
            @Parameter(example = "Charlie", schema = @Schema(defaultValue = "Charlie")) @PathVariable String name) {
        return customerClient.getByName(name);
    }

    @PostMapping
    @Operation(summary = "Register customer")
    public ResponseEntity<Object> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer JSON body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
{"name":"Charlie","lastName":"Donoso","phone":"+56912345678","email":"charlie@test.com","address":"Santiago"}
                    """)
                    )
            )
            @RequestBody Map<String, Object> body) {
        return customerClient.create(body);
    }
}
