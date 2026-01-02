package com.homifybackend.agentsforcustomer.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "Reviewer ID is required")
    private Long reviewerId;

    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @NotBlank(message = "Review title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Review content is required")
    @Size(min = 20, max = 2000, message = "Content must be between 20 and 2000 characters")
    private String content;

    // Individual ratings (optional but validated if provided)
    @Min(value = 0, message = "Responsiveness rating must be at least 0")
    @Max(value = 5, message = "Responsiveness rating must not exceed 5")
    private Integer responsiveness;

    @Min(value = 0, message = "Local knowledge rating must be at least 0")
    @Max(value = 5, message = "Local knowledge rating must not exceed 5")
    private Integer localKnowledge;

    @Min(value = 0, message = "Negotiation skills rating must be at least 0")
    @Max(value = 5, message = "Negotiation skills rating must not exceed 5")
    private Integer negotiationSkills;

    @Min(value = 0, message = "Professionalism rating must be at least 0")
    @Max(value = 5, message = "Professionalism rating must not exceed 5")
    private Integer professionalism;

    // Image URLs (optional)
    @Size(max = 10, message = "Maximum 10 images allowed")
    private String[] imageUrls;
}