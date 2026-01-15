package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "error response")
public record ErrorResponse(

    @Schema(description = "status", example = "400")
    int status,

    @Schema(description = "error", example = "BAD_REQUEST")
    String error,
    
    @Schema(description = "message", example = "Bad request.")
    String message
) {}
