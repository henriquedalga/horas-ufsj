package com.universidade.sighoras.exception;

public class ApiExternaException extends RuntimeException {
    public ApiExternaException(String message) {
        super(message);
    }
    
    public ApiExternaException(String message, Throwable cause) {
        super(message, cause);
    }
}