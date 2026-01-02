package com.homifybackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())  // Tắt CSRF cho API
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()   // Cho phép tất cả request không cần login
        )
        .formLogin(form -> form.disable())  // Tắt trang login form
        .httpBasic(httpBasic -> httpBasic.disable()); // Tắt basic auth

    return http.build();
  }
}