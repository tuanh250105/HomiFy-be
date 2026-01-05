package com.homifybackend.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    
    // Explicit constructor for String message
    public ErrorResponse(String message) {
        this.message = message;
    }
}

