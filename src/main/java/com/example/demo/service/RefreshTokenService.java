package com.example.demo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.exception.TokenException;
import com.example.demo.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_DAYS = 7;

    private final RefreshTokenRepository repo;

    @Transactional
    public RefreshToken create(User user) {
        
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

    @Transactional
    public void deleteByUser(User user) {

        repo.deleteByUser(user);
    }
}
