package com.onlinebookstore.common.exception;

/**
 * Exception representing HTTP 400 Bad Request error.
 */
public class BadRequestException extends AppException {

    public BadRequestException(String message) {
        super(400, message);
    }

    public BadRequestException(String message, Object data) {
        super(400, message, data);
    }
}
