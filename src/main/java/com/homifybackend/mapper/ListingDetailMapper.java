package com.homifybackend.mapper;

import com.homifybackend.dto.*;
import com.homifybackend.model.*;
import jakarta.persistence.*;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(readOnly = true)
public class ListingDetailMapper {

    public ListingDetailResponse toResponse(
            ListingType type,
            Property property,
            SaleListing sale,
            RentalListing rent
    ) {

        PropertyCoreDTO core = mapCore(property);
        Map<String, Object> details = extractSubtypeDetails(property);
        List<RoomDTO> roomDTOs = mapRooms(property.getRooms());


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
        Map<String, Object> details = new HashMap<>();

        if (property instanceof Apartment apartment) {

            details.put("usableArea", apartment.getUsableArea());
            details.put("maintenanceFee", apartment.getMaintenanceFee());
            details.put("level", apartment.getLevel());
            details.put("hasElevatorAccess", apartment.getHasElevatorAccess());
            details.put("petAllowed", apartment.getPetAllowed());
            details.put("sharedFacilities", apartment.getSharedFacilities());
            details.put("totalBuildingFloors", apartment.getTotalBuildingFloors());
            details.put("balcony", apartment.getBalcony());

        } else if (property instanceof SingleHouse singleHouse) {

            details.put("landArea", singleHouse.getLandArea());
            details.put("backyardArea", singleHouse.getBackyardArea());
            details.put("frontYardArea", singleHouse.getFrontYardArea());
            details.put("hasGarage", singleHouse.getHasGarage());
            details.put("hasBasement", singleHouse.getHasBasement());

        } else if (property instanceof Villa villa) {

            details.put("lotArea", villa.getLotArea());
            details.put("backyardArea", villa.getBackyardArea());
            details.put("frontYardArea", villa.getFrontYardArea());
            details.put("gardenArea", villa.getGardenArea());
            details.put("parkingSpaces", villa.getParkingSpaces());
            details.put("hasGarage", villa.getHasGarage());
            details.put("hasBasement", villa.getHasBasement());
            details.put("garageArea", villa.getGarageArea());
            details.put("viewType", villa.getViewType());
            details.put("smartHomeLevel", villa.getSmartHomeLevel());
            details.put("serviceArea", villa.getServiceArea());

        } else if (property instanceof TownHouse townHouse) {

            details.put("landArea", townHouse.getLandArea());
            details.put("numberOfFloors", townHouse.getNumberOfFloors());
            details.put("cornerLot", townHouse.getCornerLot());
            details.put("frontWidth", townHouse.getFrontWidth());
            details.put("depth", townHouse.getDepth());
            details.put("carAccessible", townHouse.getCarAccessible());
            details.put("cctvInstalled", townHouse.getCctvInstalled());
            details.put("maintenanceFee", townHouse.getMaintenanceFee());
            details.put("clubhouseAccess", townHouse.getClubhouseAccess());
            details.put("poolAccess", townHouse.getPoolAccess());
            details.put("gymAccess", townHouse.getGymAccess());
            details.put("greenSpace", townHouse.getGreenSpace());
            details.put("roadWidth", townHouse.getRoadWidth());
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
