package com.homifybackend.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homifybackend.auth.dto.ErrorResponse;
import com.homifybackend.auth.dto.MessageResponse;
import com.homifybackend.auth.dto.SetPasswordRequest;
import com.homifybackend.auth.dto.UserResponse;
import com.homifybackend.auth.security.CustomUserDetails;
import com.homifybackend.auth.security.JwtService;
import com.homifybackend.auth.service.AuthService;
import com.homifybackend.auth.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    /**
     * Get current authenticated user's CustomUserDetails information
     * @return UserResponse with user information from JWT token
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserDetails() {
        try {
            CustomUserDetails userDetails = userService.getCurrentUserDetails();
            
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            UserResponse response = UserResponse.builder()
                    .userId(userDetails.getUserId())
                    .email(userDetails.getEmail())
                    .username(userDetails.getUsername())
                    .fullName(userDetails.getFullName())
                    .phoneNumber(userDetails.getPhoneNumber())
                    .phone(userDetails.getPhoneNumber())  // For backward compatibility
                    .role(userDetails.getRole() != null ? userDetails.getRole().name() : null)
                    .avatarUrl(userDetails.getAvatarUrl())
                    .dateOfBirth(userDetails.getDateOfBirth())
                    .gender(userDetails.getGender() != null ? userDetails.getGender().name() : null)
                    .registrationDate(userDetails.getRegistrationDate())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    /**
     * Get current authenticated user's ID only
     * @return User ID
     */
    @GetMapping("/me/id")
    public ResponseEntity<?> getCurrentUserId() {
        try {
            Long userId = userService.getCurrentUserId();
            
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            return ResponseEntity.ok(new MessageResponse("User ID: " + userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    /**
     * Get current authenticated user's email
     * @return User email
     */
    @GetMapping("/me/email")
    public ResponseEntity<?> getCurrentUserEmail() {
        try {
            String email = userService.getCurrentUserEmail();
            
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            return ResponseEntity.ok(new MessageResponse("Email: " + email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    /**
     * Get current authenticated user's username
     * @return Username
     */
    @GetMapping("/me/username")
    public ResponseEntity<?> getCurrentUsername() {
        try {
            String username = userService.getCurrentUsername();
            
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            return ResponseEntity.ok(new MessageResponse("Username: " + username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    /**
     * Get current authenticated user's role
     * @return User role
     */
    @GetMapping("/me/role")
    public ResponseEntity<?> getCurrentUserRole() {
        try {
            String role = userService.getCurrentUserRole();
            
            if (role == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            return ResponseEntity.ok(new MessageResponse("Role: " + role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    /**
     * Check if user is authenticated
     * @return Authentication status
     */
    @GetMapping("/authenticated")
    public ResponseEntity<?> checkAuthentication() {
        try {
            boolean isAuthenticated = userService.isAuthenticated();
            return ResponseEntity.ok(new MessageResponse("Authenticated: " + isAuthenticated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(
            @Valid @RequestBody SetPasswordRequest setPasswordRequest,
            HttpServletRequest request) {
        try {
            // Extract username from JWT token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Authorization token required"));
            }

            String jwt = authHeader.substring(7);
            String username = jwtService.extractUsername(jwt);

            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid token"));
            }

            authService.setPassword(username, setPasswordRequest.getPassword());
            return ResponseEntity.ok(new MessageResponse("Password set successfully. You can now login with your password."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }
}

