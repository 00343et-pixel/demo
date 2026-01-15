package com.example.demo.practice.exception;

public class OrderShippedException extends RuntimeException {
    
    public OrderShippedException() {
        super("Order was shipped.");
    }
}
