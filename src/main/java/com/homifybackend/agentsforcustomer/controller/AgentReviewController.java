package com.homifybackend.agentsforcustomer.controller;

import com.homifybackend.agentsforcustomer.dto.AgentReviewDTO;
import com.homifybackend.agentsforcustomer.dto.CreateReviewRequest;
import com.homifybackend.agentsforcustomer.service.AgentReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
// ✅ REMOVED @CrossOrigin - using global CORS config in SecurityConfig
public class AgentReviewController {

    private final AgentReviewService reviewService;

    @PostMapping("/{agentId}/reviews")
    public ResponseEntity<Map<String, Object>> createReview(
            @PathVariable Long agentId,
            @Valid @RequestBody CreateReviewRequest request) {
        try {
            log.info("POST /api/agents/{}/reviews - Creating review by reviewer {}",
                    agentId, request.getReviewerId());

            AgentReviewDTO review = reviewService.createReview(agentId, request);
            log.info("Review created successfully with ID: {}", review.getReviewId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success", true,
                            "message", "Review submitted successfully. It will be visible after approval.",
                            "data", review
                    ));
        } catch (Exception e) {
            log.error("Error creating review for agent {}", agentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error creating review: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/{agentId}/reviews")
    public ResponseEntity<Map<String, Object>> getReviewsByAgent(
            @PathVariable Long agentId,
            @RequestParam(required = false, defaultValue = "false") boolean includeAll) {
        try {
            log.info("GET /api/agents/{}/reviews - includeAll: {}", agentId, includeAll);

            List<AgentReviewDTO> reviews;
            if (includeAll) {
                log.debug("Fetching all reviews (including pending/rejected)");
                reviews = reviewService.getReviewsByAgentId(agentId);
            } else {
                log.debug("Fetching approved reviews only");
                reviews = reviewService.getApprovedReviewsByAgentId(agentId);
            }

            Double avgRating = reviewService.getAverageRating(agentId);
            Long reviewCount = reviewService.getReviewCountByAgentId(agentId);

            log.info("Found {} reviews for agent {} (avg rating: {})",
                    reviews.size(), agentId, avgRating);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reviews retrieved successfully",
                    "data", reviews,
                    "total", reviews.size(),
                    "averageRating", avgRating,
                    "approvedCount", reviewCount
            ));
        } catch (Exception e) {
            log.error("Error fetching reviews for agent {}", agentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching reviews: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<Map<String, Object>> getReviewById(@PathVariable Long reviewId) {
        try {
            log.info("GET /api/agents/reviews/{}", reviewId);

            return reviewService.getReviewById(reviewId)
                    .map(review -> {
                        log.debug("Review found: {}", review.getReviewId());
                        return ResponseEntity.ok(Map.of(
                                "success", true,
                                "message", "Review found",
                                "data", review
                        ));
                    })
                    .orElseGet(() -> {
                        log.warn("Review not found with ID: {}", reviewId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                        "success", false,
                                        "message", "Review not found"
                                ));
                    });
        } catch (Exception e) {
            log.error("Error fetching review {}", reviewId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error fetching review: " + e.getMessage()
                    ));
        }
    }

    @PutMapping("/reviews/{reviewId}/approve")
    public ResponseEntity<Map<String, Object>> approveReview(
            @PathVariable Long reviewId,
            @RequestParam(required = false) Long approvedBy) {
        try {
            log.info("PUT /api/agents/reviews/{}/approve - approvedBy: {}", reviewId, approvedBy);

            AgentReviewDTO review = reviewService.approveReview(reviewId, approvedBy);
            log.info("Review approved successfully");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Review approved",
                    "data", review
            ));
        } catch (Exception e) {
            log.error("Error approving review {}", reviewId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error approving review: " + e.getMessage()
                    ));
        }
    }

    @PutMapping("/reviews/{reviewId}/reject")
    public ResponseEntity<Map<String, Object>> rejectReview(
            @PathVariable Long reviewId,
            @RequestParam(required = false) Long approvedBy) {
        try {
            log.info("PUT /api/agents/reviews/{}/reject - approvedBy: {}", reviewId, approvedBy);

            AgentReviewDTO review = reviewService.rejectReview(reviewId, approvedBy);
            log.info("Review rejected successfully");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Review rejected",
                    "data", review
            ));
        } catch (Exception e) {
            log.error("Error rejecting review {}", reviewId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error rejecting review: " + e.getMessage()
                    ));
        }
    }
}