package com.homifybackend.manageRentals.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransportRatingDTO {
  private Long ratingId;
  private Integer walkScore;
  private Integer bikeScore;
  private Integer transitScore;
}