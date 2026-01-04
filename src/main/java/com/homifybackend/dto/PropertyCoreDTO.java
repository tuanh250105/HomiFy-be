package com.homifybackend.dto;


import com.homifybackend.model.ApplianceRating;
import com.homifybackend.model.TransportRating;

public record PropertyCoreDTO(
        Long propertyId,
        String propertyType,
        Double area,
        Integer beds,
        Integer baths,
        Integer floors,
        AddressDTO address,
        Integer yearBuilt,
        String description,
        TransportRating transportRating,
        ApplianceRating applianceRating
) {}

