
package com.homifybackend.mapper;

import com.homifybackend.dto.AddressDTO;
import com.homifybackend.dto.ListingMapDTO;
import com.homifybackend.model.*;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
@Component
public class ListingMapper {

    public ListingMapDTO toMapDTO(SaleListing listing) {
        Property property = listing.getProperty();
        Address address = property.getAddress();

        String listingType = SaleListingStatus.SOLD.equals(listing.getSaleStatus())
                ? "SOLD"
                : "BUY";

        AddressDTO addressDTO = new AddressDTO(
                address.getAddressId(),
                address.getCity(),
                address.getProvince(),
                address.getStreet(),
                address.getLatitude(),
                address.getLongitude()
        );

        return ListingMapDTO.builder()
                .listingId(listing.getId()) // PK riêng của Listing
                .propertyId(listing.getProperty().getPropertyId())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .price(listing.getCurrentPrice() != null ? listing.getCurrentPrice().doubleValue() : null)
                .listingType(listingType)
                .propertyType(property.getPropertyType())
                .address(addressDTO)
                .build();
    }

    public ListingMapDTO toMapDTO(RentalListing listing) {
        Property property = listing.getProperty();
        Address address = property.getAddress();

        AddressDTO addressDTO = new AddressDTO(
                address.getAddressId(),
                address.getCity(),
                address.getProvince(),
                address.getStreet(),
                address.getLatitude(),
                address.getLongitude()
        );

        return ListingMapDTO.builder()
                .listingId(listing.getId()) // PK riêng của Listing
                .propertyId(listing.getProperty().getPropertyId())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .price(listing.getMonthlyRent() != null ? listing.getMonthlyRent().doubleValue() : null)
                .listingType("RENT")
                .propertyType(property.getPropertyType())
                .address(addressDTO)
                .build();
    }
}
