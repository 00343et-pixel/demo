package com.example.demo.practice.exception;

public class TokenException extends RuntimeException {
    public TokenException() {
        super("Invalid or expired token.");
    }
}
