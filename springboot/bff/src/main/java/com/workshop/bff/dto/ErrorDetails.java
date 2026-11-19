package com.workshop.bff.dto;

public record ErrorDetails(
        String code,
        String message
) {
}
