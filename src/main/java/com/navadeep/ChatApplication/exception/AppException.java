package com.navadeep.ChatApplication.exception;

/**
 * Base class for all custom application exceptions.
 */
public class AppException extends RuntimeException {
    public AppException(String message) { super(message); }
    public AppException(String message, Throwable cause) { super(message, cause); }
}