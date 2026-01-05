package com.homifybackend.accountSetting_myListings.dto.listing;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingRequest {

    private String listingType; // SALE | RENT
    private String status;      // Draft | Active | Under Contract | Sold/Rented

    private AgentDto agent;

    private PricingDto pricing;
    private MarketingDto marketing;
    private AddressDto address;
    private PropertyDto property;
    private MediaDto media;

    // FE có thể gửi extra fields -> giữ mềm bằng Map nếu muốn
    private Map<String, Object> extra;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AgentDto {
        private Long agentId;
        private String name;
        private String org;
        private Double rate;
        private Integer reviews;
        private String avatarUrl;
        private String bannerUrl;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PricingDto {
        // SALE
        private BigDecimal currentPrice;
        private BigDecimal estimateValue;
        private String currency;

        // RENT
        private BigDecimal monthlyRent;
        private BigDecimal depositAmount;
        private BigDecimal maintenanceFee;
        private String availableFrom;   // yyyy-mm-dd
        private Integer leaseTermMonths;
        private Boolean petAllowed;
        private String utilitiesIncluded;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MarketingDto {
        private String title;
        private String marketingDescription;
        private List<String> highlights;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AddressDto {
        private Long addressId;
        private String street;
        private String city;
        private String province;
        private String nation;
        private String zipCode;
        private Double latitude;
        private Double longitude;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PropertyDto {
        private Long propertyId;
        private Long ownerId;

        private String propertyType; // VILLA | APARTMENT | TOWN_HOUSE | SINGLE_HOUSE
        private Integer yearBuilt;
        private Integer floors;
        private Integer beds;
        private Integer baths;
        private Double area;
        private String description;

        private TransportDto transport;
        private AppliancesDto appliances;
        private FeaturesDto features;

        private Map<String, Object> subtype;
        private List<Map<String, Object>> rooms;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TransportDto {
        private Integer walkScore;
        private Integer bikeScore;
        private Integer transitScore;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AppliancesDto {
        private Boolean dishwasher;
        private Boolean dryer;
        private Boolean microwave;
        private Boolean oven;
        private Boolean refrigerator;
        private Boolean washer;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FeaturesDto {
        private Map<String, Object> security;
        private Map<String, Object> outdoor;
        private Map<String, Object> entertainment;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MediaDto {
        private List<ListingImageDto> images;
        private String floorPlanUrl;
        private String videoUrl;
    }
}
