package com.homifybackend.manageRentals.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDTO {
  private Long propertyId;
  private Double usableArea;
  private Double maintenanceFee;
  private Integer level;
  private Boolean hasElevatorAccess;
  private Boolean petAllowed;
  private Boolean sharedFacilities;
  private Integer totalBuildingFloors;
  private Boolean balcony;
}