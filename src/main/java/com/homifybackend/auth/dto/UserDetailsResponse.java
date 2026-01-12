package com.homifybackend.auth.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Response DTO for returning CustomUserDetails information via API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    private Long userId;
    private String email;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String role;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private String gender;
    private LocalDate registrationDate;
}
