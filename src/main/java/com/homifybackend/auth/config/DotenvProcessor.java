package com.homifybackend.auth.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println("=== Loading .env file ===");
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            if (dotenv == null) {
                System.err.println("WARNING: .env file not found or could not be loaded!");
                System.err.println("Please create a .env file in the project root directory.");
                System.err.println("You can copy .env.example and rename it to .env");
                return;
            }

            Map<String, Object> props = new HashMap<>();
            dotenv.entries().forEach(e -> {
                String key = e.getKey();
                String value = e.getValue();
                props.put(key, value);
                
                // CRITICAL: Set as System property so Spring Boot can read it
                // This ensures ${MAIL_USERNAME} in application.yml works
                System.setProperty(key, value);
            });

            // Log loaded environment variables (mask sensitive data)
            System.out.println("Loaded " + props.size() + " environment variables from .env file");
            
            // Check for mail configuration
            boolean hasMailUsername = props.containsKey("MAIL_USERNAME");
            boolean hasMailPassword = props.containsKey("MAIL_PASSWORD");
            boolean hasMailFrom = props.containsKey("MAIL_FROM");
            
            System.out.println("Mail configuration check:");
            System.out.println("- MAIL_USERNAME: " + (hasMailUsername ? "✓ Set" : "✗ Missing"));
            System.out.println("- MAIL_PASSWORD: " + (hasMailPassword ? "✓ Set" : "✗ Missing"));
            System.out.println("- MAIL_FROM: " + (hasMailFrom ? "✓ Set" : "✗ Missing"));
            
            if (hasMailUsername) {
                String username = (String) props.get("MAIL_USERNAME");
                System.out.println("  MAIL_USERNAME value: " + (username != null ? username : "null"));
                System.out.println("  MAIL_USERNAME in System properties: " + System.getProperty("MAIL_USERNAME"));
            }
            if (hasMailFrom) {
                String from = (String) props.get("MAIL_FROM");
                System.out.println("  MAIL_FROM value: " + (from != null ? from : "null"));
                System.out.println("  MAIL_FROM in System properties: " + System.getProperty("MAIL_FROM"));
            }
            
            // Add to PropertySource for Spring Boot property resolution
            environment.getPropertySources().addFirst(new MapPropertySource("dotenv", props));
            
            // Verify System properties are set
            System.out.println("System properties verification:");
            System.out.println("- System.getProperty('MAIL_USERNAME'): " + System.getProperty("MAIL_USERNAME"));
            System.out.println("- System.getProperty('MAIL_PASSWORD'): " + (System.getProperty("MAIL_PASSWORD") != null ? "***set***" : "null"));
            System.out.println("- System.getProperty('MAIL_FROM'): " + System.getProperty("MAIL_FROM"));
            
            System.out.println("=== .env file loaded successfully ===");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

