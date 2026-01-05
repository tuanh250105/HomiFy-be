package com.homifybackend.manageRentalPayments.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalPaymentScheduleDTO {

  private Long scheduleId;
  private Long rentalContractId;
  private LocalDate periodMonth;
  private LocalDate dueDate;
  private BigDecimal amountDue;
  private String status;
  // Xóa paidAt vì không có trong DB
  private Long matchedTxnId;
  private LocalDateTime matchedAt;  // Dùng cái này làm thời điểm thanh toán
  private String note;

  // UI fields cho frontend
  private String tenantName;
  private String propertyAddress;
  private Long propertyId;

  // Constructor rỗng
  public RentalPaymentScheduleDTO() {
  }

  // Getters and Setters
  public Long getScheduleId() {
    return scheduleId;
  }

  public void setScheduleId(Long scheduleId) {
    this.scheduleId = scheduleId;
  }

  public Long getRentalContractId() {
    return rentalContractId;
  }

  public void setRentalContractId(Long rentalContractId) {
    this.rentalContractId = rentalContractId;
  }

  public LocalDate getPeriodMonth() {
    return periodMonth;
  }

  public void setPeriodMonth(LocalDate periodMonth) {
    this.periodMonth = periodMonth;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public BigDecimal getAmountDue() {
    return amountDue;
  }

  public void setAmountDue(BigDecimal amountDue) {
    this.amountDue = amountDue;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getMatchedTxnId() {
    return matchedTxnId;
  }

  public void setMatchedTxnId(Long matchedTxnId) {
    this.matchedTxnId = matchedTxnId;
  }

  public LocalDateTime getMatchedAt() {
    return matchedAt;
  }

  public void setMatchedAt(LocalDateTime matchedAt) {
    this.matchedAt = matchedAt;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getTenantName() {
    return tenantName;
  }

  public void setTenantName(String tenantName) {
    this.tenantName = tenantName;
  }

  public String getPropertyAddress() {
    return propertyAddress;
  }

  public void setPropertyAddress(String propertyAddress) {
    this.propertyAddress = propertyAddress;
  }

  public Long getPropertyId() {
    return propertyId;
  }

  public void setPropertyId(Long propertyId) {
    this.propertyId = propertyId;
  }
}