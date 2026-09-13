package com.onlinebookstore.common.exception;

/**
 * Exception representing HTTP 404 Resource Not Found error.
 */
public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String message) {
        super(404, message);
    }

    public ResourceNotFoundException(String message, Object data) {
        super(404, message, data);
    }
}
