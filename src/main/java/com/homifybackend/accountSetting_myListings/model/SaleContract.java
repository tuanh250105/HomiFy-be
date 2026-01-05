package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sale_contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleContract {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "listing_id", nullable = false)
  private SaleListing saleListing;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id", nullable = false)
  private Customer buyer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id", nullable = false)
  private Customer seller;

  @Column(precision = 18, scale = 2)
  private BigDecimal price;

  @Column(name = "contract_date")
  private LocalDate contractDate = LocalDate.now();

  @Column(name = "close_date")
  private LocalDate closeDate;

  @Column(name = "loan_date")
  private LocalDate loanDate;
}