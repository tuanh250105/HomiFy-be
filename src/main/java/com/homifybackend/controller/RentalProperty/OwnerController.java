package com.homifybackend.controller.RentalProperty;

import com.homifybackend.dto.UserDTO;
import com.homifybackend.mapper.UserMapper;
import com.homifybackend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rental")
public class OwnerController {

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private UserMapper userMapper;

  private static final Long CURRENT_OWNER_ID = 101L;

  @GetMapping("/current-owner")
  public ResponseEntity<UserDTO> getCurrentOwner() {
    return customerRepository.findById(CURRENT_OWNER_ID)
        .map(customer -> ResponseEntity.ok(userMapper.toDto(customer)))
        .orElse(ResponseEntity.notFound().build());
  }
}