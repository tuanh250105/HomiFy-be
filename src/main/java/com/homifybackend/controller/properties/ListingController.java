package com.homifybackend.controller.properties;

import com.homifybackend.model.SaleListing;
import com.homifybackend.service.ListingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
    private final ListingService listingService;

    public ListingController(ListingService listingService, ListingService listingService1) {
        this.listingService = listingService1;
    }

    @GetMapping
    public List<SaleListing> loadListing(@RequestParam double minLat,
                                         @RequestParam double maxLat,
                                         @RequestParam double minLng,
                                         @RequestParam double maxLng,
                                         @RequestParam Integer zoom,
                                         @RequestParam String category,
                                         @RequestParam(required = false) float minPrice,
                                         @RequestParam(required = false) float maxPrice,
                                         @RequestParam String propertyType,
                                         @RequestParam String keyword) {

        return listingService.loadListing(minLat, maxLat,minLng, maxLng,  zoom,  category,  minPrice, maxPrice,  propertyType,  keyword);
    }
}
