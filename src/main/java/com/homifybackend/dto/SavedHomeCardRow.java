package com.homifybackend.saved_homes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SavedHomeCardRow {
    Long getId();
    Long getListingId();
    String getListingType();
    String getTitle();
    String getAddress();
    BigDecimal getPrice();
    String getStatus();
    Integer getBeds();
    Integer getBaths();
    Double getSqft();
    String getImg();
    String getType();
    LocalDateTime getAddedAt();
}
