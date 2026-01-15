package com.example.demo.exception;

public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException() {
        super("Invalid email or password.");
    }
}
