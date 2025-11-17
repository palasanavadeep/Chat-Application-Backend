package com.navadeep.ChatApplication.exception;

/**
 * Thrown when the request conflicts with existing data (e.g., duplicate).
 */
public class ConflictException extends AppException {
    public ConflictException(String message) { super(message); }
}