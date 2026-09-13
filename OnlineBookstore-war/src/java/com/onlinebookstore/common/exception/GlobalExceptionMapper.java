package com.onlinebookstore.common.exception;

import com.onlinebookstore.common.dto.ApiResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global Exception Handling Middleware for Backend (Jakarta REST / JAX-RS).
 * Intercepts all uncaught exceptions across REST resources and EJBs,
 * logging details and returning standardized JSON responses.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // 1. Handle custom business exceptions (AppException & subclasses)
        if (exception instanceof AppException) {
            AppException appException = (AppException) exception;
            LOGGER.log(Level.INFO, "Business exception caught [Status {0}]: {1}",
                    new Object[]{appException.getStatusCode(), appException.getMessage()});

            ApiResponse<Object> apiResponse = new ApiResponse<>(
                    false,
                    appException.getMessage(),
                    appException.getData()
            );

            return Response.status(appException.getStatusCode())
                    .entity(apiResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // 2. Handle Bean Validation exceptions (ConstraintViolationException)
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            Map<String, String> errors = new LinkedHashMap<>();
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                errors.put(field, message);
            }

            LOGGER.log(Level.WARNING, "Validation failed with {0} violations", errors.size());

            ApiResponse<Map<String, String>> response = new ApiResponse<>(false, "Validation failed", errors);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // 3. Handle standard JAX-RS WebApplicationException (e.g. 404 Not Found, 405 Method Not Allowed)
        if (exception instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) exception;
            int status = wae.getResponse().getStatus();
            String message = wae.getMessage() != null ? wae.getMessage() : "HTTP Error " + status;

            LOGGER.log(Level.WARNING, "JAX-RS WebApplicationException caught [Status {0}]: {1}",
                    new Object[]{status, message});

            return Response.status(status)
                    .entity(ApiResponse.failed(message))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // 4. Handle unexpected runtime exceptions & errors (HTTP 500)
        LOGGER.log(Level.SEVERE, "Unhandled backend exception intercepted by GlobalExceptionMapper", exception);

        String errorMessage = "Internal Server Error: " +
                (exception.getMessage() != null && !exception.getMessage().trim().isEmpty()
                        ? exception.getMessage()
                        : exception.getClass().getName());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.failed(errorMessage))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
