package com.example.demo.practice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "create user")
public record UserCreateRequest(

    @NotBlank
    @Schema(description = "user name", example = "GuoHong")
    String username,

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "password", example = "12345678")
    String password,

    @Email
    @NotBlank
    @Schema(description = "email", example = "guohong@example.com")
    String email,

    @NotBlank
    @Schema(description = "phone", example = "0972777777")
    String phone,

    @NotBlank
    @Schema(description = "address", example = "Taichung")
    String address
) {}