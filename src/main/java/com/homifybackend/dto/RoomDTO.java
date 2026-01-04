package com.homifybackend.dto;

public record RoomDTO(
        Long roomId,
        String type,
        Double width,
        Double length,
        Double height,
        Double area
        )
{ }
