package com.homifybackend.service.PropertyService;

import com.homifybackend.model.SaleListing;
import com.homifybackend.repository.SaleListingReposity;
import org.springframework.stereotype.Service;

@Service
public class SaleListingService {
    private final SaleListingReposity saleListingReposity;

    public SaleListingService(SaleListingReposity saleListingReposity) {
        this.saleListingReposity = saleListingReposity;
    }

    public SaleListing findById(Long id) {
        return saleListingReposity.findById(id).orElse(null);
    }
}
