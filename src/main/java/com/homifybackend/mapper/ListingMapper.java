package com.homifybackend.mapper;

import com.homifybackend.dto.AddressDTO;
import com.homifybackend.dto.ListingMapDTO;
import com.homifybackend.model.*;
import org.springframework.stereotype.Component;

@Component
public class ListingMapper {

    public ListingMapDTO toMapDTO(SaleListing listing) {

        Property property = listing.getProperty();
        Address address = property.getAddress();

        String listingType =
                "SOLD".equalsIgnoreCase(String.valueOf(listing.getSaleStatus()))
                        ? "SOLD"
                        : "BUY";

        return new ListingMapDTO(
                listing.getId(),
                listingType,
                listing.getCurrentPrice(),
                property.getClass().getSimpleName().toUpperCase(),
                new AddressDTO(
                        address.getCity(),
                        address.getProvince(),
                        address.getStreet(),
                        address.getLatitude(),
                        address.getLongitude()
                )
        );
    }

    public ListingMapDTO toMapDTO(RentalListing listing) {

        Property property = listing.getProperty();
        Address address = property.getAddress();

        return new ListingMapDTO(
                listing.getId(),
                "RENT",
                listing.getMonthlyRent(),
                property.getClass().getSimpleName().toUpperCase(),
                new AddressDTO(
                        address.getCity(),
                        address.getProvince(),
                        address.getStreet(),
                        address.getLatitude(),
                        address.getLongitude()
                )
        );
    }
}

