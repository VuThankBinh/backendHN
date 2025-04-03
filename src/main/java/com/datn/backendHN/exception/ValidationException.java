package com.datn.backendHN.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
} 