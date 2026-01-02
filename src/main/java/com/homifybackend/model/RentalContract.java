package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "rental_contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalContract {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_listing_id", nullable = false)
  private RentalListing rentalListing;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id", nullable = false)
  private Customer tenant;

  @Column(name = "monthly_rent")
  private Double monthlyRent;

  @Column(name = "deposit_paid")
  private Double depositPaid;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(name = "signed_date")
  private LocalDate signedDate;

  @Column(name = "contract_status", length = 20)
  private String contractStatus = "ACTIVE";

  @Column(name = "payment_due_day")
  private Integer paymentDueDay;
}