package com.homifybackend.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homifybackend.auth.config.AuthConstants;
import com.homifybackend.auth.dto.ChooseRoleRequest;
import com.homifybackend.auth.dto.ErrorResponse;
import com.homifybackend.auth.dto.ForgotPasswordRequest;
import com.homifybackend.auth.dto.GoogleAuthRequest;
import com.homifybackend.auth.dto.LoginRequest;
import com.homifybackend.auth.dto.MessageResponse;
import com.homifybackend.auth.dto.RegisterRequest;
import com.homifybackend.auth.dto.ResetPasswordRequest;
import com.homifybackend.auth.dto.UserResponse;
import com.homifybackend.auth.dto.VerifyOtpRequest;
import com.homifybackend.auth.security.JwtService;
import com.homifybackend.auth.service.AuthService;
import com.homifybackend.auth.service.GoogleOAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final int ACCESS_TOKEN_MAX_AGE = 24 * 60 * 60; // 24 hours
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7 days

    @Autowired
    private AuthService authService;

    @Autowired
    private GoogleOAuthService googleOAuthService;

    @Autowired
    private JwtService jwtService;

    /**
     * Generic login endpoint - maintained for backward compatibility.
     * Prefer using /login/customer or /login/agent for better UX.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return handleLogin(loginRequest, null, response);
    }

    @PostMapping("/login/customer")
    public ResponseEntity<?> loginAsCustomer(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return handleLogin(loginRequest, AuthConstants.RoleType.CUSTOMER_UPPER, response);
    }

    @PostMapping("/login/agent")
    public ResponseEntity<?> loginAsAgent(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return handleLogin(loginRequest, AuthConstants.RoleType.AGENT_UPPER, response);
    }

    /**
     * Centralized login handler to avoid code duplication.
     * 
     * @param loginRequest The login credentials
     * @param expectedRole The expected role (null for any role)
     * @param response HTTP response to set cookies
     * @return ResponseEntity with user data or error
     */
    private ResponseEntity<?> handleLogin(LoginRequest loginRequest, String expectedRole, HttpServletResponse response) {
        try {
            loginRequest.setExpectedRole(expectedRole);
            UserResponse userResponse = authService.login(loginRequest);
            
            // Set JWT tokens as HttpOnly cookies
            setAuthCookies(response, userResponse.getAccessToken(), userResponse.getRefreshToken());
            
            return ResponseEntity.ok(userResponse);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            // Use generic error message to prevent user enumeration attack
            logger.warn("Failed login attempt for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INVALID_CREDENTIALS));
        } catch (Exception e) {
            logger.error("Login error for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INTERNAL_ERROR));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        // Backward-compatible: default CUSTOMER
        if (registerRequest.getRole() == null || registerRequest.getRole().isBlank()) {
            registerRequest.setRole(AuthConstants.RoleType.CUSTOMER);
        }
        return handleRegistration(registerRequest, response);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerAsCustomer(@Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        registerRequest.setRole(AuthConstants.RoleType.CUSTOMER);
        return handleRegistration(registerRequest, response);
    }

    @PostMapping("/register/agent")
    public ResponseEntity<?> registerAsAgent(@Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        registerRequest.setRole(AuthConstants.RoleType.AGENT);
        return handleRegistration(registerRequest, response);
    }

    /**
     * Centralized registration handler to avoid code duplication.
     * Note: response parameter reserved for future use (e.g., setting session cookies after auto-login)
     * 
     * @param registerRequest The registration data
     * @param response HTTP response (reserved for future use)
     * @return ResponseEntity with success message or error
     */
    private ResponseEntity<?> handleRegistration(RegisterRequest registerRequest, HttpServletResponse response) {
        try {
            // Validate and store registration data (account not created yet)
            authService.register(registerRequest);

            // Send OTP email for verification
            String normalizedEmail = registerRequest.getEmail().trim().toLowerCase();
            authService.sendRegistrationOtp(normalizedEmail);

            return ResponseEntity.ok(new MessageResponse(
                    AuthConstants.SuccessMessage.REGISTRATION_OTP_SENT));
        } catch (RuntimeException e) {
            logger.warn("Registration failed for email: {}, error: {}", 
                    registerRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Registration error for email: {}", registerRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INTERNAL_ERROR));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest, HttpServletResponse response) {
        try {
            UserResponse userResponse = authService.verifyEmailAndActivate(verifyOtpRequest);
            
            // Set JWT tokens as HttpOnly cookies
            setAuthCookies(response, userResponse.getAccessToken(), userResponse.getRefreshToken());
            
            return ResponseEntity.ok(userResponse);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            logger.warn("Email verification failed for: {}", verifyOtpRequest.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INVALID_OTP));
        } catch (Exception e) {
            logger.error("Email verification error for: {}", verifyOtpRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INTERNAL_ERROR));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            authService.forgotPassword(forgotPasswordRequest);
            // Always return success to prevent user enumeration
            return ResponseEntity.ok(new MessageResponse(
                    AuthConstants.SuccessMessage.PASSWORD_RESET_OTP_SENT));
        } catch (RuntimeException e) {
            logger.warn("Forgot password failed for: {}", forgotPasswordRequest.getEmail());
            // Still return success message to prevent user enumeration
            return ResponseEntity.ok(new MessageResponse(
                    AuthConstants.SuccessMessage.PASSWORD_RESET_OTP_SENT));
        } catch (Exception e) {
            logger.error("Forgot password error for: {}", forgotPasswordRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INTERNAL_ERROR));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            authService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(new MessageResponse(
                    AuthConstants.SuccessMessage.PASSWORD_RESET_SUCCESS));
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            logger.warn("Password reset failed for: {}", resetPasswordRequest.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INVALID_OTP));
        } catch (Exception e) {
            logger.error("Password reset error for: {}", resetPasswordRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INTERNAL_ERROR));
        }
    }

    /**
     * @deprecated Use /google/customer or /google/agent (to be added) for role-specific google login.
     */
    @Deprecated
    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@Valid @RequestBody GoogleAuthRequest googleAuthRequest, HttpServletResponse response) {
        // Backward-compatible: default CUSTOMER
        return googleAuthAsCustomer(googleAuthRequest, response);
    }

    @PostMapping("/google/customer")
    public ResponseEntity<?> googleAuthAsCustomer(@Valid @RequestBody GoogleAuthRequest googleAuthRequest, HttpServletResponse response) {
        return handleGoogleAuth(googleAuthRequest, com.homifybackend.model.Role.CUSTOMER, response);
    }

    @PostMapping("/google/agent")
    public ResponseEntity<?> googleAuthAsAgent(@Valid @RequestBody GoogleAuthRequest googleAuthRequest, HttpServletResponse response) {
        return handleGoogleAuth(googleAuthRequest, com.homifybackend.model.Role.AGENT, response);
    }

    /**
     * Centralized Google authentication handler.
     * 
     * @param googleAuthRequest The Google authentication request with ID token
     * @param role The expected user role
     * @param response HTTP response to set cookies
     * @return ResponseEntity with user data or error
     */
    private ResponseEntity<?> handleGoogleAuth(GoogleAuthRequest googleAuthRequest, com.homifybackend.model.Role role, HttpServletResponse response) {
        try {
            if (googleAuthRequest.getIdToken() == null || googleAuthRequest.getIdToken().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(AuthConstants.ErrorMessage.IDTOKEN_REQUIRED));
            }

            UserResponse userResponse = googleOAuthService.authenticateGoogleUser(
                    googleAuthRequest.getIdToken(), role);
            
            // Set JWT tokens as HttpOnly cookies
            setAuthCookies(response, userResponse.getAccessToken(), userResponse.getRefreshToken());
            
            return ResponseEntity.ok(userResponse);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            logger.warn("Google authentication failed for role: {}", role);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INVALID_CREDENTIALS));
        } catch (RuntimeException e) {
            logger.warn("Google authentication error for role: {}, error: {}", role, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.GOOGLE_AUTH_FAILED));
        } catch (Exception e) {
            logger.error("Google authentication error for role: {}", role, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INTERNAL_ERROR));
        }
    }


    @PostMapping("/choose-role")
    public ResponseEntity<?> chooseRole(
            @Valid @RequestBody ChooseRoleRequest chooseRoleRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            // Extract JWT from cookie or header
            String jwt = extractJwtFromRequest(request);
            
            if (jwt == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse(AuthConstants.ErrorMessage.TOKEN_REQUIRED));
            }

            Long userId = jwtService.extractUserId(jwt);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse(AuthConstants.ErrorMessage.INVALID_TOKEN));
            }

            UserResponse userResponse = authService.chooseRoleByUserId(userId, chooseRoleRequest);
            
            // Set new JWT tokens as HttpOnly cookies
            setAuthCookies(response, userResponse.getAccessToken(), userResponse.getRefreshToken());
            
            return ResponseEntity.ok(userResponse);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            logger.warn("Choose role failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.USER_NOT_FOUND));
        } catch (RuntimeException e) {
            logger.warn("Choose role error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Choose role error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INTERNAL_ERROR));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Extract refresh token from cookie
            String refreshToken = extractRefreshTokenFromRequest(request);
            
            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse(AuthConstants.ErrorMessage.INVALID_REFRESH_TOKEN));
            }
            
            UserResponse userResponse = authService.refreshToken(refreshToken);
            
            // Set new JWT tokens as HttpOnly cookies
            setAuthCookies(response, userResponse.getAccessToken(), userResponse.getRefreshToken());
            
            return ResponseEntity.ok(userResponse);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            logger.warn("Refresh token validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INVALID_REFRESH_TOKEN));
        } catch (RuntimeException e) {
            logger.warn("Refresh token error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INVALID_REFRESH_TOKEN));
        } catch (Exception e) {
            logger.error("Refresh token error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INTERNAL_ERROR));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            // Clear authentication cookies by setting maxAge to 0
            Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE, null);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(0);
            response.addCookie(accessTokenCookie);

            Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, null);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new MessageResponse(AuthConstants.SuccessMessage.LOGOUT_SUCCESS));
        } catch (Exception e) {
            logger.error("Logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(AuthConstants.ErrorMessage.INTERNAL_ERROR));
        }
    }

    /**
     * Set JWT tokens as HttpOnly cookies
     */
    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        // Access Token Cookie
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE, accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false); // Set to true in production with HTTPS
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(ACCESS_TOKEN_MAX_AGE);
        response.addCookie(accessTokenCookie);

        // Refresh Token Cookie
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // Set to true in production with HTTPS
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        response.addCookie(refreshTokenCookie);
    }

    /**
     * Extract JWT access token from cookie or Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        // Try cookie first
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        // Fallback to Authorization header
        String authHeader = request.getHeader(AuthConstants.Security.AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(AuthConstants.Security.BEARER_PREFIX)) {
            return authHeader.substring(AuthConstants.Security.BEARER_PREFIX_LENGTH);
        }
        
        return null;
    }

    /**
     * Extract refresh token from cookie
     */
    private String extractRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
