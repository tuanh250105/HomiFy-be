package com.homifybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


import lombok.*;
        import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalListingDTO_Rental {
    private Long id;
    private Long propertyId;
    private BigDecimal monthlyRent;
    private BigDecimal depositAmount;
    private BigDecimal maintenanceFee;
    private LocalDate availableFrom;
    private Integer leaseTermMonths;
    private Boolean petAllowed;
    private String utilitiesIncluded;
    private String listingStatus;
    private String marketingDescription;
    private LocalDateTime dateListed;
    private LocalDateTime dateRented;
}