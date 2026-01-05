package com.homifybackend.accountSetting_myListings.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFound(NotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of(
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> other(Exception ex) {
        return ResponseEntity.status(500).body(Map.of(
                "message", "Internal error",
                "detail", ex.getMessage()
        ));
    }
}
