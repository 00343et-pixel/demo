package com.example.demo.dto.response;

import java.time.ZoneId;

import com.example.demo.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "user imformation")
public record UserResponse(
    
    @Schema(description = "user ID", example = "1")
    Long userId,

    @Schema(description = "user name", example = "GuoHong")
    String userName,

    @Schema(description = "email", example = "guohong@example.com")
    String email,

    @Schema(description = "phone number", example = "0972777777")
    String phone,

    @Schema(description = "address", example = "Taichung")
    String address,

    @Schema(description = "updated at", example = "2026-01-09T11:26:18.426728+08:00[Asia/Taipei]")
    String updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getAddress(),
            user.getUpdatedAt().atZone(ZoneId.of("Asia/Taipei")).toString()
        );
    }
}
