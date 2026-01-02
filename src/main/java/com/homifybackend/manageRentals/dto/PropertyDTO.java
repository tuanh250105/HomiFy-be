package com.homifybackend.manageRentals.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {
  private Long propertyId;
  private Long ownerId;
  private Long addressId;
  private Integer yearBuilt;
  private Integer floors;
  private Integer beds;
  private Integer baths;
  private Double area;
  private String description;
  private Long transportRatingId;
  private Long applianceRatingId;
  private LocalDateTime createdAt;
}