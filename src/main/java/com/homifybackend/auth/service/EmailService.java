package com.homifybackend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromEmail;
    
    @Value("${spring.mail.username}")
    private String mailUsername;
    
    @Value("${spring.mail.password}")
    private String mailPassword;

    public void sendOtpEmail(String to, String otpCode, String otpType) {
        try {
            System.out.println("=== Attempting to send OTP email ===");
            System.out.println("To: " + to);
            System.out.println("From: " + fromEmail);
            System.out.println("OTP Code: " + otpCode);
            System.out.println("OTP Type: " + otpType);
            
            // Verify configuration using @Value injected properties (Enterprise standard)
            System.out.println("Mail configuration check (Enterprise @Value pattern):");
            System.out.println("@Value(\"${spring.mail.username}\"): " + (mailUsername != null && !mailUsername.isEmpty() ? "✓ Set (" + mailUsername + ")" : "✗ Not set"));
            System.out.println("@Value(\"${spring.mail.password}\"): " + (mailPassword != null && !mailPassword.isEmpty() ? "✓ Set (***hidden***)" : "✗ Not set"));
            System.out.println("@Value(\"${mail.from}\"): " + (fromEmail != null && !fromEmail.isEmpty() ? "✓ Set (" + fromEmail + ")" : "✗ Not set"));
            
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                throw new RuntimeException("Email 'from' address is not configured. Please set MAIL_FROM in .env file (will be read as ${mail.from} in application.yml)");
            }
            
            if (mailSender == null) {
                throw new RuntimeException("JavaMailSender is not configured. Please check your mail configuration and ensure spring-boot-starter-mail dependency is included.");
            }
            
            // Additional validation
            if (mailUsername == null || mailUsername.trim().isEmpty()) {
                throw new RuntimeException("spring.mail.username is not configured. Please set MAIL_USERNAME in .env file.");
            }
            if (mailPassword == null || mailPassword.trim().isEmpty()) {
                throw new RuntimeException("spring.mail.password is not configured. Please set MAIL_PASSWORD in .env file.");
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            
            if ("REGISTER".equals(otpType)) {
                message.setSubject("Verify Your Email - HomiFy");
                message.setText("Welcome to HomiFy!\n\n" +
                        "Your verification code is: " + otpCode + "\n\n" +
                        "This code will expire in 10 minutes.\n\n" +
                        "If you didn't create an account, please ignore this email.");
            } else if ("FORGOT_PASSWORD".equals(otpType)) {
                message.setSubject("Reset Your Password - HomiFy");
                message.setText("You requested to reset your password.\n\n" +
                        "Your verification code is: " + otpCode + "\n\n" +
                        "This code will expire in 10 minutes.\n\n" +
                        "If you didn't request this, please ignore this email.");
            } else {
                throw new RuntimeException("Unknown OTP type: " + otpType);
            }
            
            mailSender.send(message);
            System.out.println("=== OTP email sent successfully ===");
        } catch (MailException e) {
            System.err.println("=== Email sending failed ===");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
                System.err.println("Cause class: " + e.getCause().getClass().getName());
            }
            e.printStackTrace();
            
            // Provide helpful error message for authentication failures
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.toLowerCase().contains("authentication failed")) {
                throw new RuntimeException(
                    "Email authentication failed. Please check:\n" +
                    "1. MAIL_USERNAME and MAIL_PASSWORD environment variables are set correctly\n" +
                    "2. For Gmail, use an App Password (not your regular password)\n" +
                    "3. Enable 2-Step Verification in your Google Account\n" +
                    "4. Generate App Password: https://myaccount.google.com/apppasswords\n" +
                    "Original error: " + e.getMessage(), e);
            }
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("=== Unexpected error in email service ===");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException("Unexpected error sending email: " + e.getMessage(), e);
        }
    }
}

