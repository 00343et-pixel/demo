package com.example.demo.exception;

public class EmptyCartException extends RuntimeException {
    
    public EmptyCartException() {
        super("Cart is empty.");
    }
}
