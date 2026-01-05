package com.homifybackend.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TodayTaskDto(
    long id,
    String type,
    String status,

    @JsonProperty("requested_date") String requestedDate,
    @JsonProperty("time_slot") String timeSlot,
    @JsonProperty("scheduled_at") String scheduledAt,
    @JsonProperty("sale_listing_id") Long saleListingId,
    @JsonProperty("sell_request_id") Long sellRequestId,
    
    String address,
    String district
) {}
