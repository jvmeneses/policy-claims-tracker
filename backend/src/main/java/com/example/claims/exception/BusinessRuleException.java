package com.example.claims.exception;

/** Thrown when a business rule is violated. Mapped to HTTP 409. */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}
