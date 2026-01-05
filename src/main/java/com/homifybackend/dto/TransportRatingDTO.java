package com.homifybackend.dto;

import com.homifybackend.model.TransportRating;

public record TransportRatingDTO(Integer walkScore,
                                 Integer bikeScore,
                                 Integer transitScore) {
    public static TransportRatingDTO from (TransportRating t){
        if (t == null) return null;
        return new TransportRatingDTO(t.getWalkScore(), t.getBikeScore(), t.getTransitScore());
    }
}
