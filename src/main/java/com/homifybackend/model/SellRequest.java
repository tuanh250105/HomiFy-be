package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sell_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private Customer owner;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id")
  private Address address;

  private Integer estBeds;
  private Integer estBaths;
  private Integer floors;
  private Boolean hasGarage = false;
  private Boolean hasBasement = false;

  @Column(length = 50)
  private String livingRoomCondition;

  @Column(length = 50)
  private String kitchenCondition;

  @Column(length = 50)
  private String interiorCondition;

  @Column(length = 50)
  private String exteriorCondition;

  private Double estimatedArea;

  @Column(columnDefinition = "TEXT")
  private String neededRepairNotes;

  @Column(length = 20)
  private SellRequestStatus status = SellRequestStatus.PENDING;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;
}