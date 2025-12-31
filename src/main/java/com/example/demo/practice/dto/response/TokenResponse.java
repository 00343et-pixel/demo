package com.example.demo.practice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "refresh to get new token")
public record TokenResponse(
    
    @Schema(description = "JWT Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    String accessToken
) {}