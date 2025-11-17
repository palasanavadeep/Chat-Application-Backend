package com.navadeep.ChatApplication.exception;


/**
 * Thrown for unexpected system or server errors.
 */
public class InternalServerException extends AppException {
    public InternalServerException(String message) { super(message); }
    public InternalServerException(String message, Throwable cause) { super(message, cause); }
}