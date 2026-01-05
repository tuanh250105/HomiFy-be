package com.homifybackend.dto;

import com.homifybackend.model.Address;

public record AddressDTO(
         Long addressId,
         String city,
         String province,
         String nation,
         String street,
         Double latitude,
         Double longitude,
         String zipCode)
{
    public static AddressDTO from(Address address) {
        return new AddressDTO(
                address.getAddressId(),
                address.getCity(),
                address.getProvince(),
                address.getNation(),
                address.getStreet(),
                address.getLatitude(),
                address.getLongitude(),
                address.getZipCode()
        );
}}
