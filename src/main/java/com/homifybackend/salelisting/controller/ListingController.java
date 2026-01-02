package com.homifybackend.salelisting.controller;

import com.homifybackend.salelisting.dto.CreateDraftListingRequest;
import com.homifybackend.salelisting.dto.CreateDraftListingResponse;
import com.homifybackend.salelisting.dto.ListingResponse;
import com.homifybackend.salelisting.dto.UpdateListingRequest;
import com.homifybackend.salelisting.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
    
    @Autowired
    private ListingService listingService;
    
    /**
     * POST /api/listings/draft
     * Tạo draft listing (step 1-2 wizard)
     */
    @PostMapping("/draft")
    public ResponseEntity<CreateDraftListingResponse> createDraft(@Valid @RequestBody CreateDraftListingRequest request) {
        CreateDraftListingResponse response = listingService.createDraft(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * PUT /api/listings/{id}
     * Update wizard data (step 3-7)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateListing(@PathVariable Long id, @RequestBody UpdateListingRequest request) {
        listingService.updateListing(id, request);
        return ResponseEntity.ok().build();
    }
    
    /**
     * GET /api/listings
     * Lấy danh sách tất cả listings (có filter)
     */
    @GetMapping("")
    public ResponseEntity<List<ListingResponse>> getAllListings(
        @RequestParam(required = false) Long agentId,
        @RequestParam(required = false) Long ownerId,
        @RequestParam(required = false) String status
    ) {
        List<ListingResponse> listings = listingService.getAllListings(agentId, ownerId, status);
        return ResponseEntity.ok(listings);
    }
    
    /**
     * GET /api/listings/{id}
     * Lấy lại data để edit
     */
    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getListing(@PathVariable Long id) {
        ListingResponse response = listingService.getListing(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PATCH /api/listings/{id}/submit
     * Publish listing (DRAFT → ACTIVE)
     */
    @PatchMapping("/{id}/submit")
    public ResponseEntity<Void> submitListing(@PathVariable Long id) {
        listingService.submitListing(id);
        return ResponseEntity.ok().build();
    }
}
