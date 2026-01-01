package com.homifybackend.manage_personalinfo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homifybackend.model.User;
import com.homifybackend.model.Account;
import com.homifybackend.model.Address;

import com.homifybackend.manage_personalinfo.dto.ManagePersonalInfoDTO;
import com.homifybackend.manage_personalinfo.repository.ManagePersonalInfoRepository;
import com.homifybackend.manage_personalinfo.exception.NotFoundException;
import com.homifybackend.manage_personalinfo.exception.ConflictException;

import java.util.regex.Pattern;

@Service
public class ManagePersonalInfoService {

    private final ManagePersonalInfoRepository repository;

    public ManagePersonalInfoService(ManagePersonalInfoRepository repository) {
        this.repository = repository;
    }

    // validate đơn giản, đủ dùng cho đồ án
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+]{8,15}$");

    public ManagePersonalInfoDTO getProfile(Long userId) {
        User user = repository.findUserById(userId);
        if (user == null) throw new NotFoundException("User not found");

        Account account = repository.findAccountByUserId(userId);

        Address address = null;
        if (user.getAddressId() != null) {
            address = repository.findAddressById(user.getAddressId());
        }

        return toDTO(user, account, address);
    }

    @Transactional
    public ManagePersonalInfoDTO updateProfile(Long userId, ManagePersonalInfoDTO dto) {

        User user = repository.findUserById(userId);
        if (user == null) throw new NotFoundException("User not found");

        Account account = repository.findAccountByUserId(userId);
        if (account == null) throw new NotFoundException("Account not found");

        // ===== validate tối thiểu theo đặc tả =====
        String email = dto.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                throw new ConflictException("Invalid email format");
            }
        }

        String phone = dto.getPhoneNumber();
        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new ConflictException("Invalid phone number format");
            }
        }

        // ===== update users =====
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setNotes(dto.getNotes());
        user.setScreenName(dto.getScreenName());
        user.setAvatarUrl(dto.getAvatarUrl());

        // ===== update accounts =====
        if (dto.getEmail() != null) {
            account.setEmail(dto.getEmail().trim());
        }

        // ===== update address city (nếu có) =====
        String city = dto.getCity();
        if (city != null && !city.trim().isEmpty()) {
            Address address;
            if (user.getAddressId() == null) {
                address = new Address();
            } else {
                address = repository.findAddressById(user.getAddressId());
                if (address == null) address = new Address();
            }

            address.setCity(city.trim());
            Address savedAddress = repository.save(address);
            user.setAddressId(savedAddress.getAddressId());
        }

        try {
            repository.save(user);
            repository.save(account);
        } catch (Exception ex) {
            String msg = (ex.getMessage() == null) ? "" : ex.getMessage().toLowerCase();

            // unique screen_name
            if (msg.contains("ux_users_screen_name") || msg.contains("screen_name")) {
                throw new ConflictException("This screen name is already taken");
            }

            // unique email
            if (msg.contains("accounts_email_key") || msg.contains("email")) {
                throw new ConflictException("This email is already registered");
            }

            throw ex;
        }

        return getProfile(userId);
    }

    private ManagePersonalInfoDTO toDTO(User user, Account account, Address address) {
        ManagePersonalInfoDTO dto = new ManagePersonalInfoDTO();
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setNotes(user.getNotes());
        dto.setScreenName(user.getScreenName());
        dto.setAvatarUrl(user.getAvatarUrl());

        dto.setEmail(account == null ? null : account.getEmail());
        dto.setCity(address == null ? null : address.getCity());

        return dto;
    }
}
