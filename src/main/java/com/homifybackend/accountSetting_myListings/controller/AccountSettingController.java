package com.homifybackend.accountSetting_myListings.controller;

import com.homifybackend.accountSetting_myListings.dto.account.AccountMeResponse;
import com.homifybackend.accountSetting_myListings.dto.account.ChangePasswordRequest;
import com.homifybackend.accountSetting_myListings.dto.account.UpdateAccountMeRequest;
import com.homifybackend.accountSetting_myListings.service.AccountSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Backwards-compatible endpoints (older FE) under /api/account.
 */
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountSettingController {

    private final AccountSettingService service;

    @GetMapping("/me")
    public AccountMeResponse me(@RequestHeader(value = "X-User-Id", required = false) Long uid) {
        long userId = (uid != null ? uid : 9L);
        return service.getMe(userId);
    }

    @PutMapping("/me")
    public AccountMeResponse update(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @RequestBody UpdateAccountMeRequest req
    ) {
        long userId = (uid != null ? uid : 9L);
        return service.updateMe(userId, req);
    }

    @PutMapping("/change-password")
    public void changePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @RequestBody ChangePasswordRequest req
    ) {
        long userId = (uid != null ? uid : 9L);
        service.changePassword(userId, req);
    }
}
