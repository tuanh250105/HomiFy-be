package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apartment {

  @Id
  @Column(name = "property_id")
  private Long propertyId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "property_id")
  private Property property;

  @Column(name = "usable_area")
  private Double usableArea;

  @Column(name = "maintenance_fee")
  private Double maintenanceFee;

  private Integer level;

  @Column(name = "has_elevator_access")
  private Boolean hasElevatorAccess = false;

  @Column(name = "pet_allowed")
  private Boolean petAllowed = false;

  @Column(name = "shared_facilities")
  private Boolean sharedFacilities = false;

  @Column(name = "total_building_floors")
  private Integer totalBuildingFloors;

  private Boolean balcony = false;
}