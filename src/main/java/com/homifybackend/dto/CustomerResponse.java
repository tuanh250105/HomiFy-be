package com.homifybackend.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
}
