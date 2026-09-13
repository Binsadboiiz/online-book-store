package com.onlinebookstore.common.exception;

/**
 * Base custom runtime exception for backend business errors.
 * Stores HTTP status code, error message, and optional payload details.
 */
public class AppException extends RuntimeException {

    private final int statusCode;
    private final Object data;

    public AppException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.data = null;
    }

    public AppException(int statusCode, String message, Object data) {
        super(message);
        this.statusCode = statusCode;
        this.data = data;
    }

    public AppException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.data = null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getData() {
        return data;
    }
}
