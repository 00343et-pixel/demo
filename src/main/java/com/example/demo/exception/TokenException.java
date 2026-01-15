package com.example.demo.exception;

public class TokenException extends RuntimeException {
    
    public TokenException() {
        super("Invalid or expired token.");
    }
}
