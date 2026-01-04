package com.homifybackend.service.PropertyService;

import com.homifybackend.model.RentalListing;
import com.homifybackend.repository.RentalListingReposity;
import org.springframework.stereotype.Service;

@Service
public class RentalListingService {
    private final RentalListingReposity rentalListingReposity;

    public RentalListingService(RentalListingReposity rentalListingReposity) {
        this.rentalListingReposity = rentalListingReposity;
    }

    public RentalListing findById(Long id) {
        return  rentalListingReposity.findById(id).orElse(null);
    }
}
