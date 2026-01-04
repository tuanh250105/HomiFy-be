package com.homifybackend.service.PropertyService;

import com.homifybackend.dto.ListingDetailResponse;
import com.homifybackend.dto.ListingMapDTO;
import com.homifybackend.dto.ListingType;
import com.homifybackend.mapper.ListingDetailMapper;
import com.homifybackend.mapper.ListingMapper;
import com.homifybackend.mapper.PropertyFilterType;
import com.homifybackend.model.*;
import com.homifybackend.repository.PropertyReposity;
import com.homifybackend.repository.RentalListingReposity;
import com.homifybackend.repository.RoomReposity;
import com.homifybackend.repository.SaleListingReposity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListingService {
    private final PropertyReposity propertyReposity;
    private final RoomReposity roomReposity;
    private final SaleListingReposity saleListingReposity;
    private final RentalListingReposity rentalListingReposity;
    private final  ListingMapper listingMapper;
    private final ListingDetailMapper listingDetailMapper;
    public ListingService(PropertyReposity propertyReposity, RoomReposity roomReposity, SaleListingReposity saleListingReposity, RentalListingReposity rentalListingReposity, ListingMapper listingMapper, ListingDetailMapper listingDetailMapper) {
        this.propertyReposity = propertyReposity;
        this.roomReposity = roomReposity;
        this.saleListingReposity = saleListingReposity;
        this.rentalListingReposity = rentalListingReposity;
        this.listingMapper = listingMapper;
        this.listingDetailMapper = listingDetailMapper;
    }

    public List<ListingMapDTO> loadListing(
            Double minLat, Double maxLat, Double minLng, Double maxLng, Integer zoom,
            String category, Double minPrice, Double maxPrice, String propertyType, String keyword) {

        String propertyType1 = (propertyType == null || propertyType.isBlank())
                ? null
                : propertyType.toUpperCase();

        return switch (category.toUpperCase()) {
            case "BUY" -> saleListingReposity.findByMapArea(
                            minLat, maxLat, minLng, maxLng, minPrice, maxPrice,
                            SaleListingStatus.ACTIVE, propertyType1
                    ).stream()
                    .map(listingMapper::toMapDTO)
                    .toList();

            case "SOLD" -> saleListingReposity.findByMapArea(
                            minLat, maxLat, minLng, maxLng, minPrice, maxPrice,
                            SaleListingStatus.SOLD, propertyType1
                    ).stream()
                    .map(listingMapper::toMapDTO)
                    .toList();

            case "RENT" -> rentalListingReposity.findByMapArea(
                            minLat, maxLat, minLng, maxLng, minPrice, maxPrice,
                            RentalListingStatus.ACTIVE, propertyType1
                    ).stream()
                    .map(listingMapper::toMapDTO)
                    .toList();

            default -> throw new IllegalArgumentException("Invalid category");
        };
    }


    public ListingDetailResponse getListingDetail(ListingType type, Long propertyId) {

        // 1️⃣ Property
        Property property = propertyReposity.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));


        SaleListing sale = null;
        RentalListing rent = null;

        if (type == ListingType.BUY) {
            sale = saleListingReposity
                    .findByProperty_PropertyId(propertyId)
                    .orElseThrow(() -> new RuntimeException("Sale listing not found"));
        }

        if (type == ListingType.RENT) {
            rent = rentalListingReposity
                    .findByProperty_PropertyId(propertyId)
                    .orElseThrow(() -> new RuntimeException("Rental listing not found"));
        }

        // 4️⃣ Map response
        return listingDetailMapper.toResponse(
                type,
                property,
                sale,
                rent
        );
    }


}
