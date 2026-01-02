package com.homifybackend.accountSetting_myListings.controller;

import com.homifybackend.accountSetting_myListings.dto.listing.ListingRequest;
import com.homifybackend.accountSetting_myListings.dto.listing.ListingResponse;
import com.homifybackend.accountSetting_myListings.service.AgentListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent/listings")
@RequiredArgsConstructor
public class AgentListingController {

    private final AgentListingService service;

    @GetMapping
    public List<ListingResponse> getMyListings() {
        return service.getMyListings();
    }

    @PostMapping
    public ListingResponse create(@RequestBody ListingRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ListingResponse update(@PathVariable Long id, @RequestBody ListingRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/duplicate")
    public ListingResponse duplicate(@PathVariable Long id) {
        return service.duplicate(id);
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(@PathVariable Long id, @RequestParam String status) {
        service.changeStatus(id, status);
    }
}
