package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "villas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Villa {

  @Id
  @Column(name = "property_id")
  private Long propertyId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "property_id")
  private Property property;

  @Column(name = "lot_area")
  private Double lotArea;

  @Column(name = "backyard_area")
  private Double backyardArea;

  @Column(name = "front_yard_area")
  private Double frontYardArea;

  @Column(name = "garden_area")
  private Double gardenArea;

  @Column(name = "parking_spaces")
  private Integer parkingSpaces;

  @Column(name = "has_garage")
  private Boolean hasGarage = false;

  @Column(name = "has_basement")
  private Boolean hasBasement = false;

  @Column(name = "garage_area")
  private Double garageArea;

  @Column(name = "view_type", length = 50)
  private String viewType;

  @Column(name = "smart_home_level")
  private Integer smartHomeLevel;

  @Column(name = "service_area")
  private Double serviceArea;
}