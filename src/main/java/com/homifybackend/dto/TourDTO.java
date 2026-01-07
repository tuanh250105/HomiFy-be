package com.homifybackend.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TourDTO {
    private String date;
    private String time;
    private String message;

    public TourDTO() {
    }

}