package com.homifybackend.mapper;

import com.homifybackend.dto.*;
import com.homifybackend.model.*;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import org.hibernate.Hibernate;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ListingDetailMapper {

    public ListingDetailResponse toResponse(
            ListingType type,
            Property property,
            SaleListing sale,
            RentalListing rent,
            List<Room> rooms
    ) {

        PropertyCoreDTO core = mapCore(property);
        Map<String, Object> details = extractSubtypeDetails(property);
        List<RoomDTO> roomDTOs = mapRooms(rooms);

        Object listing = (type == ListingType.BUY)
                ? mapSale(sale)
                : mapRental(rent);

        return new ListingDetailResponse(
                type, core, details, roomDTOs, listing
        );
    }
//    BigDecimal currentPrice,
//    BigDecimal estimateValue,
//    String saleStatus,
//    String marketingDescription,
//    LocalDateTime dateListed,
//    List<ListingImage> images
    private SaleListingDTO mapSale(SaleListing s) {
        if (s == null) return null;

        List<ListingImageDTO> images = s.getImages() == null
                ? List.of()
                : s.getImages().stream()
                .map(img -> new ListingImageDTO(img.getId(), img.getUrl())).toList();

        return new SaleListingDTO(
                s.getCurrentPrice(),
                s.getEstimateValue(),
                s.getSaleStatus().name(),
                s.getMarketingDescription(),
                s.getDateListed(),
                images
        );
    }

//    BigDecimal monthlyRent,
//    BigDecimal depositAmount,
//    Integer leaseTermMonths,
//    Boolean petAllowed,
//    LocalDate availableFrom,
//    String rentalStatus,
//    LocalDateTime dateListed,
//    String marketingDescription
    private RentalListingDTO mapRental(RentalListing r) {
        if (r == null) return null;
        return new RentalListingDTO(
                r.getMonthlyRent(),
                r.getDepositAmount(),
                r.getLeaseTermMonths(),
                r.getPetAllowed(),
                r.getAvailableFrom(),
                r.getRentalStatus().name(),
                r.getDateListed(),
                r.getMarketingDescription()
        );
    }

    private PropertyCoreDTO mapCore(Property property) {
        if (property == null) return null;
        return new PropertyCoreDTO(
                property.getPropertyId(),
                Hibernate.getClass(property).getSimpleName().toUpperCase(),
                property.getArea(),
                property.getBeds(),
                property.getBaths(),
                property.getFloors(),
                new AddressDTO(property.getAddress()),
                property.getYearBuilt(),
                property.getDescription(),
                property.getTransportRating(),
                property.getApplianceRating()
        );
    }

    private Map<String, Object> extractSubtypeDetails(Property property) {

        Map<String, Object> details = new LinkedHashMap<>();

        if (property == null) return details;

        Class<?> clazz = Hibernate.getClass(property);

        for (Field field : clazz.getDeclaredFields()) {

            // Bỏ qua field static / synthetic
            if (field.isSynthetic()
                    || java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // Bỏ qua quan hệ JPA (tránh lộ entity + loop)
            if (field.isAnnotationPresent(OneToMany.class)
                    || field.isAnnotationPresent(ManyToOne.class)
                    || field.isAnnotationPresent(OneToOne.class)
                    || field.isAnnotationPresent(ManyToMany.class)) {
                continue;
            }

            // Bỏ qua id
            if ("propertyId".equals(field.getName())) {
                continue;
            }

            field.setAccessible(true);

            try {
                Object value = field.get(property);
                if (value != null) {
                    details.put(field.getName(), value);
                }
            } catch (IllegalAccessException ignored) {}
        }

        return details;
    }

    private List<RoomDTO> mapRooms(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) return List.of();

//        Long roomId,
//        String type,
//        Integer levels,
//        Double width,
//        Double length,
//        Double height,
//        Double area
        return rooms.stream()
                .map(r -> new RoomDTO(
                        r.getRoomId(),
                        r.getType(),
                        r.getWidth(),
                        r.getLength(),
                        r.getHeight(),
                        r.getArea()
                ))
                .toList();
    }
}
