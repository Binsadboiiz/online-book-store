package com.onlinebookstore.common.exception;

/**
 * Exception representing HTTP 401 Unauthorized error.
 */
public class UnauthorizedException extends AppException {

    public UnauthorizedException(String message) {
        super(401, message);
    }

    public UnauthorizedException(String message, Object data) {
        super(401, message, data);
    }
}
