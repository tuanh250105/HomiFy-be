package com.homifybackend.manageRentals.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutdoorFeaturesDTO {
  private Long propertyId;
  private Boolean hasSwimmingPool;
  private Boolean hasChildrensPlayground;
}