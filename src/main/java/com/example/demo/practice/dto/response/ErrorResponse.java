package com.example.demo.practice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "error response")
public record ErrorResponse(

    @Schema(description = "status", example = "404")
    int status,

    @Schema(description = "error", example = "Not Found")
    String error,
    
    @Schema(description = "message", example = "user not exists")
    String message
) {}
