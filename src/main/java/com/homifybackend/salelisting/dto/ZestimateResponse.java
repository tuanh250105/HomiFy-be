package com.homifybackend.salelisting.dto;

import java.math.BigDecimal;

public class ZestimateResponse {
    private BigDecimal zestimate;
    private Double confidence;
    private PriceRange priceRange;

    // Constructors
    public ZestimateResponse() {}

    public ZestimateResponse(BigDecimal zestimate, Double confidence, PriceRange priceRange) {
        this.zestimate = zestimate;
        this.confidence = confidence;
        this.priceRange = priceRange;
    }

    // Getters and Setters
    public BigDecimal getZestimate() {
        return zestimate;
    }

    public void setZestimate(BigDecimal zestimate) {
        this.zestimate = zestimate;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public PriceRange getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(PriceRange priceRange) {
        this.priceRange = priceRange;
    }

    // Inner class for price range
    public static class PriceRange {
        private BigDecimal min;
        private BigDecimal max;

        public PriceRange() {}

        public PriceRange(BigDecimal min, BigDecimal max) {
            this.min = min;
            this.max = max;
        }

        public BigDecimal getMin() {
            return min;
        }

        public void setMin(BigDecimal min) {
            this.min = min;
        }

        public BigDecimal getMax() {
            return max;
        }

        public void setMax(BigDecimal max) {
            this.max = max;
        }
    }
}
