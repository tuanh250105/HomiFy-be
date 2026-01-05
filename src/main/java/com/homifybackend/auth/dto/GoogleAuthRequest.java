package com.homifybackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleAuthRequest {
    @NotBlank(message = "ID token is required")
    private String idToken;
    
    // Explicit getter (in addition to @Data generated getter)
    public String getIdToken() {
        return idToken;
    }
}

