package com.homifybackend.accountSetting_myListings.service;

import com.homifybackend.accountSetting_myListings.dto.listing.ListingRequest;
import com.homifybackend.accountSetting_myListings.dto.listing.ListingResponse;
import com.homifybackend.accountSetting_myListings.exception.NotFoundException;
import com.homifybackend.accountSetting_myListings.mapper.ListingMapper;
import com.homifybackend.accountSetting_myListings.model.RentalListing;
import com.homifybackend.accountSetting_myListings.model.SaleListing;
import com.homifybackend.accountSetting_myListings.repository.CustomerRepository;
import com.homifybackend.accountSetting_myListings.repository.RentalListingRepository;
import com.homifybackend.accountSetting_myListings.repository.SaleListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentListingService {

    private final SaleListingRepository saleRepo;
    private final RentalListingRepository rentRepo;
    private final CustomerRepository customerRepo;

    private void validateOwnerIsCustomer(ListingRequest req) {
        Long ownerId = req.getProperty() != null ? req.getProperty().getOwnerId() : null;
        if (ownerId == null) {
            throw new IllegalArgumentException("property.ownerId is required (must be customers.user_id)");
        }
        if (!customerRepo.existsById(ownerId)) {
            throw new IllegalArgumentException("Invalid ownerId=" + ownerId + " (not found in customers)");
        }
    }

    public List<ListingResponse> getMyListings() {
        List<ListingResponse> out = new ArrayList<>();
        saleRepo.findAll().forEach(s -> out.add(ListingMapper.toResponseFromSale(s)));
        rentRepo.findAll().forEach(r -> out.add(ListingMapper.toResponseFromRent(r)));
        return out;
    }

    public ListingResponse create(ListingRequest req) {
        validateOwnerIsCustomer(req);

        if ("RENT".equalsIgnoreCase(req.getListingType())) {
            RentalListing r = ListingMapper.applyToRent(new RentalListing(), req);
            // ✅ set agentId for rent too (bạn thiếu)
            r.setAgentId(req.getAgent() != null ? req.getAgent().getAgentId() : null);
            r = rentRepo.save(r);
            return ListingMapper.toResponseFromRent(r);
        } else {
            SaleListing s = ListingMapper.applyToSale(new SaleListing(), req);
            s = saleRepo.save(s);
            return ListingMapper.toResponseFromSale(s);
        }
    }

    public ListingResponse update(Long id, ListingRequest req) {
        validateOwnerIsCustomer(req);

        if ("RENT".equalsIgnoreCase(req.getListingType())) {
            RentalListing r = rentRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Rental listing not found: " + id));
            r = ListingMapper.applyToRent(r, req);
            r.setAgentId(req.getAgent() != null ? req.getAgent().getAgentId() : null);
            r = rentRepo.save(r);
            return ListingMapper.toResponseFromRent(r);
        } else {
            SaleListing s = saleRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Sale listing not found: " + id));
            s = ListingMapper.applyToSale(s, req);
            s = saleRepo.save(s);
            return ListingMapper.toResponseFromSale(s);
        }
    }

    public void delete(Long id) {
        if (saleRepo.existsById(id)) {
            saleRepo.deleteById(id);
            return;
        }
        if (rentRepo.existsById(id)) {
            rentRepo.deleteById(id);
            return;
        }
        throw new NotFoundException("Listing not found: " + id);
    }

    public ListingResponse duplicate(Long id) {
        if (saleRepo.existsById(id)) {
            SaleListing s = saleRepo.findById(id).orElseThrow();
            SaleListing copy = SaleListing.builder()
                    .agentId(s.getAgentId())
                    .property(s.getProperty())
                    .currentPrice(s.getCurrentPrice())
                    .estimateValue(s.getEstimateValue())
                    .saleStatus("Draft")
                    .marketingDescription(s.getMarketingDescription())
                    .dateListed(s.getDateListed())
                    .build();
            copy = saleRepo.save(copy);
            return ListingMapper.toResponseFromSale(copy);
        }

        RentalListing r = rentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found: " + id));
        RentalListing copy = RentalListing.builder()
                .agentId(r.getAgentId())
                .property(r.getProperty())
                .monthlyRent(r.getMonthlyRent())
                .depositAmount(r.getDepositAmount())
                .maintenanceFee(r.getMaintenanceFee())
                .availableFrom(r.getAvailableFrom())
                .leaseTermMonths(r.getLeaseTermMonths())
                .petAllowed(r.getPetAllowed())
                .utilitiesIncluded(r.getUtilitiesIncluded())
                .listingStatus("Draft")
                .marketingDescription(r.getMarketingDescription())
                .dateListed(r.getDateListed())
                .build();
        copy = rentRepo.save(copy);
        return ListingMapper.toResponseFromRent(copy);
    }

    public void changeStatus(Long id, String status) {
        if (saleRepo.existsById(id)) {
            SaleListing s = saleRepo.findById(id).orElseThrow();
            s.setSaleStatus(status);
            saleRepo.save(s);
            return;
        }
        if (rentRepo.existsById(id)) {
            RentalListing r = rentRepo.findById(id).orElseThrow();
            r.setListingStatus(status);
            rentRepo.save(r);
            return;
        }
        throw new NotFoundException("Listing not found: " + id);
    }
}
