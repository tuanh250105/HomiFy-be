package com.homifybackend.accountSetting_myListings.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sale_listing_daily_stats")
@IdClass(SaleListingDailyStatsId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleListingDailyStats {
    
    @Id
    @Column(name = "sale_listing_id")
    private Long saleListingId;

    @Id
    @Column(name = "stat_date")
    private LocalDate statDate;

    private Integer views = 0;
    private Integer saves = 0;
}