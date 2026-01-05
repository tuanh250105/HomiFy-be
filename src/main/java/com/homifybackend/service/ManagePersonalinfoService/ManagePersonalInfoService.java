package com.homifybackend.manage_personalinfo.service;

import com.homifybackend.manage_personalinfo.dto.ManagePersonalInfoDTO;
import com.homifybackend.manage_personalinfo.exception.ConflictException;
import com.homifybackend.manage_personalinfo.exception.NotFoundException;
import com.homifybackend.manage_personalinfo.repository.ManagePersonalInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
public class ManagePersonalInfoService {

    private final ManagePersonalInfoRepository repo;

    public ManagePersonalInfoService(ManagePersonalInfoRepository repo) {
        this.repo = repo;
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public ManagePersonalInfoDTO getProfile(Long userId) {
        if (!repo.userExists(userId)) throw new NotFoundException("User not found");

        Object[] row = repo.getProfileRow(userId);
        if (row == null) throw new NotFoundException("User not found");

        return mapRow(row);
    }

    @Transactional
    public ManagePersonalInfoDTO updateProfile(Long userId, ManagePersonalInfoDTO dto) {
        if (!repo.userExists(userId)) throw new NotFoundException("User not found");

        // ===== email =====
        String email = dto.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            String e = email.trim();
            if (!EMAIL_PATTERN.matcher(e).matches()) {
                throw new ConflictException("Invalid email format");
            }
            if (repo.emailExistsForOtherUser(e, userId)) {
                throw new ConflictException("Email already in use");
            }
            if (!repo.accountExists(userId)) {
                // vì schema accounts cần password/username, không auto-create ở đây để khỏi ảnh hưởng module khác
                throw new NotFoundException("Account not found");
            }
            repo.updateAccountEmail(userId, e);
        }

        // ===== update users =====
        repo.updateUsers(
                userId,
                dto.getFullName(),
                dto.getPhoneNumber(),
                dto.getAvatarUrl()
        );

        // ===== city -> addresses =====
        String city = dto.getCity();
        if (city != null && !city.trim().isEmpty()) {
            Object[] current = repo.getProfileRow(userId);
            Long addressId = current != null && current[3] != null ? ((Number) current[3]).longValue() : null;

            if (addressId == null) {
                Long newId = repo.insertAddressReturnId(city.trim());
                if (newId != null) repo.updateUserAddressId(userId, newId);
            } else {
                repo.updateAddressCity(addressId, city.trim());
            }
        }

        return getProfile(userId);
    }

    private ManagePersonalInfoDTO mapRow(Object[] row) {
        // row: full_name, phone_number, avatar_url, address_id, email, city
        ManagePersonalInfoDTO dto = new ManagePersonalInfoDTO();
        dto.setFullName(row[0] != null ? row[0].toString() : null);
        dto.setPhoneNumber(row[1] != null ? row[1].toString() : null);
        dto.setAvatarUrl(row[2] != null ? row[2].toString() : null);
        dto.setEmail(row[4] != null ? row[4].toString() : null);
        dto.setCity(row[5] != null ? row[5].toString() : null);
        return dto;
    }
}
