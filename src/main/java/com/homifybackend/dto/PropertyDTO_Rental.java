package com.homifybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;



import lombok.*;
        import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO_Rental {
    private Long propertyId;
    private Long ownerId;
    private Long addressId;
    private Integer yearBuilt;
    private Integer floors;
    private Integer beds;
    private Integer baths;
    private Double area;
    private String description;
    private Long transportRatingId;
    private Long applianceRatingId;
    private LocalDateTime createdAt;
    private String propertyType;
}