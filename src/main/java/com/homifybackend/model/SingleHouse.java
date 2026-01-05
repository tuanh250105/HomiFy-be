package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "single_houses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("SINGLE_HOUSE")
public class SingleHouse extends Property{

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