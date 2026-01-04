package com.homifybackend.dto;

import com.homifybackend.model.ListingImage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleListingDTO(
        BigDecimal currentPrice,
        BigDecimal estimateValue,
        String saleStatus,
        String marketingDescription,
        LocalDateTime dateListed,
        List<ListingImageDTO> images
) {
}
