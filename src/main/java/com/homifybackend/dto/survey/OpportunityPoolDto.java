package com.homifybackend.dto.survey;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpportunityPoolDto(
        @JsonProperty("id") Long id,
        @JsonProperty("status") String status,
        @JsonProperty("created_at") String createdAt,

        @JsonProperty("address_id") Long addressId,
        @JsonProperty("est_beds") Integer estBeds,
        @JsonProperty("est_baths") Integer estBaths,
        @JsonProperty("estimated_area") Double estimatedArea,

        @JsonProperty("full_address") String fullAddress,
        @JsonProperty("city") String city,
        @JsonProperty("province") String province,

        @JsonProperty("owner_name") String ownerName
) {}
