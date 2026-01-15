package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "update user data")
public record UserUpdateRequest(

    @NotBlank
    @Schema(description = "user name", example = "Eric")
    String userName,

    @NotBlank
    @Schema(description = "phone number", example = "0972666666")
    String phone,

    @NotBlank
    @Schema(description = "address", example = "Taipei")
    String address
) {}
