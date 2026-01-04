package com.homifybackend.manage_personalinfo.service;

import com.homifybackend.manage_personalinfo.dto.ManagePersonalInfoDTO;
import com.homifybackend.manage_personalinfo.exception.ConflictException;
import com.homifybackend.manage_personalinfo.exception.NotFoundException;
import com.homifybackend.manage_personalinfo.repository.ManagePersonalInfoRepository;
import com.homifybackend.model.Account;
import com.homifybackend.model.Address;
import com.homifybackend.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
public class ManagePersonalInfoService {

    private final ManagePersonalInfoRepository repository;

    public ManagePersonalInfoService(ManagePersonalInfoRepository repository) {
        this.repository = repository;
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+]{8,15}$");

    public ManagePersonalInfoDTO getProfile(Long userId) {
        User user = repository.findUserById(userId);
        if (user == null) throw new NotFoundException("User not found");

        Account account = repository.findAccountByUserId(userId);
        if (account == null) throw new NotFoundException("Account not found");

        Address address = user.getAddress(); // ✅ đúng model

        return toDTO(user, account, address);
    }

    @Transactional
    public ManagePersonalInfoDTO updateProfile(Long userId, ManagePersonalInfoDTO dto) {
        User user = repository.findUserById(userId);
        if (user == null) throw new NotFoundException("User not found");

        Account account = repository.findAccountByUserId(userId);
        if (account == null) throw new NotFoundException("Account not found");

        String email = dto.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                throw new ConflictException("Invalid email format");
            }
            account.setEmail(email.trim());
        }

        String phone = dto.getPhoneNumber();
        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new ConflictException("Invalid phone number format");
            }
        }

        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAvatarUrl(dto.getAvatarUrl());

        String city = dto.getCity();
        if (city != null && !city.trim().isEmpty()) {
            Address address = user.getAddress();
            if (address == null) address = new Address();
            address.setCity(city.trim());

            Address saved = repository.saveAddress(address);
            user.setAddress(saved); // ✅ đúng model
        }

        repository.saveUser(user);
        repository.saveAccount(account);

        return getProfile(userId);
    }

    private ManagePersonalInfoDTO toDTO(User user, Account account, Address address) {
        ManagePersonalInfoDTO dto = new ManagePersonalInfoDTO();
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setEmail(account.getEmail());
        dto.setCity(address != null ? address.getCity() : null);
        return dto;
    }
}
