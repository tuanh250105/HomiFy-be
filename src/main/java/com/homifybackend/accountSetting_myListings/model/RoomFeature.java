package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomFeature {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feature_id")
  private Long featureId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @Column(length = 100)
  private String name;

  @Column(length = 255)
  private String value;
}