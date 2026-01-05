package com.homifybackend.accountSetting_myListings.dto.listing;

import lombok.*;

import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingResponse {
    private Long id;

    private String listingType;
    private String status;

    // BE trả y hệt FE structure (mềm, FE render thẳng)
    private Map<String, Object> agent;
    private Map<String, Object> pricing;
    private Map<String, Object> marketing;
    private Map<String, Object> address;
    private Map<String, Object> property;
    private Map<String, Object> media;

    private String createdAt;
    private String updatedAt;
}
