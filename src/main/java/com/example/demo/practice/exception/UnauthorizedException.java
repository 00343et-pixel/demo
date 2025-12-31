package com.example.demo.practice.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Invalid email or password.");
    }
}
