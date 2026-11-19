package com.example.saba.bff.controller;

import com.example.saba.bff.client.ParkingClient;
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
@RequestMapping("/api/parkings")
@Tag(name = "Parkings BFF", description = "Parking routes proxied to the parking microservice")
public class ParkingBffController {

    private final ParkingClient parkingClient;

    public ParkingBffController(ParkingClient parkingClient) {
        this.parkingClient = parkingClient;
    }

    @GetMapping("/all")
    @Operation(summary = "List all parkings")
    public ResponseEntity<Object> listAll() {
        return parkingClient.getAll();
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Search parking by ID")
    public ResponseEntity<Object> getParkingById(
            @Parameter(example = "1", schema = @Schema(defaultValue = "1")) @PathVariable Long id) {
        return parkingClient.getById(id);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Search parking by email")
    public ResponseEntity<Object> getByEmail(
            @Parameter(example = "charlie@test.com", schema = @Schema(defaultValue = "charlie@test.com")) @PathVariable String email) {
        return parkingClient.getByEmail(email);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Search parking by name")
    public ResponseEntity<Object> getByNameContainingIgnoreCase(
            @Parameter(example = "Charlie", schema = @Schema(defaultValue = "Charlie")) @PathVariable String name) {
        return parkingClient.getByName(name);
    }

    @PostMapping
    @Operation(summary = "Register parking")
    public ResponseEntity<Object> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Parking JSON body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
{"name":"Charlie","lastName":"Donoso","phone":"+56912345678","email":"charlie@test.com","address":"Santiago"}
                    """)
                    )
            )
            @RequestBody Map<String, Object> body) {
        return parkingClient.create(body);
    }
}
