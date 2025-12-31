package com.example.demo.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PasswordEncoderTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void encode_and_match_should_work() {
        String rawPassword = "1234";

        String encoded = passwordEncoder.encode(rawPassword);

        System.out.println("Encoded password = " + encoded);

        assertThat(passwordEncoder.matches(rawPassword, encoded)).isTrue();
    }
}
