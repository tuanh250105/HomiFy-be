package com.homifybackend.accountSetting_myListings.service;

import com.homifybackend.auth.repository.AccountRepository;
import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.dto.AccountMeResponse;
import com.homifybackend.dto.ChangePasswordRequest;
import com.homifybackend.dto.UpdateAccountMeRequest;
import com.homifybackend.exception.NotFoundException;
import com.homifybackend.model.*;
import com.homifybackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountSettingService {

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    private final AddressRepository addressRepo;
    private final AgentRepository agentRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AccountMeResponse getMe(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Account acc = accountRepo.findByUser_UserId(userId).orElse(null);
        Address addr = u.getAddress();
        Agent agent = agentRepo.findById(userId).orElse(null);

        AccountMeResponse r = new AccountMeResponse();
        r.setUserId(u.getUserId());
        r.setFullName(u.getFullName());
        r.setPhoneNumber(u.getPhoneNumber());
        r.setEmail(acc != null ? acc.getEmail() : null);
        r.setUsername(acc != null ? acc.getUsername() : null);
        r.setAvatarUrl(u.getAvatarUrl());
        r.setRole(u.getRole() != null ? u.getRole().name() : null);
        r.setDateOfBirth(u.getDateOfBirth() != null ? u.getDateOfBirth().toString() : null);
        r.setGender(u.getGender() != null ? u.getGender().name() : null);

        // address
        if (addr != null) {
            AccountMeResponse.AddressDto a = new AccountMeResponse.AddressDto();
            a.setAddressId(addr.getAddressId());
            a.setZipCode(addr.getZipCode());
            a.setCity(addr.getCity());
            a.setProvince(addr.getProvince());
            a.setStreet(addr.getStreet());
            a.setNation(addr.getNation());
            a.setLatitude(addr.getLatitude());
            a.setLongitude(addr.getLongitude());
            r.setAddress(a);
        }

        // agent fields
        if (agent != null) {
            r.setLicenseId(agent.getLicenseId());
            r.setBio(agent.getBio());
            r.setRate(agent.getRate() != null ? agent.getRate().doubleValue() : null);
        }

        return r;
    }

    @Transactional
    public AccountMeResponse updateMe(Long userId, UpdateAccountMeRequest req) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Account acc = accountRepo.findByUser_UserId(userId).orElse(null);
        if (acc == null) {
            acc = new Account();
            acc.setUser(u);
        }

        // --- base user fields
        if (req.getFullName() != null)
            u.setFullName(req.getFullName());
        if (req.getPhoneNumber() != null)
            u.setPhoneNumber(req.getPhoneNumber());
        if (req.getAvatarUrl() != null)
            u.setAvatarUrl(req.getAvatarUrl());
        // Note: bannerUrl is in DTO but not in User model, so it's ignored

        if (req.getDateOfBirth() != null) {
            try {
                u.setDateOfBirth(java.time.LocalDate.parse(req.getDateOfBirth()));
            } catch (Exception ignored) {
                // keep old value if invalid format
            }
        }

        if (req.getGender() != null) {
            // Accept: "MALE"/"FEMALE"/"OTHER" (case-insensitive)
            try {
                u.setGender(Gender.valueOf(req.getGender().trim().toUpperCase()));
            } catch (Exception ignored) {
                // keep old value if invalid
            }
        }

        // --- account fields (email, username)
        if (req.getEmail() != null)
            acc.setEmail(req.getEmail());
        if (req.getUsername() != null)
            acc.setUsername(req.getUsername());

        // --- address
        if (req.getAddress() != null) {
            Address addr = u.getAddress();
            if (addr == null)
                addr = new Address();

            if (req.getAddress().getZipCode() != null)
                addr.setZipCode(req.getAddress().getZipCode());
            if (req.getAddress().getCity() != null)
                addr.setCity(req.getAddress().getCity());
            if (req.getAddress().getProvince() != null)
                addr.setProvince(req.getAddress().getProvince());
            if (req.getAddress().getStreet() != null)
                addr.setStreet(req.getAddress().getStreet());
            if (req.getAddress().getNation() != null)
                addr.setNation(req.getAddress().getNation());
            if (req.getAddress().getLatitude() != null)
                addr.setLatitude(req.getAddress().getLatitude());
            if (req.getAddress().getLongitude() != null)
                addr.setLongitude(req.getAddress().getLongitude());

            addr = addressRepo.save(addr);
            u.setAddress(addr);
        }

        u = userRepo.save(u);
        acc = accountRepo.save(acc);

        // --- agent fields (only if payload includes agent)
        // Note: org is in DTO but not in Agent model, so it's ignored
        if (req.getLicenseId() != null || req.getBio() != null || req.getRate() != null) {
            Agent a = agentRepo.findById(userId).orElse(null);
            if (a == null) {
                a = new Agent();
                a.setUserId(userId);
            }
            if (req.getLicenseId() != null)
                a.setLicenseId(req.getLicenseId());
            if (req.getBio() != null)
                a.setBio(req.getBio());
            if (req.getRate() != null)
                a.setRate(BigDecimal.valueOf(req.getRate()));
            agentRepo.save(a);
        }

        return getMe(userId);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        Account acc = accountRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new NotFoundException("Account not found: " + userId));

        if (req.getOldPassword() == null || req.getNewPassword() == null) {
            throw new IllegalArgumentException("Old password and new password are required");
        }

        // Verify old password
        if (!passwordEncoder.matches(req.getOldPassword(), acc.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Update password
        acc.setPassword(passwordEncoder.encode(req.getNewPassword()));
        accountRepo.save(acc);
    }
}
