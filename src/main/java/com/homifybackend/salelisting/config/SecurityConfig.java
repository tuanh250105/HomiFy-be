package com.homifybackend.salelisting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF vì đang dùng REST API (stateless)
            .csrf(csrf -> csrf.disable())
            
            // Tắt HTTP Basic Authentication
            .httpBasic(httpBasic -> httpBasic.disable())
            
            // Tắt Form Login
            .formLogin(formLogin -> formLogin.disable())
            
            // Cấu hình authorization
            .authorizeHttpRequests(auth -> auth
                // Cho phép tất cả endpoints (để dev dễ dàng)
                .anyRequest().permitAll()
            )
            
            // Stateless session (không dùng session, dùng JWT sau này)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}
