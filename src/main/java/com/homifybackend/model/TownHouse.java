package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "town_houses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("TOWN_HOUSE")
public class TownHouse  extends Property{

  @Column(name = "land_area")
  private Double landArea;

  @Column(name = "number_of_floors")
  private Integer numberOfFloors;

  @Column(name = "corner_lot")
  private Boolean cornerLot = false;

  @Column(name = "front_width")
  private Double frontWidth;

  private Double depth;

  @Column(name = "car_accessible")
  private Boolean carAccessible = false;

  @Column(name = "cctv_installed")
  private Boolean cctvInstalled = false;

  @Column(name = "maintenance_fee")
  private Double maintenanceFee;

  @Column(name = "clubhouse_access")
  private Boolean clubhouseAccess = false;

  @Column(name = "pool_access")
  private Boolean poolAccess = false;

  @Column(name = "gym_access")
  private Boolean gymAccess = false;

  @Column(name = "green_space")
  private Boolean greenSpace = false;

  @Column(name = "road_width")
  private Double roadWidth;
}