package com.homifybackend.auth.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homifybackend.auth.config.AuthConstants;
import com.homifybackend.auth.dto.ChooseRoleRequest;
import com.homifybackend.auth.dto.ForgotPasswordRequest;
import com.homifybackend.auth.dto.LoginRequest;
import com.homifybackend.auth.dto.RegisterRequest;
import com.homifybackend.auth.dto.ResetPasswordRequest;
import com.homifybackend.auth.dto.UserResponse;
import com.homifybackend.auth.dto.VerifyOtpRequest;
import com.homifybackend.auth.repository.AccountRepository;
import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.auth.security.CustomUserDetailsService;
import com.homifybackend.auth.security.JwtService;
import com.homifybackend.model.Account;
import com.homifybackend.model.Agent;
import com.homifybackend.model.Customer;
import com.homifybackend.model.Role;
import com.homifybackend.model.User;
import com.homifybackend.repository.AgentRepository;
import com.homifybackend.repository.CustomerRepository;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

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

        // Load user details - this will find the account by email or username
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        // Use the username from UserDetails (which was used for authentication) to find the account
        // This ensures we get the same account that was authenticated
        String authenticatedUsername = userDetails.getUsername();
        Account account = accountRepository.findByUsernameWithUser(authenticatedUsername)
                .orElseThrow(() -> new BadCredentialsException("Account not found for authenticated user: " + authenticatedUsername));

        // Get user - already fetched by JOIN FETCH, so no LazyInitializationException
        User user = account.getUser();
        
        // Log for debugging
        System.out.println("Login input: " + loginRequest.getEmail());
        System.out.println("Authenticated username: " + authenticatedUsername);
        System.out.println("Found account ID: " + account.getAccountId());
        System.out.println("Found user ID: " + user.getUserId());
        System.out.println("User role: " + (user.getRole() != null ? user.getRole() : "NULL"));
        
        // Enforce role-specific login when provided (used by /login/customer and /login/agent)
        if (loginRequest.getExpectedRole() != null && !loginRequest.getExpectedRole().isBlank()) {
            Role expectedRole;
            try {
                expectedRole = Role.valueOf(loginRequest.getExpectedRole().trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new BadCredentialsException("Invalid expected role");
            }

            if (user.getRole() != expectedRole) {
                throw new BadCredentialsException(AuthConstants.ErrorMessage.ROLE_MISMATCH);
            }
        }

        // Extract all needed data within transaction to avoid LazyInitializationException
        String fullName = user.getFullName();
        String phone = user.getPhoneNumber();
        String role = user.getRole() != null ? user.getRole().toString() : null;
        String email = account.getEmail();
        String username = account.getUsername();

        // Check if remember me is enabled
        boolean rememberMe = loginRequest.getRememberMe() != null && loginRequest.getRememberMe();

        // Generate tokens with remember me support
        String accessToken = jwtService.generateToken(userDetails, rememberMe);
        String refreshToken = jwtService.generateRefreshToken(userDetails, rememberMe);

        // Build response with extracted data (all data extracted within transaction)
        return UserResponse.builder()
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
            throw new RuntimeException(AuthConstants.ErrorMessage.EMAIL_REQUIRED);
        }
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new RuntimeException(AuthConstants.ErrorMessage.USERNAME_REQUIRED);
        }
        if (registerRequest.getFullName() == null || registerRequest.getFullName().trim().isEmpty()) {
            throw new RuntimeException(AuthConstants.ErrorMessage.FULLNAME_REQUIRED);
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            throw new RuntimeException(AuthConstants.ErrorMessage.PASSWORD_REQUIRED);
        }
        if (registerRequest.getPassword().length() < 6) {
            throw new RuntimeException(AuthConstants.ErrorMessage.PASSWORD_TOO_SHORT);
        }

        // Normalize email and username
        String normalizedEmail = registerRequest.getEmail().trim().toLowerCase();
        String normalizedUsername = registerRequest.getUsername().trim();

        // Check if email already exists
        if (accountRepository.existsByEmail(normalizedEmail)) {
            // If the email exists, tell user to login on the correct page based on existing role
            Account existingAccount = accountRepository.findByEmailWithUser(normalizedEmail).orElse(null);
            if (existingAccount != null && existingAccount.getUser() != null && existingAccount.getUser().getRole() != null) {
                throw new RuntimeException("Email already registered as " + existingAccount.getUser().getRole()
                        + ". Please login on the correct page.");
            }
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
        logger.debug("Sending registration OTP to: {}", email);
        try {
            otpService.createAndSendOtp(email, AuthConstants.OtpType.REGISTER);
            logger.debug("Registration OTP sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send registration OTP to: {}", email, e);
            throw new RuntimeException(AuthConstants.ErrorMessage.OTP_SEND_FAILED, e);
        }
    }

    @Transactional
    public UserResponse verifyEmailAndActivate(VerifyOtpRequest verifyOtpRequest) {
        String normalizedEmail = verifyOtpRequest.getEmail().trim().toLowerCase();
        
        // Verify OTP
        boolean isValidOtp = otpService.verifyOtp(
                normalizedEmail,
                verifyOtpRequest.getOtpCode(),
                AuthConstants.OtpType.REGISTER
        );

        if (!isValidOtp) {
            throw new BadCredentialsException(AuthConstants.ErrorMessage.INVALID_OTP);
        }

        // Get pending registration data
        RegisterRequest registerRequest = pendingRegistrationService.getAndRemovePendingRegistration(normalizedEmail);
        if (registerRequest == null) {
            throw new BadCredentialsException(AuthConstants.ErrorMessage.REGISTRATION_DATA_NOT_FOUND);
        }

        // Now create the account after OTP verification
        String normalizedUsername = registerRequest.getUsername().trim();
        // Parse role according to current model enum (CUSTOMER / AGENT)
        String roleInput = (registerRequest.getRole() != null && !registerRequest.getRole().isBlank())
                ? registerRequest.getRole().trim()
                : AuthConstants.RoleType.CUSTOMER;

        Role role;
        try {
            role = Role.valueOf(roleInput.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(AuthConstants.ErrorMessage.INVALID_ROLE);
        }
        // Double-check if account was created in the meantime (race condition protection)
        if (accountRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException(AuthConstants.ErrorMessage.EMAIL_ALREADY_EXISTS);
        }
        if (accountRepository.existsByUsername(normalizedUsername)) {
            throw new RuntimeException(AuthConstants.ErrorMessage.USERNAME_ALREADY_EXISTS);
        }

        // With JOINED inheritance (Customer/Agent extends User), we must persist the subtype,
        // otherwise saving the subtype later will create a SECOND users row with null fields.
        User persistedUser;
        switch (role) {
            case CUSTOMER:
            Customer customer = Customer.builder()
                    .fullName(registerRequest.getFullName().trim())
                    .phoneNumber(registerRequest.getPhone() != null ? registerRequest.getPhone().trim() : null)
                    .registrationDate(LocalDate.now())
                    .role(Role.CUSTOMER)
                    .build();
            persistedUser = customerRepository.save(customer);
                break;
            case AGENT:
                Agent agent = Agent.builder()
                    .fullName(registerRequest.getFullName().trim())
                    .phoneNumber(registerRequest.getPhone() != null ? registerRequest.getPhone().trim() : null)
                    .registrationDate(LocalDate.now())
                    .role(Role.AGENT)
                    .build();
            persistedUser = agentRepository.save(agent);
                break;
            default:
                throw new RuntimeException(AuthConstants.ErrorMessage.INVALID_ROLE);
        }

        // Create account for user
        Account account = Account.builder()
                .user(persistedUser)
                .username(normalizedUsername)
                .email(normalizedEmail)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        accountRepository.save(account);

        // Auto login after verification
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return UserResponse.builder()
                .email(account.getEmail())
                .username(account.getUsername())
                .fullName(persistedUser.getFullName())
                .phone(persistedUser.getPhoneNumber())
                .role(persistedUser.getRole() != null ? persistedUser.getRole().toString() : null)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        // Check if user exists - but don't reveal this info to prevent enumeration
        if (!accountRepository.existsByEmail(forgotPasswordRequest.getEmail())) {
            logger.warn("Forgot password requested for non-existent email: {}", 
                    forgotPasswordRequest.getEmail());
            // Don't throw exception to prevent user enumeration
            return;
        }
        
        // Send OTP for password reset
        otpService.createAndSendOtp(forgotPasswordRequest.getEmail(), 
                AuthConstants.OtpType.FORGOT_PASSWORD);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // Verify OTP
        boolean isValidOtp = otpService.verifyOtp(
                resetPasswordRequest.getEmail(),
                resetPasswordRequest.getOtpCode(),
                AuthConstants.OtpType.FORGOT_PASSWORD
        );

        if (!isValidOtp) {
            throw new BadCredentialsException(AuthConstants.ErrorMessage.INVALID_OTP);
        }

        // Update password
        Account account = accountRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException(AuthConstants.ErrorMessage.USER_NOT_FOUND));

        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        accountRepository.save(account);
    }

    @Transactional
    public void setPassword(String username, String password) {
        Account account = accountRepository.findByUsername(username)
                .or(() -> accountRepository.findByEmail(username))
                .orElseThrow(() -> new BadCredentialsException(AuthConstants.ErrorMessage.USER_NOT_FOUND));

        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
    }

    @Transactional
    public UserResponse chooseRole(String username, ChooseRoleRequest chooseRoleRequest) {
        // Find account with User fetched (JOIN FETCH to avoid LazyInitializationException)
        Account account = accountRepository.findByUsernameWithUser(username)
                .or(() -> accountRepository.findByEmailWithUser(username))
                .orElseThrow(() -> new BadCredentialsException(AuthConstants.ErrorMessage.USER_NOT_FOUND));

        User user = account.getUser();
        String roleInput = (chooseRoleRequest.getRole() != null) ? chooseRoleRequest.getRole().trim() : "";

        Role newRole;
        try {
            newRole = Role.valueOf(roleInput.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(AuthConstants.ErrorMessage.INVALID_ROLE);
        }

        // Update user role
        user.setRole(newRole);
        user = userRepository.save(user);

        // Create role-specific record if doesn't exist
        // NOTE: Customer/Agent extends User (JOINED). We shouldn't create a new row with the same PK.
        // At this stage we only ensure related tables exist when the model supports it.
        // Current Customer/Agent classes don't expose a (user) association, so we skip creation here.
        // If you need promotion/demotion between roles, implement it at entity level (migrate inheritance row).
        if (newRole == Role.CUSTOMER) {
            // no-op
        } else if (newRole == Role.AGENT) {
            // no-op
        }

        // Generate new tokens with updated role
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return UserResponse.builder()
                .email(account.getEmail())
                .username(account.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().toString() : null)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public UserResponse chooseRoleByUserId(Long userId, ChooseRoleRequest chooseRoleRequest) {
        // Find account by user ID
        Account account = accountRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BadCredentialsException(AuthConstants.ErrorMessage.USER_NOT_FOUND));

        User user = account.getUser();
        String roleInput = (chooseRoleRequest.getRole() != null) ? chooseRoleRequest.getRole().trim() : "";

        Role newRole;
        try {
            newRole = Role.valueOf(roleInput.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(AuthConstants.ErrorMessage.INVALID_ROLE);
        }

        // Update user role
        user.setRole(newRole);
        user = userRepository.save(user);

        // Generate new tokens with updated role
        CustomUserDetailsService customService = (CustomUserDetailsService) userDetailsService;
        UserDetails userDetails = customService.loadUserByUserId(userId);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return UserResponse.builder()
                .email(account.getEmail())
                .username(account.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().toString() : null)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtService.validateToken(refreshToken)) {
            throw new BadCredentialsException(AuthConstants.ErrorMessage.INVALID_REFRESH_TOKEN);
        }

        // Extract user_id from refresh token
        Long userId = jwtService.extractUserId(refreshToken);
        if (userId == null) {
            throw new BadCredentialsException(AuthConstants.ErrorMessage.INVALID_REFRESH_TOKEN);
        }

        // Load user details by user_id
        CustomUserDetailsService customService = (CustomUserDetailsService) userDetailsService;
        UserDetails userDetails = customService.loadUserByUserId(userId);

        // Find account with User fetched
        Account account = accountRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BadCredentialsException(AuthConstants.ErrorMessage.USER_NOT_FOUND));

        User user = account.getUser();

        // Generate new tokens (use default expiration, not remember me)
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        // Build response
        return UserResponse.builder()
                .email(account.getEmail())
                .username(account.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().toString() : null)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}

