package com.homifybackend.auth.service;

import org.springframework.stereotype.Service;

import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.auth.security.CustomUserDetails;
import com.homifybackend.model.User;
import com.homifybackend.security.SecurityUtils;

@Service
public class UserService {
    private UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Get current authenticated user's CustomUserDetails from SecurityContext
     * @return CustomUserDetails or null if not authenticated
     */
    public CustomUserDetails getCurrentUserDetails() {
        return SecurityUtils.getCurrentUserDetails();
    }

    /**
     * Get current authenticated user's ID
     * @return User ID or null if not authenticated
     */
    public Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    /**
     * Get current authenticated user's email
     * @return User email or null if not authenticated
     */
    public String getCurrentUserEmail() {
        return SecurityUtils.getCurrentUserEmail();
    }

    /**
     * Get current authenticated user's username
     * @return Username or null if not authenticated
     */
    public String getCurrentUsername() {
        return SecurityUtils.getCurrentUsername();
    }

    /**
     * Get current authenticated user's full name
     * @return Full name or null if not authenticated
     */
    public String getCurrentUserFullName() {
        return SecurityUtils.getCurrentUserFullName();
    }

    /**
     * Get current authenticated user's role
     * @return Role or null if not authenticated
     */
    public String getCurrentUserRole() {
        return SecurityUtils.getCurrentUserRole();
    }

    /**
     * Check if user is authenticated
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return SecurityUtils.isAuthenticated();
    }
}
