package com.example.demo.practice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.practice.dto.request.UserUpdateRequest;
import com.example.demo.practice.dto.response.ErrorResponse;
import com.example.demo.practice.dto.response.UserResponse;
import com.example.demo.practice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "User", description = "使用者相關 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    
    @Operation(
        summary = "取得當前使用者資料"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功取得使用者資料"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
        Authentication authentication
    ) {
        return ResponseEntity.ok(userService.getProfileByEmail(authentication.getName()));
    }

    @Operation(
        summary = "更新使用者資料",
        description = "修改name、phone跟address"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功更新使用者資料"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponse> putMe(
        Authentication authentication,
        @RequestBody @Valid UserUpdateRequest updateRequest
    ) {
        return ResponseEntity.ok(userService.updateData(authentication.getName(), updateRequest));
    }

}
