package com.homifybackend.auth.service;

import com.homifybackend.auth.dto.*;
import com.homifybackend.auth.model.Account;
import com.homifybackend.auth.model.User;
import com.homifybackend.auth.model.Customer;
import com.homifybackend.auth.model.Agent;
import com.homifybackend.auth.repository.AccountRepository;
import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.auth.repository.CustomerRepository;
import com.homifybackend.auth.repository.AgentRepository;
import com.homifybackend.auth.security.CustomUserDetailsService;
import com.homifybackend.auth.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

        // Find account by email or username
        Account account = accountRepository.findByEmail(loginRequest.getEmail())
                .or(() -> accountRepository.findByUsername(loginRequest.getEmail()))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        User user = account.getUser();

        // Check if remember me is enabled
        boolean rememberMe = loginRequest.getRememberMe() != null && loginRequest.getRememberMe();

        // Generate tokens with remember me support
        String accessToken = jwtService.generateToken(userDetails, rememberMe);
        String refreshToken = jwtService.generateRefreshToken(userDetails, rememberMe);

        // Build response
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

    @Transactional
    public void register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (accountRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Check if username already exists
        if (accountRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Determine role (default to "customer")
        String role = registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()
                ? registerRequest.getRole().toLowerCase()
                : "customer";

        if (!role.equals("customer") && !role.equals("agent")) {
            throw new RuntimeException("Invalid role. Must be 'customer' or 'agent'");
        }

        // Create new user
        User user = User.builder()
                .fullName(registerRequest.getFullName())
                .phoneNumber(registerRequest.getPhone())
                .registrationDate(LocalDate.now())
                .role(role)
                .build();

        user = userRepository.save(user);

        // Create account for user
        Account account = Account.builder()
                .user(user)
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        accountRepository.save(account);

        // Create role-specific record
        if ("customer".equals(role)) {
            Customer customer = Customer.builder()
                    .userId(user.getUserId())
                    .user(user)
                    .build();
            customerRepository.save(customer);
        } else if ("agent".equals(role)) {
            Agent agent = Agent.builder()
                    .userId(user.getUserId())
                    .user(user)
                    .build();
            agentRepository.save(agent);
        }

        // Send OTP for email verification
        otpService.createAndSendOtp(registerRequest.getEmail(), "REGISTER");
    }

    public UserResponse verifyEmailAndActivate(VerifyOtpRequest verifyOtpRequest) {
        // Verify OTP
        boolean isValidOtp = otpService.verifyOtp(
                verifyOtpRequest.getEmail(),
                verifyOtpRequest.getOtpCode(),
                "REGISTER"
        );

        if (!isValidOtp) {
            throw new BadCredentialsException("Invalid or expired OTP code");
        }

        // Find account
        Account account = accountRepository.findByEmail(verifyOtpRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        User user = account.getUser();

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
        // Find account
        Account account = accountRepository.findByUsername(username)
                .or(() -> accountRepository.findByEmail(username))
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
                        .userId(user.getUserId())
                        .user(user)
                        .build();
                customerRepository.save(customer);
            }
        } else if ("agent".equals(newRole)) {
            if (!agentRepository.existsById(user.getUserId())) {
                Agent agent = Agent.builder()
                        .userId(user.getUserId())
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
}

