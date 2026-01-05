package com.homifybackend.manageRentals.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TownHouseDTO {
  private Long propertyId;
  private Double landArea;
  private Integer numberOfFloors;
  private Boolean cornerLot;
  private Double frontWidth;
  private Double depth;
  private Boolean carAccessible;
  private Boolean cctvInstalled;
  private Double maintenanceFee;
  private Boolean clubhouseAccess;
  private Boolean poolAccess;
  private Boolean gymAccess;
  private Boolean greenSpace;
  private Double roadWidth;
}