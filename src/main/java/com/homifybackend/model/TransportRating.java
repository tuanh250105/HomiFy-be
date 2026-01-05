package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transport_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportRating {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "rating_id")
  private Long ratingId;

  @Column(name = "walk_score")
  private Integer walkScore;

  @Column(name = "bike_score")
  private Integer bikeScore;

  @Column(name = "transit_score")
  private Integer transitScore;
}