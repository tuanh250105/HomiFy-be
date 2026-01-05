package com.homifybackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalListingImageDTO {
  private Long id;
  private Long rentalListingId;
  private String url;
  private Boolean isPrimary;
}