package com.homifybackend.mapper;

import com.homifybackend.model.Tour;
import com.homifybackend.model.SaleListing;

public class TourMapper {
    public static TourDTOForFE toDTOForFE(Tour tour) {
        if (tour == null) return null;

        TourDTOForFE dto = new TourDTOForFE();

        dto.setBuyer(tour.getBuyer() != null ? tour.getBuyer() : "N/A");

        if (tour.getSaleListing() != null && tour.getSaleListing().getProperty() != null) {
            SaleListing sale = tour.getSaleListing();
            dto.setProperty(sale.getProperty().getPropertyType() != null
                    ? sale.getProperty().getPropertyType()
                    : "N/A");
        } else {
            dto.setProperty("N/A");
        }

        dto.setDate(tour.getDate() != null ? tour.getDate() : "N/A");
        dto.setStatus(tour.getStatus() != null ? tour.getStatus() : "N/A");

        return dto;
    }

    public static Tour toEntityFromFE(TourDTOForFE dto) {
        if (dto == null) return null;

        Tour tour = new Tour();
        tour.setBuyer(dto.getBuyer() != null ? dto.getBuyer() : "");
        tour.setDate(dto.getDate() != null ? dto.getDate() : "");
        tour.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        return tour;
    }

    public static class TourDTOForFE {
        private String buyer;
        private String property;
        private String date;
        private String status;

        public String getBuyer() { return buyer; }
        public void setBuyer(String buyer) { this.buyer = buyer; }

        public String getProperty() { return property; }
        public void setProperty(String property) { this.property = property; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
