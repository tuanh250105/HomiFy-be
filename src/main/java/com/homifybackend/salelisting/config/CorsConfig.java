package com.homifybackend.salelisting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Cho phép credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Cho phép frontend origins (thêm domain production sau)
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // React default
            "http://localhost:5173",      // Vite default
            "http://localhost:4200",      // Angular default
            "http://localhost:8081"       // Backup port
        ));
        
        // Cho phép tất cả headers
        config.addAllowedHeader("*");
        
        // Cho phép tất cả HTTP methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Áp dụng cho tất cả endpoints
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
