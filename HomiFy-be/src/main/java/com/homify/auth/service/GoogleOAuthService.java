package com.homify.auth.service;

import com.homify.auth.dto.UserResponse;
import com.homify.model.Account;
import com.homify.model.User;
import com.homify.model.Customer;
import com.homify.repository.AccountRepository;
import com.homify.repository.UserRepository;
import com.homify.repository.CustomerRepository;
import com.homify.security.CustomUserDetailsService;
import com.homify.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

@Service
public class GoogleOAuthService {

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
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new RuntimeException("Invalid Google ID token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String sub = payload.getSubject(); // Google user ID

        // Check if account exists
        Account account = accountRepository.findByEmail(email).orElse(null);

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
                    .userId(user.getUserId())
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

