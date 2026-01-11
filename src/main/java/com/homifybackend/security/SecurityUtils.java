package com.homifybackend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.homifybackend.auth.security.CustomUserDetails;

/**
 * Utility class for retrieving authenticated user information from SecurityContext.
 * 
 * Best Practices:
 * - NEVER trust userId from request parameters (@RequestParam/@PathVariable)
 * - ALWAYS get userId from JWT token via SecurityContext
 * - This prevents users from accessing other users' data
 * 
 * Usage Example:
 * ```java
 * @GetMapping("/my-profile")
 * public ResponseEntity<?> getProfile() {
 *     Long userId = SecurityUtils.getCurrentUserId();
 *     User user = userService.findById(userId);
 *     return ResponseEntity.ok(user);
 * }
 * ```
 */
public class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Get the current authenticated user's ID from JWT token.
     * 
     * @return User ID (Long) or null if not authenticated
     * @throws IllegalStateException if authentication principal is not CustomUserDetails
     */
    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getUserId() : null;
    }

    /**
     * Get the current authenticated user's email.
     * 
     * @return User email or null if not authenticated
     */
    public static String getCurrentUserEmail() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getEmail() : null;
    }

    /**
     * Get the current authenticated user's username.
     * 
     * @return Username or null if not authenticated
     */
    public static String getCurrentUsername() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getUsername() : null;
    }

    /**
     * Get the current authenticated user's full name.
     * 
     * @return Full name or null if not authenticated
     */
    public static String getCurrentUserFullName() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getFullName() : null;
    }

    /**
     * Get the current authenticated user's role.
     * 
     * @return Role string (e.g., "CUSTOMER", "AGENT") or null if not authenticated
     */
    public static String getCurrentUserRole() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        if (userDetails == null || userDetails.getRole() == null) {
            return null;
        }
        return userDetails.getRole().toString();
    }

    /**
     * Get the current authenticated user's phone number.
     * 
     * @return Phone number or null if not authenticated
     */
    public static String getCurrentUserPhone() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getPhoneNumber() : null;
    }

    /**
     * Check if current user has a specific role.
     * 
     * @param role Role to check (e.g., "CUSTOMER", "AGENT")
     * @return true if user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        String currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equalsIgnoreCase(role);
    }

    /**
     * Check if current user is authenticated.
     * 
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null 
                && authentication.isAuthenticated() 
                && authentication.getPrincipal() instanceof CustomUserDetails;
    }

    /**
     * Get the full CustomUserDetails object.
     * 
     * @return CustomUserDetails or null if not authenticated
     */
    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }
        
        return null;
    }

    /**
     * Get the Authentication object.
     * 
     * @return Authentication or null if not authenticated
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Validate that the requested resource belongs to the current user.
     * Throws exception if user tries to access another user's data.
     * 
     * @param requestedUserId The user ID from path/query parameter
     * @throws SecurityException if requestedUserId doesn't match current user
     */
    public static void validateUserAccess(Long requestedUserId) {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            throw new SecurityException("User not authenticated");
        }
        
        if (!currentUserId.equals(requestedUserId)) {
            throw new SecurityException("Access denied: You can only access your own data");
        }
    }

    /**
     * Validate that current user has required role.
     * 
     * @param requiredRole Required role (e.g., "AGENT", "CUSTOMER")
     * @throws SecurityException if user doesn't have the role
     */
    public static void requireRole(String requiredRole) {
        if (!hasRole(requiredRole)) {
            throw new SecurityException("Access denied: Requires " + requiredRole + " role");
        }
    }
}
