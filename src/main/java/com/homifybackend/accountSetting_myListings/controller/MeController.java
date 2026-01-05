package com.homifybackend.accountSetting_myListings.controller;

import com.homifybackend.dto.AccountMeResponse;
import com.homifybackend.dto.ChangePasswordRequest;
import com.homifybackend.dto.UpdateAccountMeRequest;
import com.homifybackend.accountSetting_myListings.service.AccountSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Canonical Account Settings endpoints used by the new FE.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeController {

    private final AccountSettingService service;

    @GetMapping("/me")
    public AccountMeResponse me(@RequestHeader(value = "X-User-Id", required = false) Long uid) {
        long userId = (uid != null ? uid : 9L);
        return service.getMe(userId);
    }

    @PutMapping("/me")
    public AccountMeResponse update(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @RequestBody UpdateAccountMeRequest req) {
        long userId = (uid != null ? uid : 9L);
        return service.updateMe(userId, req);
    }

    @PutMapping("/me/password")
    public void changePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @RequestBody ChangePasswordRequest req) {
        long userId = (uid != null ? uid : 9L);
        service.changePassword(userId, req);
    }
}
