package com.navadeep.ChatApplication.exception;

/**
 * Thrown when authentication fails (invalid or missing token).
 */
public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) { super(message); }
}