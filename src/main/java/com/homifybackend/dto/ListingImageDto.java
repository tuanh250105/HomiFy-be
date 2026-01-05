package com.homifybackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingImageDto {
    private Long id;
    private String url;
    private Boolean isPrimary;
}
