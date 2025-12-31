package com.example.demo.practice.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.practice.dto.request.LoginRequest;
import com.example.demo.practice.dto.request.RefreshRequest;
import com.example.demo.practice.dto.response.LoginResponse;
import com.example.demo.practice.dto.response.TokenResponse;
import com.example.demo.practice.entity.RefreshToken;
import com.example.demo.practice.entity.User;
import com.example.demo.practice.exception.UnauthorizedException;
import com.example.demo.practice.repository.UserRepository;
import com.example.demo.practice.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(u -> passwordEncoder.matches(
                        request.password(), u.getPassword()
                ))
                .orElseThrow(UnauthorizedException::new);

        String accessToken = jwtTokenProvider.createAccessToken(
                user.getEmail(), user.getRole()
        );

        RefreshToken refreshToken = refreshTokenService.create(user);
        return new LoginResponse(accessToken, refreshToken.getToken());
    }

    // 之後補 rotation
    public TokenResponse refresh(RefreshRequest request) {
        User user = refreshTokenService.verify(request.refreshToken());

        String newAccessToken =
                jwtTokenProvider.createAccessToken(
                        user.getEmail(), user.getRole()
                );
        return new TokenResponse(newAccessToken);
    }

    public void logout(HttpServletRequest request, Authentication authentication) {
        String token = resolveToken(request);

        if (token == null) {
            throw new UnauthorizedException();
        }

        long ttl = jwtTokenProvider.getRemainingTime(token);

        tokenBlacklistService.blacklist(token, ttl);

        userRepository.findByEmail(authentication.getName())
                .ifPresent(refreshTokenService::deleteByUser);
    }

    // 把request轉成token
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return bearer != null && bearer.startsWith("Bearer ")
                ? bearer.substring(7)
                : null;
    }
}
