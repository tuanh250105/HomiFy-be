package com.homifybackend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.homifybackend.auth.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Build a user-friendly error message
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        errors.forEach((field, message) -> {
            errorMessage.append(field).append(" - ").append(message).append("; ");
        });
        
        // Log for debugging
        System.err.println("=== Validation Error ===");
        System.err.println("Error message: " + errorMessage.toString());
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage.toString().trim()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        System.err.println("=== Data Integrity Violation ===");
        System.err.println("Error: " + ex.getMessage());
        ex.printStackTrace();
        
        // Extract user-friendly message from the exception
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("email") || message.contains("EMAIL")) {
                message = "Email already registered";
            } else if (message.contains("username") || message.contains("USERNAME")) {
                message = "Username already taken";
            } else if (message.contains("unique constraint") || message.contains("duplicate key")) {
                message = "A record with this information already exists";
            } else {
                message = "Database constraint violation: " + message;
            }
        } else {
            message = "Database constraint violation occurred";
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.err.println("=== Illegal Argument Exception ===");
        System.err.println("Error: " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Log the full exception for debugging
        System.err.println("Unhandled exception: " + ex.getMessage());
        System.err.println("Exception class: " + ex.getClass().getName());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred: " + ex.getMessage()));
    }
}

