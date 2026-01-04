package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "listing_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListingImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "listing_id", nullable = false)
  private SaleListing saleListing;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String url;

  @Column(name = "is_primary")
  private Boolean isPrimary = false;
}