package com.homifybackend.dto;

import com.homifybackend.model.ApplianceRating;

public record ApplianceRatingDTO(
        Boolean dishwasher,
        Boolean dryer,
        Boolean microwave,
        Boolean oven,
        Boolean refrigerator,
        Boolean washer) {
    public static ApplianceRatingDTO from(ApplianceRating a) {
        if (a == null) return null;
        return new ApplianceRatingDTO(
                a.getDishwasher(),
                a.getDryer(),
                a.getMicrowave(),
                a.getOven(),
                a.getRefrigerator(),
                a.getWasher()
        );
    }
}



