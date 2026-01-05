package com.homifybackend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rental_contracts")
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
  private BigDecimal monthlyRent;

  @Column(name = "deposit_paid")
  private BigDecimal depositPaid;

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

  // Constructor rỗng (bắt buộc cho JPA)
  public RentalContract() {
  }

  // ================== GETTERS AND SETTERS THỦ CÔNG ==================

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public RentalListing getRentalListing() {
    return rentalListing;
  }

  public void setRentalListing(RentalListing rentalListing) {
    this.rentalListing = rentalListing;
  }

  public Customer getTenant() {
    return tenant;
  }

  public void setTenant(Customer tenant) {
    this.tenant = tenant;
  }

  public BigDecimal getMonthlyRent() {
    return monthlyRent;
  }

  public void setMonthlyRent(BigDecimal monthlyRent) {
    this.monthlyRent = monthlyRent;
  }

  public BigDecimal getDepositPaid() {
    return depositPaid;
  }

  public void setDepositPaid(BigDecimal depositPaid) {
    this.depositPaid = depositPaid;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public LocalDate getSignedDate() {
    return signedDate;
  }

  public void setSignedDate(LocalDate signedDate) {
    this.signedDate = signedDate;
  }

  public String getContractStatus() {
    return contractStatus;
  }

  public void setContractStatus(String contractStatus) {
    this.contractStatus = contractStatus;
  }

  public Integer getPaymentDueDay() {
    return paymentDueDay;
  }

  public void setPaymentDueDay(Integer paymentDueDay) {
    this.paymentDueDay = paymentDueDay;
  }

  // Thêm getter cho tenantId và rentalListingId (dùng trong mapper)
  public Long getTenantId() {
    return tenant != null ? tenant.getUserId() : null; // giả sử Customer có getUserId()
  }

  public Long getRentalListingId() {
    return rentalListing != null ? rentalListing.getProperty().getPropertyId() : null;
  }
}