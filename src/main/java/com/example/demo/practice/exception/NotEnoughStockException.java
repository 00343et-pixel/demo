package com.example.demo.practice.exception;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException() {
        super("庫存不夠");
    }
}
