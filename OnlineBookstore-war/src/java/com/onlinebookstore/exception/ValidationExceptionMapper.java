package com.onlinebookstore.exception;

import com.onlinebookstore.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;


@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        
        for(ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            
            String message = violation.getMessage();
            
            errors.put(field, message);
        }
        ApiResponse<Map<String, String>> response = new ApiResponse<>(false, "Validation failed", errors);
        
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
    
}
