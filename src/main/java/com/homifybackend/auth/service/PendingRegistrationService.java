package com.homifybackend.auth.service;

import com.homifybackend.auth.dto.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to temporarily store pending registration data.
 * Registration data is stored until OTP verification is completed.
 */
@Service
public class PendingRegistrationService {

    // In-memory storage for pending registrations (keyed by email)
    private final Map<String, RegisterRequest> pendingRegistrations = new ConcurrentHashMap<>();

    /**
     * Store registration data temporarily until OTP verification
     */
    public void storePendingRegistration(String email, RegisterRequest registerRequest) {
        String normalizedEmail = email.trim().toLowerCase();
        pendingRegistrations.put(normalizedEmail, registerRequest);
    }

    /**
     * Retrieve and remove pending registration data
     */
    public RegisterRequest getAndRemovePendingRegistration(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        return pendingRegistrations.remove(normalizedEmail);
    }

    /**
     * Check if there's a pending registration for the email
     */
    public boolean hasPendingRegistration(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        return pendingRegistrations.containsKey(normalizedEmail);
    }

    /**
     * Remove pending registration (cleanup)
     */
    public void removePendingRegistration(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        pendingRegistrations.remove(normalizedEmail);
    }
}

