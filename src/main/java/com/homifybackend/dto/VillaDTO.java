package com.homifybackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VillaDTO {
  private Long propertyId;
  private Double lotArea;
  private Double backyardArea;
  private Double frontYardArea;
  private Double gardenArea;
  private Integer parkingSpaces;
  private Boolean hasGarage;
  private Boolean hasBasement;
  private Double garageArea;
  private String viewType;
  private Integer smartHomeLevel;
  private Double serviceArea;
}