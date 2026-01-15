package com.example.demo.practice.exception;

public class NotEnoughStockException extends RuntimeException {
    
    public NotEnoughStockException() {
        super("Not enough stock");
    }
}
