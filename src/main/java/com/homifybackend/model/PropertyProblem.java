package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "property_problems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyProblem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "problem_id")
  private Long problemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id", nullable = false)
  private User reporter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sale_listing_id")
  private SaleListing saleListing;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_listing_id")
  private RentalListing rentalListing;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "problem_type", length = 50)
  private String problemType;

  @CreationTimestamp
  @Column(name = "date_reported")
  private LocalDateTime dateReported;
}