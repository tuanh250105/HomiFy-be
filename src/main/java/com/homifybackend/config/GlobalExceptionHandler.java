package com.homifybackend.config;

import com.homifybackend.agentsforcustomer.exception.AgentNotFoundException;
import com.homifybackend.agentsforcustomer.exception.ReviewNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AgentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAgentNotFoundException(AgentNotFoundException ex) {
        log.error("Agent not found: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "NOT_FOUND");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReviewNotFoundException(ReviewNotFoundException ex) {
        log.error("Review not found: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "NOT_FOUND");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Validation failed");
        errorResponse.put("errors", errors);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "VALIDATION_ERROR");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "BAD_REQUEST");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Internal server error: ", ex);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "An unexpected error occurred");
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "INTERNAL_SERVER_ERROR");

        // Only include detailed error message in development
        // In production, you might want to hide this
        errorResponse.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}