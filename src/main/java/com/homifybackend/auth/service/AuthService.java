package com.homifybackend.auth.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homifybackend.auth.dto.ChooseRoleRequest;
import com.homifybackend.auth.dto.ForgotPasswordRequest;
import com.homifybackend.auth.dto.LoginRequest;
import com.homifybackend.auth.dto.RegisterRequest;
import com.homifybackend.auth.dto.ResetPasswordRequest;
import com.homifybackend.auth.dto.UserResponse;
import com.homifybackend.auth.dto.VerifyOtpRequest;
import com.homifybackend.auth.model.Account;
import com.homifybackend.auth.model.Agent;
import com.homifybackend.auth.model.Customer;
import com.homifybackend.auth.model.User;
import com.homifybackend.auth.repository.AccountRepository;
import com.homifybackend.auth.repository.AgentRepository;
import com.homifybackend.auth.repository.CustomerRepository;
import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.auth.security.CustomUserDetailsService;
import com.homifybackend.auth.security.JwtService;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PendingRegistrationService pendingRegistrationService;

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest loginRequest) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        // Find account by email or username with User fetched (JOIN FETCH to avoid LazyInitializationException)
        Account account = accountRepository.findByEmailWithUser(loginRequest.getEmail())
                .or(() -> accountRepository.findByUsernameWithUser(loginRequest.getEmail()))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Get user - already fetched by JOIN FETCH, so no LazyInitializationException
        User user = account.getUser();
        
        // Extract all needed data within transaction to avoid LazyInitializationException
        Long userId = user.getUserId();
        String fullName = user.getFullName();
        String phone = user.getPhoneNumber();
        String role = user.getRole();
        String email = account.getEmail();
        String username = account.getUsername();

        // Check if remember me is enabled
        boolean rememberMe = loginRequest.getRememberMe() != null && loginRequest.getRememberMe();

        // Generate tokens with remember me support
        String accessToken = jwtService.generateToken(userDetails, rememberMe);
        String refreshToken = jwtService.generateRefreshToken(userDetails, rememberMe);

        // Build response with extracted data (all data extracted within transaction)
        return UserResponse.builder()
                .id(userId)
                .email(email)
                .username(username)
                .fullName(fullName)
                .phone(phone)
                .role(role)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Register a new user - only validates and sends OTP.
     * Account is created only after OTP verification in verifyEmailAndActivate()
     */
    public void register(RegisterRequest registerRequest) {
        // Validate input
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (registerRequest.getFullName() == null || registerRequest.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Full name is required");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        if (registerRequest.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        // Normalize email and username
        String normalizedEmail = registerRequest.getEmail().trim().toLowerCase();
        String normalizedUsername = registerRequest.getUsername().trim();

        // Check if email already exists
        if (accountRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already registered");
        }

        // Check if username already exists
        if (accountRepository.existsByUsername(normalizedUsername)) {
            throw new RuntimeException("Username already taken");
        }

        // Determine role (default to "customer")
        String role = registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()
                ? registerRequest.getRole().toLowerCase()
                : "customer";

        if (!role.equals("customer") && !role.equals("agent")) {
            throw new RuntimeException("Invalid role. Must be 'customer' or 'agent'");
        }

        // Store registration data temporarily (will be used after OTP verification)
        pendingRegistrationService.storePendingRegistration(normalizedEmail, registerRequest);
    }

    /**
     * Send OTP email after registration.
     * This method is called outside the transaction to avoid rollback issues.
     */
    public void sendRegistrationOtp(String email) {
        System.out.println("=== Sending registration OTP ===");
        System.out.println("Email: " + email);
        try {
            otpService.createAndSendOtp(email, "REGISTER");
            System.out.println("=== Registration OTP sent successfully ===");
        } catch (Exception e) {
            // Log the error in detail
            System.err.println("=== ERROR: Failed to send registration OTP ===");
            System.err.println("Email: " + email);
            System.err.println("Error: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            // Re-throw so controller can handle it
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }

    @Transactional
    public UserResponse verifyEmailAndActivate(VerifyOtpRequest verifyOtpRequest) {
        String normalizedEmail = verifyOtpRequest.getEmail().trim().toLowerCase();
        
        // Verify OTP
        boolean isValidOtp = otpService.verifyOtp(
                normalizedEmail,
                verifyOtpRequest.getOtpCode(),
                "REGISTER"
        );

        if (!isValidOtp) {
            throw new BadCredentialsException("Invalid or expired OTP code");
        }

        // Get pending registration data
        RegisterRequest registerRequest = pendingRegistrationService.getAndRemovePendingRegistration(normalizedEmail);
        if (registerRequest == null) {
            throw new BadCredentialsException("Registration data not found. Please register again.");
        }

        // Now create the account after OTP verification
        String normalizedUsername = registerRequest.getUsername().trim();
        String role = registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()
                ? registerRequest.getRole().toLowerCase()
                : "customer";

        // Double-check if account was created in the meantime (race condition protection)
        if (accountRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already registered");
        }
        if (accountRepository.existsByUsername(normalizedUsername)) {
            throw new RuntimeException("Username already taken");
        }

        // Create new user
        User user = User.builder()
                .fullName(registerRequest.getFullName().trim())
                .phoneNumber(registerRequest.getPhone() != null ? registerRequest.getPhone().trim() : null)
                .registrationDate(LocalDate.now())
                .role(role)
                .build();

        user = userRepository.save(user);

        // Create account for user
        Account account = Account.builder()
                .user(user)
                .username(normalizedUsername)
                .email(normalizedEmail)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        accountRepository.save(account);

        // Create role-specific record
        if ("customer".equals(role)) {
            Customer customer = Customer.builder()
                    .user(user)
                    .build();
            customerRepository.save(customer);
        } else if ("agent".equals(role)) {
            Agent agent = Agent.builder()
                    .user(user)
                    .build();
            agentRepository.save(agent);
        }

        // Auto login after verification
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return UserResponse.builder()
                .id(user.getUserId())
                .email(account.getEmail())
                .username(account.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhoneNumber())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        // Check if user exists
        if (!accountRepository.existsByEmail(forgotPasswordRequest.getEmail())) {
            throw new RuntimeException("User not found");
        }
        
        // Send OTP for password reset
        otpService.createAndSendOtp(forgotPasswordRequest.getEmail(), "FORGOT_PASSWORD");
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // Verify OTP
        boolean isValidOtp = otpService.verifyOtp(
                resetPasswordRequest.getEmail(),
                resetPasswordRequest.getOtpCode(),
                "FORGOT_PASSWORD"
        );

        if (!isValidOtp) {
            throw new BadCredentialsException("Invalid or expired OTP code");
        }

        // Update password
        Account account = accountRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        accountRepository.save(account);
    }

    @Transactional
    public void setPassword(String username, String password) {
        Account account = accountRepository.findByUsername(username)
                .or(() -> accountRepository.findByEmail(username))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
    }

    @Transactional
    public UserResponse chooseRole(String username, ChooseRoleRequest chooseRoleRequest) {
        // Find account with User fetched (JOIN FETCH to avoid LazyInitializationException)
        Account account = accountRepository.findByUsernameWithUser(username)
                .or(() -> accountRepository.findByEmailWithUser(username))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        User user = account.getUser();
        String newRole = chooseRoleRequest.getRole().toLowerCase();

        if (!newRole.equals("customer") && !newRole.equals("agent")) {
            throw new RuntimeException("Invalid role. Must be 'customer' or 'agent'");
        }

        // Update user role
        user.setRole(newRole);
        user = userRepository.save(user);

        // Create role-specific record if doesn't exist
        if ("customer".equals(newRole)) {
            if (!customerRepository.existsById(user.getUserId())) {
                Customer customer = Customer.builder()
                        .user(user)
                        .build();
                customerRepository.save(customer);
            }
        } else if ("agent".equals(newRole)) {
            if (!agentRepository.existsById(user.getUserId())) {
                Agent agent = Agent.builder()
                        .user(user)
                        .build();
                agentRepository.save(agent);
            }
        }

        // Generate new tokens with updated role
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return UserResponse.builder()
                .id(user.getUserId())
                .email(account.getEmail())
                .username(account.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhoneNumber())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtService.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        // Extract username from refresh token
        String username = jwtService.extractUsername(refreshToken);
        if (username == null) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Find account with User fetched (JOIN FETCH to avoid LazyInitializationException)
        Account account = accountRepository.findByEmailWithUser(username)
                .or(() -> accountRepository.findByUsernameWithUser(username))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        User user = account.getUser();

        // Generate new tokens (use default expiration, not remember me)
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        // Build response
        return UserResponse.builder()
                .id(user.getUserId())
                .email(account.getEmail())
                .username(account.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhoneNumber())
                .role(user.getRole())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}

