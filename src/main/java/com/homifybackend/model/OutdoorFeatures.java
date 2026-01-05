package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_outdoor_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutdoorFeatures {

  @Id
  @Column(name = "property_id")
  private Long propertyId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "property_id")
  private Property property;

  @Column(name = "has_swimming_pool")
  private Boolean hasSwimmingPool = false;

  @Column(name = "has_childrens_playground")
  private Boolean hasChildrensPlayground = false;
}