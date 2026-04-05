package com.example.issuerservice.exception;

public class BusinessConflictException extends RuntimeException {
    public BusinessConflictException(String message) {
        super(message);
    }
}