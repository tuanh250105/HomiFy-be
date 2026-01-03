package com.homifybackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Configuration class to verify mail settings are loaded correctly
 */
@Component
public class MailConfig {

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${mail.from}")
    private String mailFrom;

    @EventListener(ApplicationReadyEvent.class)
    public void verifyMailConfiguration() {
        System.out.println("=== Mail Configuration Verification (Enterprise @Value Pattern) ===");
        System.out.println("@Value(\"${spring.mail.username}\"): " + (mailUsername != null && !mailUsername.isEmpty() ? "✓ " + mailUsername : "✗ Not set"));
        System.out.println("@Value(\"${spring.mail.password}\"): " + (mailPassword != null && !mailPassword.isEmpty() ? "✓ Set (***hidden***)" : "✗ Not set"));
        System.out.println("@Value(\"${mail.from}\"): " + (mailFrom != null && !mailFrom.isEmpty() ? "✓ " + mailFrom : "✗ Not set"));
        
        if (mailUsername == null || mailUsername.isEmpty() || 
            mailPassword == null || mailPassword.isEmpty() || 
            mailFrom == null || mailFrom.isEmpty()) {
            System.err.println("WARNING: Mail configuration is incomplete!");
            System.err.println("Please check your .env file and ensure MAIL_USERNAME, MAIL_PASSWORD, and MAIL_FROM are set.");
        } else {
            System.out.println("=== Mail configuration is ready ===");
        }
    }
}

