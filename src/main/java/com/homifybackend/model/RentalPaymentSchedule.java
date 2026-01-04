package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_payment_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"rentalContract"}) // Tránh vòng lặp khi in log
public class RentalPaymentSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "schedule_id")
  private Long scheduleId;

  @Column(name = "rental_contract_id", nullable = false)
  private Long rentalContractId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "rental_contract_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false
  )
  private RentalContract rentalContract;

  @Column(name = "period_month", nullable = false)
  private LocalDate periodMonth;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "amount_due", nullable = false, precision = 18, scale = 2)
  private BigDecimal amountDue;

  @Column(length = 20, nullable = false)
  private String status = "PENDING";

  @Column(name = "matched_txn_id")
  private Long matchedTxnId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "matched_txn_id", insertable = false, updatable = false)
  private BankTransaction matchedTransaction;

  @Column(name = "matched_at")
  private LocalDateTime matchedAt;

  @Column(columnDefinition = "TEXT")
  private String note;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}