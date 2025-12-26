package com.homifybackend.service;

import com.homifybackend.dto.ListingMapDTO;
import com.homifybackend.mapper.ListingMapper;
import com.homifybackend.mapper.PropertyFilterType;
import com.homifybackend.model.Property;
import com.homifybackend.repository.RentalListingReposity;
import com.homifybackend.repository.SaleListingReposity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingService {
    private final SaleListingReposity saleListingReposity;
    private final RentalListingReposity rentalListingReposity;
    private final  ListingMapper listingMapper;
    public ListingService(SaleListingReposity saleListingReposity, RentalListingReposity rentalListingReposity, ListingMapper listingMapper) {
        this.saleListingReposity = saleListingReposity;
        this.rentalListingReposity = rentalListingReposity;
        this.listingMapper = listingMapper;
    }

    public List<ListingMapDTO> loadListing(double minLat, double maxLat, double minLng, double maxLng, Integer zoom, String category, Double minPrice, Double maxPrice, String propertyType, String keyword){
        Class<? extends Property> propertyClass = PropertyFilterType.from(propertyType);

        return switch (category.toUpperCase()) {
            case "BUY" ->
                    saleListingReposity.findByMapArea(minLat, maxLat, minLng, maxLng, minPrice, maxPrice, propertyClass, "ACTIVE").stream()
                            .map(listingMapper::toMapDTO)
                            .toList();
            case "SOLD" ->
                    saleListingReposity.findByMapArea(minLat, maxLat, minLng, maxLng, minPrice, maxPrice, propertyClass, "SOLD").stream()
                            .map(listingMapper::toMapDTO)
                            .toList();
            case "RENT" ->
                    rentalListingReposity.findByMapArea(minLat, maxLat, minLng, maxLng, minPrice, maxPrice, propertyClass, "ACTIVE").stream()
                            .map(listingMapper::toMapDTO)
                            .toList();

            default -> throw new IllegalArgumentException("Invalid category");
        };
    }
}
