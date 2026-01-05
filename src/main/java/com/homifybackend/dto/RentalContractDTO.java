package com.homifybackend.manageRentals.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalContractDTO {
  private Long id;
  private Long rentalListingId;
  private Long tenantId;
  private BigDecimal monthlyRent;      // DB: monthly_rent
  private BigDecimal depositPaid;      // DB: deposit_paid
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDate signedDate;    // DB: signed_date
  private String contractStatus;   // DB: contract_status (ACTIVE, EXPIRED...)
  private Integer paymentDueDay;   // DB: payment_due_day (Ngày đóng tiền hàng tháng)
}