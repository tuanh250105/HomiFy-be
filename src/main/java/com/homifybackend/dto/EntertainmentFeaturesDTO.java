package com.homifybackend.manageRentals.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntertainmentFeaturesDTO {
  private Long propertyId;
  private Boolean hasMovieCinema;
  private Boolean hasHomeGym;
  private Boolean hasGameRoom;
}