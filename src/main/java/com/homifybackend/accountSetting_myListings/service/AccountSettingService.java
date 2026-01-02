package com.homifybackend.accountSetting_myListings.service;

import com.homifybackend.accountSetting_myListings.dto.account.AccountMeResponse;
import com.homifybackend.accountSetting_myListings.dto.account.ChangePasswordRequest;
import com.homifybackend.accountSetting_myListings.dto.account.UpdateAccountMeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountSettingService {

    private static AccountMeResponse inMemory = AccountMeResponse.builder()
            .userId(7L)
            .fullName("Nguyen Duy")
            .phoneNumber("0900000000")
            .email("agent@homify.local")
            .username("premier_agent")
            .avatarUrl("")
            .bannerUrl("")
            .org("Vesta Preferred Realty")
            .bio("Chuyên bất động sản cao cấp...")
            .gender("MALE")
            .dateOfBirth("1996-05-12")
            .role("AGENT")
            .address(AccountMeResponse.AddressDto.builder()
                    .street("12 Nguyen Van Huong")
                    .city("Ho Chi Minh City")
                    .province("Thu Duc")
                    .nation("Viet Nam")
                    .zipCode("700000")
                    .build())
            .build();

    public AccountMeResponse getMe() {
        return inMemory;
    }

    public AccountMeResponse updateMe(UpdateAccountMeRequest req) {
        if (req.getFullName() != null) inMemory.setFullName(req.getFullName());
        if (req.getPhoneNumber() != null) inMemory.setPhoneNumber(req.getPhoneNumber());
        if (req.getEmail() != null) inMemory.setEmail(req.getEmail());
        if (req.getUsername() != null) inMemory.setUsername(req.getUsername());

        if (req.getDateOfBirth() != null) inMemory.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender() != null) inMemory.setGender(req.getGender());

        if (req.getAvatarUrl() != null) inMemory.setAvatarUrl(req.getAvatarUrl());
        if (req.getBannerUrl() != null) inMemory.setBannerUrl(req.getBannerUrl());
        if (req.getOrg() != null) inMemory.setOrg(req.getOrg());
        if (req.getBio() != null) inMemory.setBio(req.getBio());

        if (req.getAddress() != null) {
            inMemory.setAddress(AccountMeResponse.AddressDto.builder()
                    .street(req.getAddress().getStreet())
                    .city(req.getAddress().getCity())
                    .province(req.getAddress().getProvince())
                    .nation(req.getAddress().getNation())
                    .zipCode(req.getAddress().getZipCode())
                    .latitude(req.getAddress().getLatitude())
                    .longitude(req.getAddress().getLongitude())
                    .build());
        }
        return inMemory;
    }

    public void changePassword(ChangePasswordRequest req) {
        if (req.getNewPassword() == null || req.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 chars");
        }
    }
}
