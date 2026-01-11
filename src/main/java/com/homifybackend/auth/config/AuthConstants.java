package com.homifybackend.auth.config;

/**
 * Constants used throughout the authentication module.
 * Centralized constants to avoid hardcoded strings and magic values.
 */
public final class AuthConstants {

    private AuthConstants() {
        // Prevent instantiation
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // OTP Types
    public static final class OtpType {
        public static final String REGISTER = "REGISTER";
        public static final String FORGOT_PASSWORD = "FORGOT_PASSWORD";

        private OtpType() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    // Role Types
    public static final class RoleType {
        public static final String CUSTOMER = "customer";
        public static final String AGENT = "agent";
        public static final String CUSTOMER_UPPER = "CUSTOMER";
        public static final String AGENT_UPPER = "AGENT";

        private RoleType() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    // Error Messages - Generic and secure
    public static final class ErrorMessage {
        // Authentication errors - Keep generic to prevent user enumeration
        public static final String INVALID_CREDENTIALS = "Invalid email or password";
        public static final String AUTHENTICATION_FAILED = "Authentication failed. Please check your credentials and try again.";
        public static final String UNAUTHORIZED_ACCESS = "You are not authorized to access this resource";
        public static final String ROLE_MISMATCH = "Please use the correct login page for your account type";
        
        // Registration errors
        public static final String EMAIL_ALREADY_EXISTS = "This email is already registered. Please login instead.";
        public static final String USERNAME_ALREADY_EXISTS = "This username is already taken. Please choose another one.";
        public static final String INVALID_ROLE = "Invalid account type. Please select either Customer or Agent.";
        
        // OTP errors
        public static final String INVALID_OTP = "Invalid or expired verification code. Please try again.";
        public static final String OTP_SEND_FAILED = "Failed to send verification code. Please try again later.";
        public static final String REGISTRATION_DATA_NOT_FOUND = "Registration session expired. Please register again.";
        
        // Token errors
        public static final String INVALID_TOKEN = "Invalid or expired token. Please login again.";
        public static final String TOKEN_REQUIRED = "Authorization token is required";
        public static final String INVALID_REFRESH_TOKEN = "Invalid or expired refresh token. Please login again.";
        
        // Validation errors
        public static final String EMAIL_REQUIRED = "Email address is required";
        public static final String PASSWORD_REQUIRED = "Password is required";
        public static final String PASSWORD_TOO_SHORT = "Password must be at least 6 characters long";
        public static final String USERNAME_REQUIRED = "Username is required";
        public static final String FULLNAME_REQUIRED = "Full name is required";
        public static final String IDTOKEN_REQUIRED = "Google ID token is required";
        
        // User not found - Keep generic
        public static final String USER_NOT_FOUND = "User account not found. Please register or verify your email.";
        
        // Generic error
        public static final String INTERNAL_ERROR = "An unexpected error occurred. Please try again later.";
        public static final String GOOGLE_AUTH_FAILED = "Google authentication failed. Please try again.";

        private ErrorMessage() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    // Success Messages
    public static final class SuccessMessage {
        public static final String REGISTRATION_OTP_SENT = "Verification code sent to your email. Please check your inbox to complete registration.";
        public static final String PASSWORD_RESET_OTP_SENT = "Password reset code sent to your email. Please check your inbox.";
        public static final String PASSWORD_RESET_SUCCESS = "Password reset successful. You can now login with your new password.";
        public static final String EMAIL_VERIFIED = "Email verified successfully. Your account is now active.";
        public static final String LOGOUT_SUCCESS = "Logged out successfully.";

        private SuccessMessage() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    // Security Settings
    public static final class Security {
        public static final String AUTH_HEADER = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final int BEARER_PREFIX_LENGTH = 7;
        public static final String ROLE_PREFIX = "ROLE_";

        private Security() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }
}
