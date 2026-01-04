package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_entertainment_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntertainmentFeatures {

  @Id
  @Column(name = "property_id")
  private Long propertyId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "property_id")
  private Property property;

  @Column(name = "has_movie_cinema")
  private Boolean hasMovieCinema = false;

  @Column(name = "has_home_gym")
  private Boolean hasHomeGym = false;

  @Column(name = "has_game_room")
  private Boolean hasGameRoom = false;
}