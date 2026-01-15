package com.example.demo.practice.exception;

public class OrderCanceledException extends RuntimeException {
    
    public OrderCanceledException() {
        super("Order was canceled.");
    }
}
