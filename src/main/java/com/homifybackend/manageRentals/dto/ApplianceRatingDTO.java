package com.homifybackend.manageRentals.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplianceRatingDTO {
  private Long ratingId;
  private Boolean dishwasher;
  private Boolean dryer;
  private Boolean microwave;
  private Boolean oven;
  private Boolean refrigerator;
  private Boolean washer;
}