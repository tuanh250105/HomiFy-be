
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

//        Long addressId,
//        String city,
//        String province,
//        String street,
//        Double latitude,
//        Double longitude
        AddressDTO addressDTO = new AddressDTO(
                address.getAddressId(),
                address.getCity(),
                address.getProvince(),
                address.getStreet(),
                address.getLatitude(),
                address.getLongitude()
        );

        // Sử dụng Builder hoặc constructor 7 tham số đầy đủ
        return ListingMapDTO.builder()
                .listingId(listing.getProperty().getPropertyId())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .price(listing.getCurrentPrice() != null ? listing.getCurrentPrice().doubleValue() : null)
                .listingType(listingType)
                .propertyType(
                        Hibernate.getClass(property)
                                .getSimpleName()
                                .toUpperCase()
                )
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
                .listingId(listing.getProperty().getPropertyId())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .price(listing.getMonthlyRent() != null ? listing.getMonthlyRent().doubleValue() : null)
                .listingType("RENT")
                .propertyType(
                        Hibernate.getClass(property)
                                .getSimpleName()
                                .toUpperCase()
                )
                .address(addressDTO)
                .build();
    }
}