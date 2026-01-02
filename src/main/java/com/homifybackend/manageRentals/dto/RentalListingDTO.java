package com.homifybackend.manageRentals.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalListingDTO {
  private Long id;
  private Long propertyId;
  private Double monthlyRent;
  private Double depositAmount;
  private Double maintenanceFee;
  private LocalDate availableFrom;
  private Integer leaseTermMonths;
  private Boolean petAllowed;
  private String utilitiesIncluded;
  private String listingStatus;
  private String marketingDescription;
  private LocalDateTime dateListed;
  private LocalDateTime dateRented;
}