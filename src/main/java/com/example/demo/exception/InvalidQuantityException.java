package com.example.demo.exception;

public class InvalidQuantityException extends RuntimeException {
    
    public InvalidQuantityException() {
        super("Invalid quantity.");
    }
}
