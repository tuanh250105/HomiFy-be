package com.homifybackend.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String email;
    private String username;
    private String fullName;
    private String phone;
    private String role;
    
    // Internal use only - not exposed to client
    @JsonIgnore
    private String accessToken;
    
    @JsonIgnore
    private String refreshToken;
}

