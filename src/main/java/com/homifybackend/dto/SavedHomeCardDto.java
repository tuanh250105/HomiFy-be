package com.homifybackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SavedHomeCardDto(
        Long id,                 // propertyId
        Long listingId,          // sale_listings.id
        String listingType,      // BUY | RENT
        String title,
        String address,
        BigDecimal price,
        String status,
        Integer beds,
        Integer baths,
        Double sqft,
        String img,
        String type,             // For sale / For rent
        LocalDateTime addedAt
) {}
