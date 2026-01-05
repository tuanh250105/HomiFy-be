package com.homifybackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityFeaturesDTO {
  private Long propertyId;
  private Boolean hasSecurityDoor;
  private Boolean hasCctv;
}