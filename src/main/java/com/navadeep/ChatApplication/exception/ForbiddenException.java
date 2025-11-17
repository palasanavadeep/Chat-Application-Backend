package com.navadeep.ChatApplication.exception;

/**
 * Thrown when user lacks permission for an action.
 */
public class ForbiddenException extends AppException {
    public ForbiddenException(String message) { super(message); }
}