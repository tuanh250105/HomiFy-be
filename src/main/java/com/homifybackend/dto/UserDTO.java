// UserDTO.java (cho tenant)
package com.homifybackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
  private Long userId;
  private String fullName;
  private String phoneNumber;
  private String avatarUrl;
}