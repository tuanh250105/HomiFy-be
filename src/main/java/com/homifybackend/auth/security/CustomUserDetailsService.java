package com.homifybackend.auth.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.homifybackend.auth.repository.AccountRepository;
import com.homifybackend.model.Account;
import com.homifybackend.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Use JOIN FETCH to avoid LazyInitializationException
        // Try username first, then email
        Account account = accountRepository.findByUsernameWithUser(username)
                .or(() -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("User not found by username: {}, trying email", username);
                    }
                    return accountRepository.findByEmailWithUser(username);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        User user = account.getUser();
        
        // Log which account was found (only in debug mode)
        if (logger.isDebugEnabled()) {
            logger.debug("User loaded successfully - User ID: {}, Username: {}, Role: {}", 
                    user.getUserId(),
                    account.getUsername(),
                    user.getRole() != null ? user.getRole() : "NULL");
        }

        // Handle OAuth users who don't have a password
        String password = account.getPassword() != null && !account.getPassword().isEmpty()
                ? account.getPassword()
                : "{noop}"; // No password required for OAuth users

        // Return CustomUserDetails with full user information
        return CustomUserDetails.builder()
                .userId(user.getUserId())
                .username(account.getUsername())
                .email(account.getEmail())
                .password(password)
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .registrationDate(user.getRegistrationDate())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }
    
    /**
     * Load user by user ID.
     * Used for JWT authentication where token contains user_id.
     */
    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        Account account = accountRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        
        User user = account.getUser();
        
        if (logger.isDebugEnabled()) {
            logger.debug("User loaded by ID: {}, Username: {}", userId, account.getUsername());
        }
        
        String password = account.getPassword() != null && !account.getPassword().isEmpty()
                ? account.getPassword()
                : "{noop}";
        
        return CustomUserDetails.builder()
                .userId(user.getUserId())
                .username(account.getUsername())
                .email(account.getEmail())
                .password(password)
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .registrationDate(user.getRegistrationDate())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }
}

