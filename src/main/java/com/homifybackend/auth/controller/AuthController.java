package com.homifybackend.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homifybackend.auth.dto.ChooseRoleRequest;
import com.homifybackend.auth.dto.ErrorResponse;
import com.homifybackend.auth.dto.ForgotPasswordRequest;
import com.homifybackend.auth.dto.GoogleAuthRequest;
import com.homifybackend.auth.dto.LoginRequest;
import com.homifybackend.auth.dto.MessageResponse;
import com.homifybackend.auth.dto.RefreshTokenRequest;
import com.homifybackend.auth.dto.RegisterRequest;
import com.homifybackend.auth.dto.ResetPasswordRequest;
import com.homifybackend.auth.dto.UserResponse;
import com.homifybackend.auth.dto.VerifyOtpRequest;
import com.homifybackend.auth.repository.AccountRepository;
import com.homifybackend.auth.security.JwtService;
import com.homifybackend.auth.service.AuthService;
import com.homifybackend.auth.service.GoogleOAuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private GoogleOAuthService googleOAuthService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * @deprecated Use /login/customer or /login/agent instead for role-specific login.
     */
    @Deprecated
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UserResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            // Check if account exists to provide more helpful error message
            boolean accountExists = accountRepository.existsByEmail(loginRequest.getEmail())
                    || accountRepository.existsByUsername(loginRequest.getEmail());

            String errorMessage = accountExists
                    ? "Invalid email or password"
                    : "Account not found. Please register first or verify your email if you just registered.";

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(errorMessage));
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/login/customer")
    public ResponseEntity<?> loginAsCustomer(@Valid @RequestBody LoginRequest loginRequest) {
        loginRequest.setExpectedRole("CUSTOMER");
        return login(loginRequest);
    }

    @PostMapping("/login/agent")
    public ResponseEntity<?> loginAsAgent(@Valid @RequestBody LoginRequest loginRequest) {
        loginRequest.setExpectedRole("AGENT");
        return login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Backward-compatible: default CUSTOMER
        if (registerRequest.getRole() == null || registerRequest.getRole().isBlank()) {
            registerRequest.setRole("customer");
        }
        try {
            // Validate and store registration data (account not created yet)
            authService.register(registerRequest);

            // Send OTP email for verification
            // Account will be created only after OTP verification
            String normalizedEmail = registerRequest.getEmail().trim().toLowerCase();
            authService.sendRegistrationOtp(normalizedEmail);

            return ResponseEntity.ok(new MessageResponse(
                    "Please check your email for verification code to complete registration."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerAsCustomer(@Valid @RequestBody RegisterRequest registerRequest) {
        registerRequest.setRole("customer");
        return register(registerRequest);
    }

    @PostMapping("/register/agent")
    public ResponseEntity<?> registerAsAgent(@Valid @RequestBody RegisterRequest registerRequest) {
        registerRequest.setRole("agent");
        return register(registerRequest);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        try {
            UserResponse response = authService.verifyEmailAndActivate(verifyOtpRequest);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            authService.forgotPassword(forgotPasswordRequest);
            return ResponseEntity.ok(new MessageResponse("Password reset code sent to your email."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            authService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(new MessageResponse(
                    "Password reset successful. You can now login with your new password."));
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    /**
     * @deprecated Use /google/customer or /google/agent (to be added) for role-specific google login.
     */
    @Deprecated
    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@Valid @RequestBody GoogleAuthRequest googleAuthRequest) {
        // Backward-compatible: default CUSTOMER
        return googleAuthAsCustomer(googleAuthRequest);
    }

    @PostMapping("/google/customer")
    public ResponseEntity<?> googleAuthAsCustomer(@Valid @RequestBody GoogleAuthRequest googleAuthRequest) {
        try {
            if (googleAuthRequest.getIdToken() == null || googleAuthRequest.getIdToken().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("ID token is required"));
            }

            UserResponse response = googleOAuthService.authenticateGoogleUser(googleAuthRequest.getIdToken(), com.homifybackend.model.Role.CUSTOMER);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage() != null ? e.getMessage() : "Google authentication failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred during Google authentication: " + e.getMessage()));
        }
    }

    @PostMapping("/google/agent")
    public ResponseEntity<?> googleAuthAsAgent(@Valid @RequestBody GoogleAuthRequest googleAuthRequest) {
        try {
            if (googleAuthRequest.getIdToken() == null || googleAuthRequest.getIdToken().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("ID token is required"));
            }

            UserResponse response = googleOAuthService.authenticateGoogleUser(googleAuthRequest.getIdToken(), com.homifybackend.model.Role.AGENT);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage() != null ? e.getMessage() : "Google authentication failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred during Google authentication: " + e.getMessage()));
        }
    }


    @PostMapping("/choose-role")
    public ResponseEntity<?> chooseRole(
            @Valid @RequestBody ChooseRoleRequest chooseRoleRequest,
            HttpServletRequest request) {
        try {
            // Extract username from JWT token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Authorization token required"));
            }

            String jwt = authHeader.substring(7);
            String username = jwtService.extractUsername(jwt);

            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid token"));
            }

            UserResponse response = authService.chooseRole(username, chooseRoleRequest);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            UserResponse response = authService.refreshToken(refreshTokenRequest.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }
}
