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
            // 關閉 CSRF（API 專用）
            .csrf(csrf -> csrf.disable())

            // 不使用 Session
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )

            // 權限規則
            .authorizeHttpRequests(auth -> auth

                // 放行登入
                .requestMatchers(
                    "/auth/login",
                    "/auth/refresh",
                    "/auth/register",
                    "/products/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll()

                // 需要登入
                .requestMatchers(
                    "/auth/logout",
                    "/auth/me/password",
                    "/users/**"
                ).authenticated()

                // ADMIN 專用
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // USER 、 ADMIN 、 SUPPORT
                .requestMatchers(
                    "/cart/**",
                    "/orders/**"
                ).hasAnyRole("USER", "ADMIN")

                // 其他都要登入
                .anyRequest().authenticated()
            )
            
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );


        return http.build();
    }
}
