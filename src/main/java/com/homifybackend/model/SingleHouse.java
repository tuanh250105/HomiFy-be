package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "single_houses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleHouse {

  @Id
  @Column(name = "property_id")
  private Long propertyId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "property_id")
  private Property property;

  @Column(name = "land_area")
  private Double landArea;

  @Column(name = "backyard_area")
  private Double backyardArea;

  @Column(name = "front_yard_area")
  private Double frontYardArea;

  @Column(name = "has_garage")
  private Boolean hasGarage = false;

  @Column(name = "has_basement")
  private Boolean hasBasement = false;
}