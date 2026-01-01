package com.homifybackend.explore_options.controller;

import com.homifybackend.explore_options.dto.SellRequestCreateDTO;
import com.homifybackend.explore_options.dto.SellRequestResponseDTO;
import com.homifybackend.explore_options.service.ExploreOptionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/explore-options")
public class ExploreOptionsController {

    private final ExploreOptionsService exploreOptionsService;

    public ExploreOptionsController(ExploreOptionsService exploreOptionsService) {
        this.exploreOptionsService = exploreOptionsService;
    }

    // Create Sell Request from FE payload
    @PostMapping("/sell-requests")
    public ResponseEntity<?> create(@RequestBody SellRequestCreateDTO dto) {
        try {
            return ResponseEntity.ok(exploreOptionsService.createSellRequest(dto));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of(
                    "message", e.getMessage() == null ? e.getClass().getName() : e.getMessage()
            ));
        }
    }


    @GetMapping("/sell-requests/{id}")
    public ResponseEntity<SellRequestResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(exploreOptionsService.getSellRequest(id));
    }

    // List by owner (customer user_id)
    @GetMapping("/sell-requests")
    public ResponseEntity<List<SellRequestResponseDTO>> listByOwner(@RequestParam Long ownerId) {
        return ResponseEntity.ok(exploreOptionsService.listByOwner(ownerId));
    }
}
