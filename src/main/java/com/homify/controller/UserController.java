package com.homify.controller;

import com.homify.auth.dto.ErrorResponse;
import com.homify.auth.dto.MessageResponse;
import com.homify.dto.SetPasswordRequest;
import com.homify.security.JwtService;
import com.homify.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(
            @Valid @RequestBody SetPasswordRequest setPasswordRequest,
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

            authService.setPassword(username, setPasswordRequest.getPassword());
            return ResponseEntity.ok(new MessageResponse("Password set successfully. You can now login with your password."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }
}
