package com.example.demo.practice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.practice.dto.request.LoginRequest;
import com.example.demo.practice.dto.request.RefreshRequest;
import com.example.demo.practice.dto.request.ResetPasswordRequest;
import com.example.demo.practice.dto.request.UserCreateRequest;
import com.example.demo.practice.dto.response.ErrorResponse;
import com.example.demo.practice.dto.response.LoginResponse;
import com.example.demo.practice.dto.response.TokenResponse;
import com.example.demo.practice.dto.response.UserResponse;
import com.example.demo.practice.service.AuthService;
import com.example.demo.practice.service.UserService;

@Slf4j
@Tag(name = "Auth", description = "登入與註冊相關 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

        private final UserService userService;
        private final AuthService authService;
        
        @Operation(
                summary = "登入",
                description = "輸入信箱跟密碼"
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "登入成功"
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "信箱或密碼錯誤",
                        content = @Content(
                                schema = @Schema(implementation = ErrorResponse.class)
                ))
        })
        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(
                @RequestBody @Valid LoginRequest request
        ) {
                log.info("user login, email={}", request.email());
                return ResponseEntity.ok(authService.login(request));
        }

        @Operation(
                summary = "刷新Access Token",
                description = "輸入Refresh Token"
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200",
                        description = "refresh成功"
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "輸入錯誤",
                        content = @Content(
                                schema = @Schema(implementation = ErrorResponse.class)
                ))
        })
        @PostMapping("/refresh")
        public ResponseEntity<TokenResponse> refresh(
                @RequestBody @Valid RefreshRequest request
        ) {
                log.info("refresh token request");
                return ResponseEntity.ok(authService.refresh(request));
        }

        @Operation(
                summary = "登出"
        )
        @ApiResponses({
                @ApiResponse(responseCode = "204", description = "登出成功"),
                @ApiResponse(
                        responseCode = "401",
                        description = "輸入錯誤",
                        content = @Content(
                                schema = @Schema(implementation = ErrorResponse.class)
                ))
        })
        @PostMapping("/logout")
        public ResponseEntity<Void> logout(
                HttpServletRequest request,
                Authentication authentication
        ) {
                String email = authentication.getName();
                log.info("user logout, email={}", email);
                authService.logout(request, email);
                return ResponseEntity.noContent().build();
        }

        @Operation(
                summary = "註冊",
                description = "輸入name、email、phone、address跟password"
        )
        @ApiResponses({
                @ApiResponse(responseCode = "201",
                        description = "註冊成功"
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "資料錯誤",
                        content = @Content(
                                schema = @Schema(implementation = ErrorResponse.class)
                ))
        })
        @PostMapping("/register")
        public ResponseEntity<UserResponse> registerUser(
                @RequestBody @Valid UserCreateRequest request
        ) {
                log.info("create user, email={}", request.email());
                return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
        }

        @Operation(
                summary = "重設密碼",
                description = "輸入新密碼"
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "密碼重設成功"),
                @ApiResponse(
                        responseCode = "400",
                        description = "密碼格式錯誤",
                        content = @Content(
                                schema = @Schema(implementation = ErrorResponse.class)
                ))
        })
        @PutMapping("/me/password")
        public ResponseEntity<String> resetPassword(
                Authentication authentication,
                @RequestBody @Valid ResetPasswordRequest request
        ) {
                String email = authentication.getName();
                log.info("user reset password, email={}", email);
                userService.resetPassword(email, request.newPassword());
                return ResponseEntity.ok("密碼更新成功!");
        }
}
