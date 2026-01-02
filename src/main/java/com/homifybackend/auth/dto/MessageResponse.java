package com.homifybackend.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageResponse {
    private String message;
    
    // Explicit constructor for String message
    public MessageResponse(String message) {
        this.message = message;
    }
}

