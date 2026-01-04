package com.homifybackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RentalListingDTO(
        BigDecimal monthlyRent,
        BigDecimal depositAmount,
        Integer leaseTermMonths,
        Boolean petAllowed,
        LocalDate availableFrom,
        String rentalStatus,
        LocalDateTime dateListed,
        String marketingDescription
) {
}
