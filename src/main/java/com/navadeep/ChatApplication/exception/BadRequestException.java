package com.navadeep.ChatApplication.exception;



/**
 * Exception thrown when a bad request is made by the client.
 */
public class BadRequestException extends AppException {
    public BadRequestException(String message) { super(message); }
}