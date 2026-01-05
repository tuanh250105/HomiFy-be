package com.homifybackend.accountSetting_myListings.service;

import com.homifybackend.accountSetting_myListings.dto.account.AccountMeResponse;
import com.homifybackend.accountSetting_myListings.dto.account.ChangePasswordRequest;
import com.homifybackend.accountSetting_myListings.dto.account.UpdateAccountMeRequest;
import com.homifybackend.accountSetting_myListings.exception.NotFoundException;
import com.homifybackend.accountSetting_myListings.model.Account;
import com.homifybackend.accountSetting_myListings.model.Agent;
import com.homifybackend.accountSetting_myListings.model.Address;
import com.homifybackend.accountSetting_myListings.model.User;
import com.homifybackend.accountSetting_myListings.repository.AgentRepository;
import com.homifybackend.accountSetting_myListings.repository.AccountRepository;
import com.homifybackend.accountSetting_myListings.repository.AddressRepository;
import com.homifybackend.accountSetting_myListings.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountSettingService {

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    private final AddressRepository addressRepo;
    private final AgentRepository agentRepo;

    @Transactional(readOnly = true)
    public AccountMeResponse getMe(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Account a = accountRepo.findByUser_UserId(userId).orElse(null);

        Agent ag = agentRepo.findById(userId).orElse(null);
        return toResponse(u, a, ag);
    }

    @Transactional
    public AccountMeResponse updateMe(Long userId, UpdateAccountMeRequest req) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Account a = accountRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new NotFoundException("Account not found for user: " + userId));

        // Agent subtype row (optional)
        Agent ag = agentRepo.findById(userId).orElse(null);

        // User fields
        if (req.getFullName() != null) u.setFullName(req.getFullName());
        if (req.getPhoneNumber() != null) u.setPhoneNumber(req.getPhoneNumber());
        if (req.getAvatarUrl() != null) u.setAvatarUrl(req.getAvatarUrl());
        if (req.getGender() != null) u.setGender(req.getGender());
        if (req.getDateOfBirth() != null && !req.getDateOfBirth().isBlank()) {
            u.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
        }

        // Account fields
        if (req.getEmail() != null) a.setEmail(req.getEmail());
        if (req.getUsername() != null) a.setUsername(req.getUsername());

        // Agent fields (only if agents row exists OR request tries to set agent fields)
        if (req.getBio() != null || req.getLicenseId() != null || req.getRate() != null) {
            if (ag == null) {
                ag = Agent.builder().userId(userId).build();
            }
            if (req.getLicenseId() != null) ag.setLicenseId(req.getLicenseId());
            if (req.getBio() != null) ag.setBio(req.getBio());
            if (req.getRate() != null) ag.setRate(req.getRate());
            ag = agentRepo.save(ag);
        }

        // Address
        if (req.getAddress() != null) {
            Address addr = u.getAddress();
            if (addr == null) {
                addr = new Address();
            }
            if (req.getAddress().getZipCode() != null) addr.setZipCode(req.getAddress().getZipCode());
            if (req.getAddress().getCity() != null) addr.setCity(req.getAddress().getCity());
            if (req.getAddress().getProvince() != null) addr.setProvince(req.getAddress().getProvince());
            if (req.getAddress().getStreet() != null) addr.setStreet(req.getAddress().getStreet());
            if (req.getAddress().getNation() != null) addr.setNation(req.getAddress().getNation());
            if (req.getAddress().getLatitude() != null) addr.setLatitude(req.getAddress().getLatitude());
            if (req.getAddress().getLongitude() != null) addr.setLongitude(req.getAddress().getLongitude());

            // persist address first to avoid detached/transient FK issues
            addr = addressRepo.save(addr);
            u.setAddress(addr);
        }

        userRepo.save(u);
        accountRepo.save(a);
        return toResponse(u, a, ag);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        Account a = accountRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new NotFoundException("Account not found for user: " + userId));

        if (req.getNewPassword() == null || req.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 chars");
        }

        // NOTE: demo only. In production you must hash + verify old password.
        a.setPassword(req.getNewPassword());
        accountRepo.save(a);
    }

    private static AccountMeResponse toResponse(User u, Account a, Agent ag) {
        AccountMeResponse.AddressDto addr = null;
        if (u.getAddress() != null) {
            addr = AccountMeResponse.AddressDto.builder()
                    .zipCode(u.getAddress().getZipCode())
                    .city(u.getAddress().getCity())
                    .province(u.getAddress().getProvince())
                    .street(u.getAddress().getStreet())
                    .nation(u.getAddress().getNation())
                    .latitude(u.getAddress().getLatitude())
                    .longitude(u.getAddress().getLongitude())
                    .build();
        }

        return AccountMeResponse.builder()
                .userId(u.getUserId())
                .fullName(u.getFullName())
                .phoneNumber(u.getPhoneNumber())
                .email(a != null ? a.getEmail() : null)
                .username(a != null ? a.getUsername() : null)
                .dateOfBirth(u.getDateOfBirth() != null ? u.getDateOfBirth().toString() : null)
                .gender(u.getGender())
                .avatarUrl(u.getAvatarUrl())
                // optional (not in schema users)
                .bannerUrl(null)
                .org(null)
                // from agents table if available
                .bio(ag != null ? ag.getBio() : null)
                .licenseId(ag != null ? ag.getLicenseId() : null)
                .rate(ag != null ? ag.getRate() : null)
                .role(u.getRole())
                .address(addr)
                .build();
    }
}
