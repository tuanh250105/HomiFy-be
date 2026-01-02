package com.homifybackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController  // Quan trọng: trả về JSON, không phải view HTML
public class HelloController {

  @GetMapping("/api/hello")
  public String hello() {
    return "Hello from HomiFy Backend! 🚀 Time: " + LocalDateTime.now();
  }

  // Thêm một endpoint root để tránh 404 khi mở localhost:8080
  @GetMapping("/")
  public String home() {
    return "HomiFy Backend is running! Access API at /api/hello or use Swagger later.";
  }
}