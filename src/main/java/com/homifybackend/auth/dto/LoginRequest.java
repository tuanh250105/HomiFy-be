package com.homifybackend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private Boolean rememberMe = false;

    /**
     * Only used by role-specific login endpoints (e.g. /login/customer, /login/agent).
     * Values: CUSTOMER / AGENT
     */
    private String expectedRole;
}
