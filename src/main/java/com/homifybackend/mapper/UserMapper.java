package com.homifybackend.mapper;

import com.homifybackend.dto.UserDTO;
import com.homifybackend.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserDTO toDto(Customer customer) {
    if (customer == null) return null;
    UserDTO dto = new UserDTO();
    dto.setUserId(customer.getUserId());
    dto.setFullName(customer.getFullName());
    dto.setPhoneNumber(customer.getPhoneNumber());
    dto.setAvatarUrl(customer.getAvatarUrl());
    return dto;
  }
}