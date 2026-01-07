package com.homifybackend.accountSetting_myListings.controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.homifybackend.dto.AccountMeResponse;
import com.homifybackend.dto.ChangePasswordRequest;
import com.homifybackend.dto.UpdateAccountMeRequest;
import com.homifybackend.accountSetting_myListings.service.AccountSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountSettingController {

    private final AccountSettingService service;

    @GetMapping("/me")
    public AccountMeResponse me(
            @RequestHeader(value = "X-User-Id", required = false) Long uid) {

        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        return service.getMe(uid);
    }

    @PutMapping("/me")
    public AccountMeResponse update(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @RequestBody UpdateAccountMeRequest req) {

        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        return service.updateMe(uid, req);
    }

    @PutMapping("/change-password")
    public void changePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @RequestBody ChangePasswordRequest req) {

        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        service.changePassword(uid, req);
    }
}
