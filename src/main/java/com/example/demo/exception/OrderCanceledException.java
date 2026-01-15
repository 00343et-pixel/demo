package com.example.demo.exception;

public class OrderCanceledException extends RuntimeException {
    
    public OrderCanceledException() {
        super("Order was canceled.");
    }
}
