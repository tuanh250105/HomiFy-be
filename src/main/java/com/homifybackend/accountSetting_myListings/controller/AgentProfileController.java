package com.homifybackend.accountSetting_myListings.controller;

import com.homifybackend.dto.AccountMeResponse;
import com.homifybackend.dto.ListingRequest;
import com.homifybackend.accountSetting_myListings.service.AccountSettingService;
import com.homifybackend.accountSetting_myListings.service.AgentProfileService;
import com.homifybackend.dto.ListingResponse_Duy;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/agent-profile")
@RequiredArgsConstructor
public class AgentProfileController {

    private final AgentProfileService service;
    private final AccountSettingService accountSettingService;

    @GetMapping()
    public AccountMeResponse getProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long uid) {

        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        return accountSettingService.getMe(uid);
    }

    @GetMapping("/listings")
    public List<ListingResponse_Duy> getMyListings(
            @RequestHeader(value = "X-User-Id", required = false) Long uid) {

        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        return service.getMyListings(uid);
    }

    @PostMapping("/listings")
    public ListingResponse_Duy create(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @RequestBody ListingRequest req) {

        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        if (req.getListingType() == null) {
            req.setListingType("SALE");
        }

        return service.create(uid, req);
    }

    @PutMapping("/listings/{id}")
    public ListingResponse_Duy update(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @PathVariable Long id,
            @RequestBody ListingRequest req) {

        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        return service.update(uid, id, req);
    }

    @DeleteMapping("/listings/{id}")
    public void delete(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @PathVariable Long id) {

        if (uid == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        service.delete(id);
    }

    @PatchMapping("/listings/{id}/status")
    public void changeStatus(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @PathVariable Long id,
            @RequestParam String status) {

        if (uid == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        service.changeStatus(id, status);
    }

}