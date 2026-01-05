package com.homifybackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountMeResponse {
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String username;

    private String dateOfBirth;
    private String gender;

    private String avatarUrl;

    // Agent-only fields (schema: agents table)
    private String licenseId;
    private Double rate;

    private String role;
    private String bio;

    private AddressDto address;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressDto {
        private Long addressId;
        private String zipCode;
        private String city;
        private String province;
        private String street;
        private String nation;
        private Double latitude;
        private Double longitude;
    }
}
