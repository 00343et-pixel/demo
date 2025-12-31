package com.example.demo.practice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "reset password")
public record ResetPasswordRequest(

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters") // 沒有msg會回傳預設"大小必須在 8 和 2147483647 之間"
    @Schema(description = "new password", example = "12345678")
    String newPassword
) {}
