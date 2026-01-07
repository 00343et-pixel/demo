package com.example.demo.practice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "login request data")
public record LoginRequest(

    @NotBlank
    @Schema(description = "user email", example = "guohong@test.com")
    String email,

    @NotBlank
    @Schema(description = "user password", example = "12345678")
    String password
) {}
