package com.homifybackend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "bank_transactions")
public class BankTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "txn_id")
  private Long txnId;

  @Column(name = "txn_date", nullable = false)
  private LocalDate txnDate;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal amount;

  @Column(length = 10)
  private String direction = "CREDIT";

  private String description;

  private String reference;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // Constructor rỗng
  public BankTransaction() {
  }

  // Getters and Setters
  public Long getTxnId() {
    return txnId;
  }

  public void setTxnId(Long txnId) {
    this.txnId = txnId;
  }

  public LocalDate getTxnDate() {
    return txnDate;
  }

  public void setTxnDate(LocalDate txnDate) {
    this.txnDate = txnDate;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}