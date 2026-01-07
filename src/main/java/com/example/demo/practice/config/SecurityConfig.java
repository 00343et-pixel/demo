package com.example.demo.practice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.practice.security.JwtAuthenticationFilter;
import com.example.demo.practice.security.handler.JwtAccessDeniedHandler;
import com.example.demo.practice.security.handler.JwtAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig( 
        JwtAccessDeniedHandler jwtAccessDeniedHandler, 
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, 
        JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/auth/login",
                    "/auth/refresh",
                    "/auth/register",
                    "/products/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll()

                .requestMatchers(
                    "/auth/logout",
                    "/auth/me/password",
                    "/users/**"
                ).authenticated()

                .requestMatchers(
                    "/admin/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    "/cart/**",
                    "/orders/**"
                ).hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
            )
            
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
