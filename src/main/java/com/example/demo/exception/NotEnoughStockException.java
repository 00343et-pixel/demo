package com.example.demo.exception;

public class NotEnoughStockException extends RuntimeException {
    
    public NotEnoughStockException() {
        super("Not enough stock");
    }
}
