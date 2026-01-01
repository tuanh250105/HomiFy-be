package com.homifybackend.saved_searches.dto;

public record CreateSavedSearchRequest(
        String name,
        String addressText,
        String listingType,
        Boolean subscriptionsOn,
        String frequency
) {}
