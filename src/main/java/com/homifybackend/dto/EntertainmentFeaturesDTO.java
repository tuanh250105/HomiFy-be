package com.homifybackend.dto;

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