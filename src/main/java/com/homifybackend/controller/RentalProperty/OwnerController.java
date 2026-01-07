package com.homifybackend.controller.RentalProperty;

import com.homifybackend.dto.UserDTO;
import com.homifybackend.mapper.UserMapper;
import com.homifybackend.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rental")
public class OwnerController {

  private final CustomerRepository customerRepository;
  private final UserMapper userMapper;

  public OwnerController(CustomerRepository customerRepository, UserMapper userMapper) {
    this.customerRepository = customerRepository;
    this.userMapper = userMapper;
  }

  @GetMapping("/current-owner")
  public ResponseEntity<UserDTO> getCurrentOwner(@RequestParam Long ownerId) {
    return customerRepository.findById(ownerId)
        .map(customer -> ResponseEntity.ok(userMapper.toDto(customer)))
        .orElse(ResponseEntity.notFound().build());
  }
}
