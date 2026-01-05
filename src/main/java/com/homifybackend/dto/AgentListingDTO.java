package com.homifybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentListingDTO {
    private Long listingId;
    private Long propertyId;
    private String status;
    private BigDecimal price;  // ← BigDecimal not Double!
    private LocalDateTime dateListed;
    private Integer beds;      // ← Integer not int!
    private Double baths;
    private Double area;
    private String description;
    private String street;
    private String city;
    private String province;
    private String zipCode;
    private Double latitude;
    private Double longitude;
    private String primaryImageUrl;
}