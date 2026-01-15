package com.example.demo.exception;

public class SamePasswordException extends RuntimeException {
    
    public SamePasswordException() {
        super("New password cannot be the same as the old password.");
    }
}
