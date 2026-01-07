package com.example.demo.practice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.demo.practice.entity.Role;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

        @Value("${jwt.secret}")
        private String secretKey;

        @Value("${jwt.expiration}")
        private long expiration;

        private Key key;

        @PostConstruct
        public void init() {
                this.key = Keys.hmacShaKeyFor(
                        secretKey.getBytes(StandardCharsets.UTF_8)
                );
        }

        public String createToken(String email, Role role) {
                Claims claims = Jwts.claims().setSubject(email);
                claims.put("role", role.name());

                Date now = new Date();
                Date expiry = new Date(now.getTime() + expiration);

                return Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(expiry)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
        }
        
        public boolean validateToken(String token) {
                try {
                        parseClaims(token);
                        return true; 
                } catch (ExpiredJwtException e) {
                        return false;
                } catch (JwtException e) {
                        return false;
                }
        }

        public Authentication getAuthentication(String token) {
                Claims claims = parseClaims(token);

                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + role));

                return new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities
                );
        }

        public long getRemainingTime(String token) {
                Claims claims = parseClaims(token);
                return claims.getExpiration().getTime()
                        - System.currentTimeMillis();
        }

        private Claims parseClaims(String token) {
                return Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
        }
}
