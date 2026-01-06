package com.homifybackend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Profile("prod")
public class CorsConfig {
    
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Lấy origins từ environment variable.
        // NOTE: When allowCredentials=true, allowedOrigins cannot contain "*".
        // Use allowedOriginPatterns instead when you want wildcard support.
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (origins.contains("*")) {
            // When using "*", we cannot use allowCredentials=true
            config.setAllowedOriginPatterns(List.of("*"));
            config.setAllowCredentials(false);
        } else {
            // When using specific origins, we can allow credentials
            config.setAllowedOrigins(origins);
            config.setAllowCredentials(true);
        }
        
        // Cho phép tất cả headers
        config.addAllowedHeader("*");
        
        // Cho phép tất cả HTTP methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Áp dụng cho tất cả endpoints
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
