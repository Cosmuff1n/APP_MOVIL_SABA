package com.workshop.bff.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Register request body")
public record RegisterRequest(
        @Schema(example = "charlie", defaultValue = "charlie")
        String username,

        @Schema(example = "password123", defaultValue = "password123")
        String password
) {
}
