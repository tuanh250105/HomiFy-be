package com.homifybackend.dto;

import com.homifybackend.model.Address;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListingMapDTO {

    private Long listingId;
    private Double latitude;
    private Double longitude;

    private Double price;
    private String listingType;

    private String propertyType;
    private Address address;

    public ListingMapDTO(Long id, String listingType, BigDecimal currentPrice, String upperCase, AddressDTO addressDTO) {

    }
}
