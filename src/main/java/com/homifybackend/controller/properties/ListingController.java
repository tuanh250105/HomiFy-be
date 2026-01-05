package com.homifybackend.controller.properties;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.homifybackend.dto.ListingDetailResponse;
import com.homifybackend.dto.ListingMapDTO;
import com.homifybackend.dto.ListingType;
import com.homifybackend.service.PropertyService.ListingService;
import com.homifybackend.service.PropertyService.RentalListingService;
import com.homifybackend.service.PropertyService.SaleListingService;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
    private final ListingService listingService;
    private final SaleListingService saleListingService;
    private final RentalListingService rentalListingService;

    public ListingController(ListingService listingService1, SaleListingService saleListingService, RentalListingService rentalListingService) {
        this.listingService = listingService1;
        this.saleListingService = saleListingService;
        this.rentalListingService = rentalListingService;
    }

    @GetMapping
    public List<ListingMapDTO> loadListing(@RequestParam(required = false) Double minLat,
                                           @RequestParam(required = false) Double maxLat,
                                           @RequestParam(required = false) Double minLng,
                                           @RequestParam(required = false) Double maxLng,
                                           @RequestParam(required = false, defaultValue = "10") Integer zoom,
                                           @RequestParam(required = false, defaultValue = "BUY") String category,
                                           @RequestParam(required = false) Double minPrice,
                                           @RequestParam(required = false) Double maxPrice,
                                           @RequestParam(required = false) String propertyType,
                                           @RequestParam(required = false) String keyword) {

        return listingService.loadListing(minLat, maxLat,minLng, maxLng,  zoom,  category,  minPrice, maxPrice,  propertyType,  keyword);
    }

    @GetMapping("/{type}/{propertyId}")
    public ResponseEntity<ListingDetailResponse> getListingDetail(
            @PathVariable ListingType type,
            @PathVariable Long propertyId
    ) {
        return ResponseEntity.ok(
                listingService.getListingDetail(type, propertyId)
        );
    }


}
