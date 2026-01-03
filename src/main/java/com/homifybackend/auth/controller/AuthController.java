package com.homifybackend.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private GoogleOAuthService googleOAuthService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

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

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Log the registration attempt for debugging
            System.out.println("=== Registration Request ===");
            System.out.println("Email: " + registerRequest.getEmail());
            System.out.println("Username: " + registerRequest.getUsername());
            System.out.println("Full Name: " + registerRequest.getFullName());
            
            // Validate and store registration data (account not created yet)
            authService.register(registerRequest);
            System.out.println("Registration data validated and stored");
            
            // Send OTP email for verification
            // Account will be created only after OTP verification
            String normalizedEmail = registerRequest.getEmail().trim().toLowerCase();
            try {
                authService.sendRegistrationOtp(normalizedEmail);
                System.out.println("OTP email sent successfully");
            } catch (Exception emailException) {
                // If email fails, still return success but warn user
                System.err.println("=== WARNING: Registration successful but email failed ===");
                System.err.println("Email error: " + emailException.getMessage());
                emailException.printStackTrace();
                
                // Return success but with a warning message
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body(new ErrorResponse("Registration data saved, but failed to send verification email. Please contact support or try again later. Error: " + emailException.getMessage()));
            }
            
            return ResponseEntity.ok(new MessageResponse("Please check your email for verification code to complete registration."));
        } catch (RuntimeException e) {
            // Log the error for debugging
            System.err.println("=== Registration Validation Failed ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("=== Unexpected Registration Error ===");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
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
            return ResponseEntity.ok(new MessageResponse("Password reset successful. You can now login with your new password."));
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@Valid @RequestBody GoogleAuthRequest googleAuthRequest) {
        try {
            if (googleAuthRequest.getIdToken() == null || googleAuthRequest.getIdToken().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("ID token is required"));
            }
            
            UserResponse response = googleOAuthService.authenticateGoogleUser(googleAuthRequest.getIdToken());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Log the error for debugging
            System.err.println("=== Google Auth Error ===");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            
            // Check if it's a configuration error (should be 500) vs authentication error (401)
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("not properly configured")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse(errorMessage));
            }
            
            // Return more detailed error message for debugging
            String detailedMessage = errorMessage != null ? errorMessage : "Google authentication failed";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(detailedMessage));
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("=== Google Auth Exception ===");
            System.err.println("Exception: " + e.getMessage());
            System.err.println("Exception class: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
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

