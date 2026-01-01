package com.homifybackend.saved_searches.dto;

public record UpdateSavedSearchRequest(
        String name,
        String addressText,
        String listingType,
        Boolean subscriptionsOn,
        String frequency
) {}
