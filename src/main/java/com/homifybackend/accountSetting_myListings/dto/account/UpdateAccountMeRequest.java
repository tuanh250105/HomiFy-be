package com.homifybackend.accountSetting_myListings.dto.account;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountMeRequest {
    private String fullName;
    private String phoneNumber;
    private String email;      // ✅ add
    private String username;   // ✅ add

    private String dateOfBirth;
    private String gender;

    private String avatarUrl;
    private String bannerUrl;  // ✅ add
    private String org;        // ✅ add
    private String bio;        // ✅ add

    // Agent-only fields
    private String licenseId;
    private Double rate;

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
