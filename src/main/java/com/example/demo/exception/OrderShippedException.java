package com.example.demo.exception;

public class OrderShippedException extends RuntimeException {
    
    public OrderShippedException() {
        super("Order was shipped.");
    }
}
