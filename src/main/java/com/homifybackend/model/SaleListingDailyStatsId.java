package com.homifybackend.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class SaleListingDailyStatsId implements Serializable {
    private Long saleListingId;
    private LocalDate statDate;

    public SaleListingDailyStatsId() {}

    public SaleListingDailyStatsId(Long saleListingId, LocalDate statDate) {
        this.saleListingId = saleListingId;
        this.statDate = statDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleListingDailyStatsId that = (SaleListingDailyStatsId) o;
        return Objects.equals(saleListingId, that.saleListingId) &&
               Objects.equals(statDate, that.statDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleListingId, statDate);
    }
}