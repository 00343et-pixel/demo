package com.example.demo.practice.exception;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException() {
        super("Invalid quantity.");
    }
}
