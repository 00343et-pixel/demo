package com.example.demo.practice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "update user data")
public record UserUpdateRequest(

    @Schema(description = "user name", example = "Eric")
    String userName,

    @Schema(description = "phone number", example = "0972666666")
    String phone,

    @Schema(description = "address", example = "Taipei")
    String address
) {}
