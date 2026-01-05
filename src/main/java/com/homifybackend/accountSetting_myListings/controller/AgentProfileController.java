package com.homifybackend.accountSetting_myListings.controller;

import com.homifybackend.dto.AccountMeResponse;
import com.homifybackend.dto.ListingRequest;
import com.homifybackend.accountSetting_myListings.service.AccountSettingService;
import com.homifybackend.accountSetting_myListings.service.AgentProfileService;
import com.homifybackend.dto.ListingResponse_Duy;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent-profile")
@RequiredArgsConstructor
public class AgentProfileController {

    private final AgentProfileService service;
    private final AccountSettingService accountSettingService;

    /**
     * Get agent profile
     */
    @GetMapping()
    public AccountMeResponse getProfile(@RequestHeader(value = "X-User-Id", required = false) Long uid) {
        long agentId = (uid != null ? uid : 9L);
        return accountSettingService.getMe(agentId);
    }

    /**
     * Option B: returns only SALE listings for the agent (schema has agent_id only
     * on sale_listings).
     */
    @GetMapping("/listings")
    public List<ListingResponse_Duy> getMyListings(@RequestHeader(value = "X-User-Id", required = false) Long uid) {
        long agentId = (uid != null ? uid : 9L);
        return service.getMyListings(agentId);
    }

    /**
     * Create SALE listing.
     * FE should provide property.ownerId (customer user_id) and address fields.
     */
    @PostMapping("/listings")
    public ListingResponse_Duy create(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @RequestBody ListingRequest req) {
        long agentId = (uid != null ? uid : 9L);
        // Force SALE for current screen
        if (req.getListingType() == null)
            req.setListingType("SALE");
        return service.create(agentId, req);
    }

    @PutMapping("/listings/{id}")
    public ListingResponse_Duy update(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @PathVariable Long id,
            @RequestBody ListingRequest req) {
        long agentId = (uid != null ? uid : 9L);
        return service.update(agentId, id, req);
    }

    @DeleteMapping("/listings/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/listings/{id}/status")
    public void changeStatus(@PathVariable Long id, @RequestParam String status) {
        service.changeStatus(id, status);
    }
}
