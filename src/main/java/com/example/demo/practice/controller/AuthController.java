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


@Tag(name = "Auth", description = "登入與註冊相關 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

        private final UserService userService;
        private final AuthService authService;
        
        @Operation(
                summary = "登入，回傳 JWT",
                description = "輸入email跟password"
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "登入成功",
                        content = @Content(
                                schema = @Schema(implementation = LoginResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "信箱或密碼錯誤",
                        content = @Content(
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                )
        })
        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(
                @RequestBody @Valid LoginRequest request
        ) {
                return ResponseEntity.ok(authService.login(request));
        }

        @Operation(
                summary = "刷新AT",
                description = "輸入RT"
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200",
                        description = "refresh成功",
                        content = @Content(
                                schema = @Schema(implementation = TokenResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "RT錯誤",
                        content = @Content(
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                )
        })
        @PostMapping("/refresh")
        public ResponseEntity<TokenResponse> refresh(
                @RequestBody @Valid RefreshRequest request
        ) {
                return ResponseEntity.ok(authService.refresh(request));
        }

        @Operation(
                summary = "登出（加入 blacklist）"
        )
        @PostMapping("/logout")
        public ResponseEntity<String> logout(
                HttpServletRequest request,
                Authentication authentication
        ) {

                authService.logout(request, authentication);

                return ResponseEntity.noContent().build();
        }

        @Operation(
                summary = "註冊",
                description = "輸入name、email、phone、address跟password"
        )
        @PostMapping("/register") // @Valid = 告訴 Spring：「請幫我檢查這個物件上所有的驗證註解，不合法就不要進到方法裡」
        public ResponseEntity<UserResponse> registerUser(
                @RequestBody @Valid UserCreateRequest request
        ) {
                return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
        }

        @Operation(
                summary = "重設密碼",
                description = "輸入newPassword"
        )
        @PutMapping("/me/password")
        public ResponseEntity<String> resetPassword(
                Authentication authentication,
                @RequestBody @Valid ResetPasswordRequest request
        ) {
                userService.resetPassword(
                        authentication.getName(),
                        request.newPassword()
                );
                return ResponseEntity.ok("密碼更新成功!");
        }
}
