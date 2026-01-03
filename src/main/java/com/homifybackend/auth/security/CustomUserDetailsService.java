package com.homifybackend.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.homifybackend.auth.model.Account;
import com.homifybackend.auth.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Use JOIN FETCH to avoid LazyInitializationException
        Account account = accountRepository.findByUsernameWithUser(username)
                .or(() -> accountRepository.findByEmailWithUser(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // User is already fetched by JOIN FETCH, so safe to access
        String role = account.getUser() != null && account.getUser().getRole() != null 
                ? account.getUser().getRole() 
                : "customer";
        
        // Ensure role has ROLE_ prefix for Spring Security
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }

        // Handle OAuth users who don't have a password
        // OAuth users have null password, regular users have BCrypt hashed password
        String password = account.getPassword() != null && !account.getPassword().isEmpty()
                ? account.getPassword()
                : "{noop}"; // No password required for OAuth users

        return org.springframework.security.core.userdetails.User.builder()
                .username(account.getUsername())
                .password(password)
                .authorities(role)
                .build();
    }
}

