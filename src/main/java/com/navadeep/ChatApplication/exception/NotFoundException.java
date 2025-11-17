package com.navadeep.ChatApplication.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class NotFoundException extends AppException {
    public NotFoundException(String message) { super(message); }
}