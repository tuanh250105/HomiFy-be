package com.homifybackend.controller.properties;

import com.homifybackend.dto.ListingDetailResponse;
import com.homifybackend.dto.ListingMapDTO;
import com.homifybackend.dto.ListingType;
import com.homifybackend.service.PropertyService.ListingService;
import com.homifybackend.service.PropertyService.RentalListingService;
import com.homifybackend.service.PropertyService.SaleListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
    private final ListingService listingService;


    public ListingController(ListingService listingService1, SaleListingService saleListingService, RentalListingService rentalListingService) {
        this.listingService = listingService1;
    }

    @GetMapping
    public List<ListingMapDTO> loadListing(@RequestParam Double minLat,
                                           @RequestParam Double maxLat,
                                           @RequestParam Double minLng,
                                           @RequestParam Double maxLng,
                                           @RequestParam Integer zoom,
                                           @RequestParam String category,
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
