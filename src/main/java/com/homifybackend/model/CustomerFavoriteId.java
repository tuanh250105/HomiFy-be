package com.homifybackend.model;

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