package com.homifybackend.manageRentals.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
  private Long addressId;
  private String zipCode;
  private String city;
  private String province;
  private String street;
  private String nation;
  private Double latitude;
  private Double longitude;
}