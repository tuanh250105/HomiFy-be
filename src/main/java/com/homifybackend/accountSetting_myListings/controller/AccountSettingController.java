package com.homifybackend.accountSetting_myListings.controller;

import com.homifybackend.accountSetting_myListings.dto.account.AccountMeResponse;
import com.homifybackend.accountSetting_myListings.dto.account.ChangePasswordRequest;
import com.homifybackend.accountSetting_myListings.dto.account.UpdateAccountMeRequest;
import com.homifybackend.accountSetting_myListings.service.AccountSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountSettingController {

    private final AccountSettingService service;

    @GetMapping("/me")
    public AccountMeResponse me() {
        return service.getMe();
    }

    @PutMapping("/me")
    public AccountMeResponse update(@RequestBody UpdateAccountMeRequest req) {
        return service.updateMe(req);
    }

    @PutMapping("/change-password")
    public void changePassword(@RequestBody ChangePasswordRequest req) {
        service.changePassword(req);
    }
}
