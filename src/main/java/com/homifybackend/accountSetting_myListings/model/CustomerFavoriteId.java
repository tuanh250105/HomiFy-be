package com.homifybackend.accountSetting_myListings.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CustomerFavoriteId implements Serializable {
    private Long customerId;
    private Long propertyId;
}