package com.example.demo.practice.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.practice.entity.RefreshToken;
import com.example.demo.practice.entity.User;
import com.example.demo.practice.exception.TokenException;
import com.example.demo.practice.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_DAYS = 7;

    private final RefreshTokenRepository repo;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    @Transactional // save / delete / remove 一定要在 Transaction 裡 (重要!!!)
    public RefreshToken create(User user) {
        // 同一個使用者只留一顆
        repo.deleteByUser(user);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(
                Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS)
        );

        return repo.save(token);
    }

    public User verify(String token) {
        RefreshToken refreshToken = repo.findByToken(token)
                .orElseThrow(TokenException::new);

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(refreshToken);
            throw new TokenException();
        }

        return refreshToken.getUser();
    }

    @Transactional // save / delete / remove 一定要在 Transaction 裡 (重要!!!)
    public void deleteByUser(User user) {
        repo.deleteByUser(user);
    }

}
