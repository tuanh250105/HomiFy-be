package com.homifybackend.dto;

import java.util.List;
import java.util.Map;

public record ListingDetailResponse(
            ListingType type,
            PropertyCoreDTO property,
            Map<String, Object> details,
            List<RoomDTO> rooms,
            Object listing
)
{ }
