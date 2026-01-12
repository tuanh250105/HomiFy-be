package com.homifybackend.service;

import com.homifybackend.auth.repository.AccountRepository;
import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.dto.AccountMeResponse;
import com.homifybackend.exception.NotFoundException;
import com.homifybackend.model.*;
import com.homifybackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    private final AgentRepository agentRepo;

    /**
     * Get current authenticated user info from SecurityContext
     */
    @Transactional(readOnly = true)
    public AccountMeResponse getCurrentUser() {
        String username = getCurrentUsername();
        log.info("Getting user info for username: {}", username);
        
        Account account = accountRepo.findByUsernameWithUser(username)
                .or(() -> accountRepo.findByEmailWithUser(username))
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
        
        User user = account.getUser();
        if (user == null) {
            throw new NotFoundException("User entity not found for account: " + username);
        }
        
        return buildAccountMeResponse(user, account);
    }

    /**
     * Get user info by userId
     */
    @Transactional(readOnly = true)
    public AccountMeResponse getUserById(Long userId) {
        log.info("Getting user info for userId: {}", userId);
        
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        Account account = accountRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new NotFoundException("Account not found for user ID: " + userId));
        
        return buildAccountMeResponse(user, account);
    }

    /**
     * Build AccountMeResponse from User and Account entities
     */
    private AccountMeResponse buildAccountMeResponse(User user, Account account) {
        Address addr = user.getAddress();
        Agent agent = agentRepo.findById(user.getUserId()).orElse(null);

        AccountMeResponse response = new AccountMeResponse();
        response.setUserId(user.getUserId());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(account.getEmail());
        response.setUsername(account.getUsername());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRole(user.getRole() != null ? user.getRole().name() : null);
        response.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
        response.setGender(user.getGender() != null ? user.getGender().name() : null);

        // Address
        if (addr != null) {
            AccountMeResponse.AddressDto addressDto = new AccountMeResponse.AddressDto();
            addressDto.setAddressId(addr.getAddressId());
            addressDto.setZipCode(addr.getZipCode());
            addressDto.setCity(addr.getCity());
            addressDto.setProvince(addr.getProvince());
            addressDto.setStreet(addr.getStreet());
            addressDto.setNation(addr.getNation());
            addressDto.setLatitude(addr.getLatitude());
            addressDto.setLongitude(addr.getLongitude());
            response.setAddress(addressDto);
        }

        // Agent fields
        if (agent != null) {
            response.setLicenseId(agent.getLicenseId());
            response.setBio(agent.getBio());
            response.setRate(agent.getRate() != null ? agent.getRate().doubleValue() : null);
        }

        return response;
    }

    /**
     * Get current authenticated username from SecurityContext
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String username) {
            return username;
        }
        
        throw new RuntimeException("Unable to get username from authentication");
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !(authentication.getPrincipal() instanceof String && 
                 authentication.getPrincipal().equals("anonymousUser"));
    }
}
