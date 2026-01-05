package com.homifybackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleHouseDTO {
  private Long propertyId;
  private Double landArea;
  private Double backyardArea;
  private Double frontYardArea;
  private Boolean hasGarage;
  private Boolean hasBasement;
}
