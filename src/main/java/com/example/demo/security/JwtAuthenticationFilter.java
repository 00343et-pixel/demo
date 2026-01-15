package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.TokenBlacklistService;

import io.jsonwebtoken.JwtException;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(
        JwtTokenProvider jwtTokenProvider,
        TokenBlacklistService tokenBlacklistService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);
            
        if (token != null && jwtTokenProvider.validateToken(token)) {

            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new JwtException("Token is revoked.");
            }

            Authentication auth =
                jwtTokenProvider.getAuthentication(token);

            SecurityContextHolder.getContext()
                .setAuthentication(auth);
        }

        filterChain.doFilter(request, response);

    }
    
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer "))
                ? bearer.substring(7)
                : null;
    }
}
