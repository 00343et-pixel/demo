package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RefreshRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.TokenResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;

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

        String accessToken = jwtTokenProvider.createToken(
                user.getEmail(), user.getRole()
        );

        RefreshToken refreshToken = refreshTokenService.create(user);
        return new LoginResponse(accessToken, refreshToken.getToken());
    }

    public TokenResponse refresh(RefreshRequest request) {
        User user = refreshTokenService.verify(request.refreshToken());

        String newAccessToken =
                jwtTokenProvider.createToken(
                        user.getEmail(), user.getRole()
                );
        return new TokenResponse(newAccessToken);
    }

    public void logout(HttpServletRequest request, String email) {
        String token = resolveToken(request);

        if (token == null) {
            throw new UnauthorizedException();
        }

        long ttl = jwtTokenProvider.getRemainingTime(token);

        tokenBlacklistService.blacklist(token, ttl);

        userRepository.findByEmail(email)
                .ifPresent(refreshTokenService::deleteByUser);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return bearer != null && bearer.startsWith("Bearer ")
                ? bearer.substring(7)
                : null;
    }
}
