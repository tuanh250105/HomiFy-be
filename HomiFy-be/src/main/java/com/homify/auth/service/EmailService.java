package com.homify.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public void sendOtpEmail(String to, String otpCode, String otpType) {
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
        }
        
        mailSender.send(message);
    }
}

