package com.homifybackend.accountSetting_myListings.controller;

import com.homifybackend.accountSetting_myListings.dto.listing.ListingRequest;
import com.homifybackend.accountSetting_myListings.dto.listing.ListingResponse;
import com.homifybackend.accountSetting_myListings.service.AgentListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agent/listings")
@RequiredArgsConstructor
public class AgentListingController {

    private final AgentListingService service;

    /**
     * Option B: returns only SALE listings for the agent (schema has agent_id only
     * on sale_listings).
     */
    @GetMapping
    public List<ListingResponse> getMyListings(@RequestHeader(value = "X-User-Id", required = false) Long uid) {
        long agentId = (uid != null ? uid : 9L);
        return service.getMyListings(agentId);
    }

    /**
     * Create SALE listing.
     * FE should provide property.ownerId (customer user_id) and address fields.
     */
    @PostMapping
    public ListingResponse create(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @RequestBody ListingRequest req) {
        long agentId = (uid != null ? uid : 9L);
        // Force SALE for current screen
        if (req.getListingType() == null)
            req.setListingType("SALE");
        return service.create(agentId, req);
    }

    @PutMapping("/{id}")
    public ListingResponse update(
            @RequestHeader(value = "X-User-Id", required = false) Long uid,
            @PathVariable Long id,
            @RequestBody ListingRequest req) {
        long agentId = (uid != null ? uid : 9L);
        return service.update(agentId, id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(@PathVariable Long id, @RequestParam String status) {
        service.changeStatus(id, status);
    }
}
