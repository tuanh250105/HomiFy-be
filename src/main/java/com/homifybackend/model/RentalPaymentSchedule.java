package com.homifybackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_payment_schedules")
public class RentalPaymentSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "schedule_id")
  private Long scheduleId;

  @Column(name = "rental_contract_id", nullable = false)
  private Long rentalContractId;

  // ✅ JOIN DB bằng FK rental_contract_id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_contract_id", referencedColumnName = "id", insertable = false, updatable = false)
  private RentalContract rentalContract;

  @Column(name = "period_month", nullable = false)
  private LocalDate periodMonth;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "amount_due", nullable = false, precision = 18, scale = 2)
  private BigDecimal amountDue;

  @Column(nullable = false, length = 20)
  private String status = "PENDING";

  @Column(name = "matched_txn_id")
  private Long matchedTxnId;

  @Column(name = "matched_at")
  private LocalDateTime matchedAt;

  @Column(name = "note")
  private String note;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public RentalPaymentSchedule() {}

  // ===== getters/setters =====
  public Long getScheduleId() { return scheduleId; }
  public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

  public Long getRentalContractId() { return rentalContractId; }
  public void setRentalContractId(Long rentalContractId) { this.rentalContractId = rentalContractId; }

  public RentalContract getRentalContract() { return rentalContract; }
  public void setRentalContract(RentalContract rentalContract) { this.rentalContract = rentalContract; }

  public LocalDate getPeriodMonth() { return periodMonth; }
  public void setPeriodMonth(LocalDate periodMonth) { this.periodMonth = periodMonth; }

  public LocalDate getDueDate() { return dueDate; }
  public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

  public BigDecimal getAmountDue() { return amountDue; }
  public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  public Long getMatchedTxnId() { return matchedTxnId; }
  public void setMatchedTxnId(Long matchedTxnId) { this.matchedTxnId = matchedTxnId; }

  public LocalDateTime getMatchedAt() { return matchedAt; }
  public void setMatchedAt(LocalDateTime matchedAt) { this.matchedAt = matchedAt; }

  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
