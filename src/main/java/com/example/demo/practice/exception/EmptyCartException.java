package com.example.demo.practice.exception;

public class EmptyCartException extends RuntimeException {
    
    public EmptyCartException() {
        super("Cart is empty.");
    }
}
