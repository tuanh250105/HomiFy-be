package com.homifybackend.auth.security;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.homifybackend.model.Gender;
import com.homifybackend.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Custom UserDetails implementation that stores complete user information.
 * This is used by Spring Security for authentication and authorization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    // User information from database
    private Long userId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private LocalDate registrationDate;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String avatarUrl;
    private Role role;
    
    // Account status
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return Collections.emptyList();
        }
        String roleName = "ROLE_" + role.name();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Get user ID as string for JWT token subject.
     * Using user_id instead of username/email for better security.
     */
    public String getUserIdAsString() {
        return userId != null ? userId.toString() : null;
    }
}
