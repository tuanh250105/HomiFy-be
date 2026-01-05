package com.homifybackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
    // Explicit getter (in addition to @Data generated getter)
    public String getRefreshToken() {
        return refreshToken;
    }
}

