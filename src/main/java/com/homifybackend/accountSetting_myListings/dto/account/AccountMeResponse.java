package com.homifybackend.accountSetting_myListings.dto.account;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountMeResponse {
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String username;

    private String dateOfBirth; // ✅ add
    private String gender;      // ✅ add

    private String avatarUrl;
    private String bannerUrl;   // ✅ add
    private String org;         // ✅ add
    private String bio;         // ✅ add

    private String role;

    private AddressDto address;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressDto {
        private String zipCode;
        private String city;
        private String province;
        private String street;
        private String nation;
        private Double latitude;
        private Double longitude;
    }
}
