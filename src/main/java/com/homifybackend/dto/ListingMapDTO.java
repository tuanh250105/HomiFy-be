package com.homifybackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ListingMapDTO {

    private Long listingId;
    private Long propertyId;
    private Double latitude;
    private Double longitude;

    private Double price;
    private String listingType;

    private String propertyType;
    private AddressDTO address;

}
