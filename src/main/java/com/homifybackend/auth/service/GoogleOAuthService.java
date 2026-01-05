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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.homifybackend.auth.dto.UserResponse;
import com.homifybackend.auth.repository.AccountRepository;
import com.homifybackend.repository.AgentRepository;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.auth.security.CustomUserDetailsService;
import com.homifybackend.auth.security.JwtService;
import com.homifybackend.model.Account;
import com.homifybackend.model.Agent;
import com.homifybackend.model.Customer;
import com.homifybackend.model.Role;
import com.homifybackend.model.User;

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
    private AgentRepository agentRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Value("${app.google.client-id}")
    private String clientId;

    /**
     * Backward-compatible method.
     * Default expected role is CUSTOMER.
     */
    @Transactional
    public UserResponse authenticateGoogleUser(String idTokenString) throws Exception {
        return authenticateGoogleUser(idTokenString, Role.CUSTOMER);
    }

    /**
     * Role-specific Google authentication.
     * - If user already exists: must match expectedRole.
     * - If user is new: will be created with expectedRole.
     */
    @Transactional
    public UserResponse authenticateGoogleUser(String idTokenString, Role expectedRole) throws Exception {
        logger.info("Attempting to authenticate Google user");

        if (expectedRole == null) {
            throw new RuntimeException("Expected role is required");
        }

        if (clientId == null || clientId.trim().isEmpty()) {
            logger.error("Google Client ID is not configured");
            throw new RuntimeException("Google authentication is not properly configured. Please contact support.");
        }

        if (idTokenString == null || idTokenString.trim().isEmpty()) {
            logger.error("Google ID token is null or empty");
            throw new RuntimeException("Google ID token is required");
        }

        // Try to decode token without verification first to see the audience
        String tokenAudience = null;
        try {
            String[] parts = idTokenString.split("\\.");
            if (parts.length >= 2) {
                String payload = parts[1];
                while (payload.length() % 4 != 0) {
                    payload += "=";
                }
                String decodedPayload = new String(java.util.Base64.getUrlDecoder().decode(payload));

                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"aud\"\\s*:\\s*\"([^\"]+)\"");
                java.util.regex.Matcher matcher = pattern.matcher(decodedPayload);
                if (matcher.find()) {
                    tokenAudience = matcher.group(1);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not decode token for debugging: {}", e.getMessage());
        }

        // Build list of client ID variants to try
        List<String> clientIdVariants = new ArrayList<>();

        if (tokenAudience != null && !tokenAudience.isEmpty()) {
            clientIdVariants.add(tokenAudience);
        }

        clientIdVariants.add(clientId);

        if (!clientId.endsWith(".apps.googleusercontent.com")) {
            clientIdVariants.add(clientId + ".apps.googleusercontent.com");
        }

        if (clientId.endsWith(".apps.googleusercontent.com")) {
            String withoutSuffix = clientId.replace(".apps.googleusercontent.com", "");
            if (!withoutSuffix.isEmpty()) {
                clientIdVariants.add(withoutSuffix);
            }
        }

        clientIdVariants = clientIdVariants.stream().distinct().collect(Collectors.toList());

        GoogleIdToken idToken = null;
        Exception lastException = null;

        for (String variant : clientIdVariants) {
            if (variant == null || variant.isEmpty()) {
                continue;
            }

            try {
                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                        new NetHttpTransport(), new GsonFactory())
                        .setAudience(Collections.singletonList(variant))
                        .build();

                idToken = verifier.verify(idTokenString);

                if (idToken != null) {
                    break;
                }
            } catch (com.google.api.client.auth.oauth2.TokenResponseException e) {
                lastException = e;
            } catch (java.io.IOException e) {
                lastException = e;
            } catch (Exception e) {
                lastException = e;
            }
        }
        if (idToken == null) {
            if (lastException != null) {
                logger.error("Google ID token verification failed: {}", lastException.getMessage(), lastException);
            }
            throw new RuntimeException(
                    "Failed to verify Google ID token. The token may be invalid, expired, or the client ID may not match. Configured client ID: "
                            + clientId);
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String sub = payload.getSubject();

        // Check if account exists with User fetched
        Account account = accountRepository.findByEmailWithUser(email).orElse(null);

        if (account == null) {
            // Create new user with expectedRole
            String baseUsername = email.split("@")[0];
            String uniqueSuffix = sub.length() > 8 ? sub.substring(0, 8) : sub;
            String username = baseUsername + "_" + uniqueSuffix;

            int counter = 1;
            while (accountRepository.existsByUsername(username)) {
                username = baseUsername + "_" + uniqueSuffix + "_" + counter;
                counter++;
            }

            User user = User.builder()
                    .fullName(name != null ? name : "User")
                    .registrationDate(LocalDate.now())
                    .role(expectedRole)
                    .build();

            user = userRepository.save(user);

            account = Account.builder()
                    .user(user)
                    .username(username)
                    .email(email)
                    .password(null)
                    .build();

            account = accountRepository.save(account);

            // Create role-specific row if needed
            if (expectedRole == Role.CUSTOMER) {
                Customer customer = Customer.builder().build();
                customer.setUserId(user.getUserId());
                customerRepository.save(customer);
            } else if (expectedRole == Role.AGENT) {
                Agent agent = Agent.builder().build();
                agent.setUserId(user.getUserId());
                agentRepository.save(agent);
            }
        } else {
            // Existing account: enforce role
            User user = account.getUser();
            if (user.getRole() != expectedRole) {
                throw new BadCredentialsException("You are not allowed to login on this page");
            }

            if (user.getFullName() == null || user.getFullName().isEmpty()) {
                user.setFullName(name != null ? name : "User");
                userRepository.save(user);
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
                .role(user.getRole() != null ? user.getRole().toString() : null)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
