package com.example.demo.practice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "refresh access token")
public record RefreshRequest(

    @NotBlank
    @Schema(description = "refresh token", example = "db3cf253-ac96-4613...")
    String refreshToken
) {}
