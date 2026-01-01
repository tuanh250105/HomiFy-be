package com.homifybackend.saved_searches.dto;

import java.time.LocalDateTime;

public record SavedSearchDto(
        Long id,
        String name,
        String addressText,
        String listingType,
        Boolean subscriptionsOn,
        String frequency,
        LocalDateTime createdAt
) {}
