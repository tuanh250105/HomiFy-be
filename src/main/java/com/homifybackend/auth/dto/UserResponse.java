package com.homifybackend.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Unified response DTO for user information
 * Used for both authentication (login/register) and user details endpoints
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    // Basic user information
    private Long userId;
    private String email;
    private String username;
    private String fullName;
    private String phone;
    private String phoneNumber;  // Alternative field name for compatibility
    private String role;
    
    // Extended user details (from CustomUserDetails)
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private String gender;
    private LocalDate registrationDate;
    
    // Internal use only - not exposed to client (for authentication responses)
    @JsonIgnore
    private String accessToken;
    
    @JsonIgnore
    private String refreshToken;
}

