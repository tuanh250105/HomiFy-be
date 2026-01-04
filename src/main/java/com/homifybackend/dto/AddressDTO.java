package com.homifybackend.dto;

import com.homifybackend.model.Address;

public record AddressDTO(
         Long addressId,
         String city,
         String province,
         String street,
         Double latitude,
         Double longitude
)
    { public AddressDTO(com.homifybackend.model.Address address) {
        this(
                address.getAddressId(),
                address.getCity(),
                address.getProvince(),
                address.getStreet(),
                address.getLatitude(),
                address.getLongitude()
        );
}}
