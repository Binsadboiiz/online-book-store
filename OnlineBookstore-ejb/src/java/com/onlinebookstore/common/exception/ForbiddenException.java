package com.onlinebookstore.common.exception;

/**
 * Exception representing HTTP 403 Forbidden error.
 */
public class ForbiddenException extends AppException {

    public ForbiddenException(String message) {
        super(403, message);
    }

    public ForbiddenException(String message, Object data) {
        super(403, message, data);
    }
}
