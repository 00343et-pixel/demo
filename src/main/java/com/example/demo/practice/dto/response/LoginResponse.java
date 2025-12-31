package com.example.demo.practice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "access token and refresh token")
public record LoginResponse(

    @Schema(description = "access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    String accessToken,
    
    @Schema(description = "refresh Token", example = "db3cf253-ac96-4613...")
    String refreshToken
) {}
