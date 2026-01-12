package com.homifybackend.controller;

import com.homifybackend.dto.AccountMeResponse;
import com.homifybackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User Controller - Provides APIs to return user information based on JWT authentication
 * Uses SecurityContextHolder to get current authenticated user
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get current authenticated user information from JWT token
     * 
     * @return AccountMeResponse with current user details
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        try {
            log.info("GET /api/user/current - Fetching current authenticated user");
            
            if (!userService.isAuthenticated()) {
                log.warn("User is not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "message", "User is not authenticated"
                        ));
            }
            
            AccountMeResponse user = userService.getCurrentUser();
            log.debug("Current user found: {}", user.getUsername());
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Current user retrieved successfully",
                    "data", user
            ));
        } catch (Exception e) {
            log.error("Error fetching current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching current user: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get current user's username only
     * 
     * @return Map with username
     */
    @GetMapping("/current/username")
    public ResponseEntity<Map<String, Object>> getCurrentUsername() {
        try {
            log.info("GET /api/user/current/username - Fetching current username");
            
            if (!userService.isAuthenticated()) {
                log.warn("User is not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "message", "User is not authenticated"
                        ));
            }
            
            String username = userService.getCurrentUsername();
            log.debug("Current username: {}", username);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Username retrieved successfully",
                    "username", username
            ));
        } catch (Exception e) {
            log.error("Error fetching current username", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching username: " + e.getMessage()
                    ));
        }
    }

    /**
     * Check if user is authenticated
     * 
     * @return Map with authentication status
     */
    @GetMapping("/authenticated")
    public ResponseEntity<Map<String, Object>> checkAuthentication() {
        try {
            log.info("GET /api/user/authenticated - Checking authentication status");
            
            boolean isAuthenticated = userService.isAuthenticated();
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "authenticated", isAuthenticated,
                    "message", isAuthenticated ? "User is authenticated" : "User is not authenticated"
            ));
        } catch (Exception e) {
            log.error("Error checking authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error checking authentication: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get user information by userId (Admin only)
     * 
     * @param userId User ID
     * @return AccountMeResponse with user details
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        try {
            log.info("GET /api/user/{} - Fetching user by ID", userId);
            
            AccountMeResponse user = userService.getUserById(userId);
            log.debug("User found: {}", user.getUsername());
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User retrieved successfully",
                    "data", user
            ));
        } catch (Exception e) {
            log.error("Error fetching user with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching user: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get current user profile (alias for /current)
     * 
     * @return AccountMeResponse with current user details
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        try {
            log.info("GET /api/user/profile - Fetching user profile");
            
            if (!userService.isAuthenticated()) {
                log.warn("User is not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "message", "User is not authenticated"
                        ));
            }
            
            AccountMeResponse user = userService.getCurrentUser();
            log.debug("User profile found: {}", user.getUsername());
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User profile retrieved successfully",
                    "data", user
            ));
        } catch (Exception e) {
            log.error("Error fetching user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching user profile: " + e.getMessage()
                    ));
        }
    }
}
