package com.homifybackend.controller.salelisting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.homifybackend.dto.CreateDraftListingRequest;
import com.homifybackend.dto.CreateDraftListingResponse;
import com.homifybackend.dto.ListingResponse;
import com.homifybackend.dto.UpdateListingRequest;
import com.homifybackend.service.salelisting.ListingService;

import jakarta.validation.Valid;

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
        // Auto-assign ownerId and agentId if not provided (temporary for development)
        // TODO: Replace with authentication.getPrincipal() when JWT is implemented
        if (request.getOwnerId() == null) {
            request.setOwnerId(1L); // Default test customer
        }
        if (request.getAgentId() == null) {
            request.setAgentId(2L); // Default test agent
        }
        
        CreateDraftListingResponse response = listingService.createDraft(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * PUT /api/listings/{id}
     * Update wizard data (step 3-7)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateListing(@PathVariable String id, @RequestBody UpdateListingRequest request) {
        try {
            Long listingId = Long.parseLong(id);
            listingService.updateListing(listingId, request);
            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
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
    public ResponseEntity<ListingResponse> getListing(@PathVariable String id) {
        try {
            Long listingId = Long.parseLong(id);
            ListingResponse response = listingService.getListing(listingId);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * PATCH /api/listings/{id}/submit
     * Publish listing (DRAFT → ACTIVE)
     */
    @PatchMapping("/{id}/submit")
    public ResponseEntity<Void> submitListing(@PathVariable String id) {
        try {
            Long listingId = Long.parseLong(id);
            listingService.submitListing(listingId);
            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
