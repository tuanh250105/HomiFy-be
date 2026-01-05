package com.homifybackend.accountSetting_myListings.dto.listing;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingImageDto {
    private Long id;
    private String url;
    private Boolean isPrimary;
}
