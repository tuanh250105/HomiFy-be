package com.homifybackend.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.homifybackend.auth.config.AuthConstants;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

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
            logger.debug("Attempting to send OTP email to: {}, type: {}", to, otpType);
            
            // Verify configuration
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                throw new RuntimeException("Email 'from' address is not configured. Please set MAIL_FROM in .env file");
            }
            
            if (mailSender == null) {
                throw new RuntimeException("JavaMailSender is not configured. Please check your mail configuration.");
            }
            
            if (mailUsername == null || mailUsername.trim().isEmpty()) {
                throw new RuntimeException("spring.mail.username is not configured. Please set MAIL_USERNAME in .env file.");
            }
            if (mailPassword == null || mailPassword.trim().isEmpty()) {
                throw new RuntimeException("spring.mail.password is not configured. Please set MAIL_PASSWORD in .env file.");
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            
            if (AuthConstants.OtpType.REGISTER.equals(otpType)) {
                message.setSubject("Verify Your Email - HomiFy");
                message.setText("Welcome to HomiFy!\n\n" +
                        "Your verification code is: " + otpCode + "\n\n" +
                        "This code will expire in 10 minutes.\n\n" +
                        "If you didn't create an account, please ignore this email.");
            } else if (AuthConstants.OtpType.FORGOT_PASSWORD.equals(otpType)) {
                message.setSubject("Reset Your Password - HomiFy");
                message.setText("You requested to reset your password.\n\n" +
                        "Your verification code is: " + otpCode + "\n\n" +
                        "This code will expire in 10 minutes.\n\n" +
                        "If you didn't request this, please ignore this email.");
            } else {
                throw new RuntimeException("Unknown OTP type: " + otpType);
            }
            
            mailSender.send(message);
            logger.debug("OTP email sent successfully to: {}", to);
        } catch (MailException e) {
            logger.error("Email sending failed to: {}", to, e);
            
            // Provide helpful error message for authentication failures
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.toLowerCase().contains("authentication failed")) {
                throw new RuntimeException(
                    "Email authentication failed. Please check MAIL_USERNAME and MAIL_PASSWORD", e);
            }
            throw new RuntimeException(AuthConstants.ErrorMessage.OTP_SEND_FAILED, e);
        } catch (Exception e) {
            logger.error("Unexpected error in email service for: {}", to, e);
            throw new RuntimeException(AuthConstants.ErrorMessage.OTP_SEND_FAILED, e);
        }
    }
}

