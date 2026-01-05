package com.homifybackend.auth.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.homifybackend.auth.dto.UserResponse;
import com.homifybackend.model.Account;
import com.homifybackend.model.Customer;
import com.homifybackend.model.User;
import com.homifybackend.auth.repository.AccountRepository;
import com.homifybackend.auth.repository.CustomerRepository;
import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.auth.security.CustomUserDetailsService;
import com.homifybackend.auth.security.JwtService;

@Service
public class GoogleOAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleOAuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Value("${app.google.client-id}")
    private String clientId;

    @Transactional
    public UserResponse authenticateGoogleUser(String idTokenString) throws Exception {
        logger.info("Attempting to authenticate Google user");
        
        if (clientId == null || clientId.trim().isEmpty()) {
            logger.error("Google Client ID is not configured");
            throw new RuntimeException("Google authentication is not properly configured. Please contact support.");
        }
        
        logger.info("Client ID from config: {}", clientId);
        
        if (idTokenString == null || idTokenString.trim().isEmpty()) {
            logger.error("Google ID token is null or empty");
            throw new RuntimeException("Google ID token is required");
        }

        logger.debug("ID token length: {}", idTokenString.length());
        logger.debug("ID token preview: {}", idTokenString.substring(0, Math.min(50, idTokenString.length())) + "...");

        // Try to decode token without verification first to see the audience
        String tokenAudience = null;
        try {
            String[] parts = idTokenString.split("\\.");
            if (parts.length >= 2) {
                // Decode payload (base64url)
                String payload = parts[1];
                // Add padding if needed
                while (payload.length() % 4 != 0) {
                    payload += "=";
                }
                String decodedPayload = new String(java.util.Base64.getUrlDecoder().decode(payload));
                logger.info("Decoded token payload (for debugging): {}", decodedPayload);
                
                // Try to extract audience from JSON using regex for more reliable parsing
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"aud\"\\s*:\\s*\"([^\"]+)\"");
                java.util.regex.Matcher matcher = pattern.matcher(decodedPayload);
                if (matcher.find()) {
                    tokenAudience = matcher.group(1);
                    logger.info("Token audience (client ID from token): {}", tokenAudience);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not decode token for debugging: {}", e.getMessage());
        }

        // Build list of client ID variants to try
        List<String> clientIdVariants = new ArrayList<>();
        
        // Add the exact client ID from token if we decoded it
        if (tokenAudience != null && !tokenAudience.isEmpty()) {
            clientIdVariants.add(tokenAudience);
            logger.info("Will try token's client ID first: {}", tokenAudience);
        }
        
        // Add configured client ID variants
        clientIdVariants.add(clientId); // Original format
        
        // Add variant with .apps.googleusercontent.com suffix if missing
        if (!clientId.endsWith(".apps.googleusercontent.com")) {
            clientIdVariants.add(clientId + ".apps.googleusercontent.com");
        }
        
        // Add variant without .apps.googleusercontent.com suffix if present
        if (clientId.endsWith(".apps.googleusercontent.com")) {
            String withoutSuffix = clientId.replace(".apps.googleusercontent.com", "");
            if (!withoutSuffix.isEmpty()) {
                clientIdVariants.add(withoutSuffix);
            }
        }
        
        // Remove duplicates
        clientIdVariants = clientIdVariants.stream().distinct().collect(Collectors.toList());
        logger.info("Will try {} client ID variant(s): {}", clientIdVariants.size(), clientIdVariants);

        GoogleIdToken idToken = null;
        Exception lastException = null;
        
        for (String variant : clientIdVariants) {
            if (variant == null || variant.isEmpty()) {
                continue;
            }
            
            try {
                logger.info("Attempting to verify Google ID token with client ID: {}", variant);
                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                        new NetHttpTransport(), new GsonFactory())
                        .setAudience(Collections.singletonList(variant))
                        .build();
                
                idToken = verifier.verify(idTokenString);
                
                if (idToken != null) {
                    logger.info("✓ Token verification succeeded with client ID: {}", variant);
                    break;
                } else {
                    logger.warn("Token verification returned null for client ID: {}", variant);
                }
            } catch (com.google.api.client.auth.oauth2.TokenResponseException e) {
                logger.error("TokenResponseException with client ID '{}': Status {} - {}", variant, e.getStatusCode(), e.getMessage());
                lastException = e;
            } catch (java.io.IOException e) {
                logger.error("IOException during verification with client ID '{}': {}", variant, e.getMessage());
                lastException = e;
            } catch (Exception e) {
                logger.error("Exception during verification with client ID '{}': {} - {}", variant, e.getClass().getSimpleName(), e.getMessage());
                if (e.getCause() != null) {
                    logger.error("Cause: {}", e.getCause().getMessage());
                }
                lastException = e;
            }
        }

        if (idToken == null) {
            logger.error("Google ID token verification failed with all client ID variants");
            logger.error("Configured client ID: {}", clientId);
            if (lastException != null) {
                logger.error("Last exception type: {}", lastException.getClass().getName());
                logger.error("Last exception message: {}", lastException.getMessage(), lastException);
            }
            throw new RuntimeException("Failed to verify Google ID token. The token may be invalid, expired, or the client ID may not match. Configured client ID: " + clientId + ". Please ensure the client ID in your backend matches the one used in the frontend.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String sub = payload.getSubject(); // Google user ID

        // Check if account exists with User fetched (JOIN FETCH to avoid LazyInitializationException)
        Account account = accountRepository.findByEmailWithUser(email).orElse(null);

        if (account == null) {
            // Create new user
            String baseUsername = email.split("@")[0];
            String uniqueSuffix = sub.length() > 8 ? sub.substring(0, 8) : sub;
            String username = baseUsername + "_" + uniqueSuffix;
            
            // Ensure username is unique
            int counter = 1;
            while (accountRepository.existsByUsername(username)) {
                username = baseUsername + "_" + uniqueSuffix + "_" + counter;
                counter++;
            }

            User user = User.builder()
                    .fullName(name != null ? name : "User")
                    .registrationDate(LocalDate.now())
                    .role("customer")
                    .build();

            user = userRepository.save(user);

            // Create account
            account = Account.builder()
                    .user(user)
                    .username(username)
                    .email(email)
                    .password(null) // OAuth users don't need password
                    .build();

            account = accountRepository.save(account);

            // Create customer record
            Customer customer = Customer.builder()
                    .user(user)
                    .build();
            customerRepository.save(customer);
        } else {
            // Update existing user if needed
            User user = account.getUser();
            if (user.getFullName() == null || user.getFullName().isEmpty()) {
                user.setFullName(name != null ? name : "User");
                userRepository.save(user);
            }

            // Ensure Customer entity exists for existing users
            if ("customer".equals(user.getRole())) {
                boolean customerExists = customerRepository.existsById(user.getUserId());
                if (!customerExists) {
                    Customer customer = Customer.builder()
                            .user(user)
                            .build();
                    customerRepository.save(customer);
                }
            }
        }

        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        User user = account.getUser();
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

