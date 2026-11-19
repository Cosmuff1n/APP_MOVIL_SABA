package com.example.saba.bff.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorDetails error
) {
    public ApiResponse(boolean success, T data) {
        this(success, data, null);
    }

    public ApiResponse(boolean success, ErrorDetails error) {
        this(success, null, error);
    }
}
