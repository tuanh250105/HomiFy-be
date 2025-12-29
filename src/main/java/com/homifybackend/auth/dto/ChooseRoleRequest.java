package com.homifybackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChooseRoleRequest {
    @NotBlank(message = "Role is required")
    private String role; // "customer" or "agent"
}

