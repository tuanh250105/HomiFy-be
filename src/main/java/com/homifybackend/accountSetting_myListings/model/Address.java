package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "address_id")
  private Long addressId;

  @Column(name = "zip_code", length = 20)
  private String zipCode;

  @Column(name = "city", length = 100)
  private String city;

  @Column(name = "province", length = 100)
  private String province;

  @Column(name = "street", length = 255)
  private String street;

  @Column(name = "nation", length = 100)
  private String nation;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;
}
