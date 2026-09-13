package com.onlinebookstore.common.exception;

/**
 * Exception representing HTTP 409 Conflict error.
 */
public class ConflictException extends AppException {

    public ConflictException(String message) {
        super(409, message);
    }

    public ConflictException(String message, Object data) {
        super(409, message, data);
    }
}
