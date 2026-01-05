package com.homifybackend.accountSetting_myListings.service;

import com.homifybackend.dto.ListingRequest;
import com.homifybackend.dto.ListingResponse;
import com.homifybackend.exception.NotFoundException;
import com.homifybackend.mapper.ListingMapper;
import com.homifybackend.model.*;
import com.homifybackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentListingService {

    private final SaleListingRepository saleRepo;
    private final RentalListingRepository rentRepo;
    private final PropertyRepository propertyRepo;
    private final AddressRepository addressRepo;
    private final CustomerRepository customerRepo;
    private final AgentRepository agentRepo;
    private final JdbcTemplate jdbc;

    @PersistenceContext
    private EntityManager em;

    private void validateOwnerIsCustomerIfPresent(ListingRequest req) {
        Long ownerId = req.getProperty() != null ? req.getProperty().getOwnerId() : null;
        if (ownerId == null) return;
        if (!customerRepo.existsById(ownerId)) {
            throw new IllegalArgumentException("Invalid ownerId=" + ownerId + " (not found in customers)");
        }
    }

    private static String normType(String t) {
        if (t == null) return null;
        return t.trim().toUpperCase();
    }

    private static String currentType(Property p) {
        if (p instanceof TownHouse) return "TOWN_HOUSE";
        if (p instanceof Apartment) return "APARTMENT";
        if (p instanceof Villa) return "VILLA";
        if (p instanceof SingleHouse) return "SINGLE_HOUSE";
        return "PROPERTY";
    }

    private Property newPropertyByType(String type) {
        String t = normType(type);
        if (t == null) return new Property();
        return switch (t) {
            case "TOWN_HOUSE" -> new TownHouse();
            case "APARTMENT" -> new Apartment();
            case "VILLA" -> new Villa();
            case "SINGLE_HOUSE" -> new SingleHouse();
            default -> new Property();
        };
    }

    private void switchSubtype(Long propertyId, String newType) {
        String t = normType(newType);
        if (t == null) return;

        jdbc.update("DELETE FROM town_houses WHERE property_id=?", propertyId);
        jdbc.update("DELETE FROM apartments WHERE property_id=?", propertyId);
        jdbc.update("DELETE FROM villas WHERE property_id=?", propertyId);
        jdbc.update("DELETE FROM single_houses WHERE property_id=?", propertyId);

        switch (t) {
            case "TOWN_HOUSE" -> jdbc.update("INSERT INTO town_houses(property_id) VALUES (?)", propertyId);
            case "APARTMENT" -> jdbc.update("INSERT INTO apartments(property_id) VALUES (?)", propertyId);
            case "VILLA" -> jdbc.update("INSERT INTO villas(property_id) VALUES (?)", propertyId);
            case "SINGLE_HOUSE" -> jdbc.update("INSERT INTO single_houses(property_id) VALUES (?)", propertyId);
            default -> { }
        }

        // ✅ Detach from cache without clearing entire context
        em.detach(em.find(Property.class, propertyId));
    }

    @Transactional(readOnly = true)
    public List<ListingResponse> getMyListings(Long agentId) {
        return saleRepo.findByAgent_UserId(agentId)
                .stream()
                .map(ListingMapper::toResponseFromSale)
                .toList();
    }

    @Transactional
    public ListingResponse create(Long agentId, ListingRequest req) {
        validateOwnerIsCustomerIfPresent(req);
        if (req.getListingType() != null && "RENT".equalsIgnoreCase(req.getListingType())) {
            throw new IllegalArgumentException("Rent listings are not agent-owned in schema v2");
        }

        Agent agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Agent not found: " + agentId));

        Long ownerId = req.getProperty() != null ? req.getProperty().getOwnerId() : null;
        if (ownerId == null) throw new IllegalArgumentException("ownerId is required (property.ownerId)");
        Customer owner = customerRepo.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + ownerId));

        if (req.getAddress() == null) throw new IllegalArgumentException("address is required");
        Address addr;
        if (req.getAddress().getAddressId() != null) {
            addr = addressRepo.findById(req.getAddress().getAddressId())
                    .orElseThrow(() -> new NotFoundException("Address not found: " + req.getAddress().getAddressId()));
        } else {
            addr = new Address();
            addr.setZipCode(req.getAddress().getZipCode());
            addr.setCity(req.getAddress().getCity());
            addr.setProvince(req.getAddress().getProvince());
            addr.setStreet(req.getAddress().getStreet());
            addr.setNation(req.getAddress().getNation());
            addr.setLatitude(req.getAddress().getLatitude());
            addr.setLongitude(req.getAddress().getLongitude());
            addr = addressRepo.save(addr);
        }

        String type = req.getProperty() != null ? req.getProperty().getPropertyType() : null;
        Property p = newPropertyByType(type);
        p.setOwner(owner);
        p.setAddress(addr);
        ListingMapper.applyToProperty(p, req, customerRepo, em, jdbc);
        p = propertyRepo.save(p);

        if (type != null && !"PROPERTY".equalsIgnoreCase(type)) {
            switchSubtype(p.getPropertyId(), type);
            p = em.find(Property.class, p.getPropertyId());
        }

        SaleListing s = new SaleListing();
        s.setAgent(agent);
        s.setProperty(p);
        ListingMapper.applyToSale(s, req);
        s = saleRepo.save(s);

        return ListingMapper.toResponseFromSale(s);
    }

    @Transactional
    public ListingResponse update(Long agentId, Long id, ListingRequest req) {
        validateOwnerIsCustomerIfPresent(req);

        // Determine listing type by checking both SALE and RENT repos
        String listingType = req.getListingType();
        if (listingType == null || listingType.isBlank()) {
            // Try to detect from existing listing
            var saleOpt = saleRepo.findByIdWithImages(id);
            if (saleOpt.isPresent()) {
                listingType = "SALE";
            } else {
                var rentOpt = rentRepo.findById(id);
                if (rentOpt.isPresent()) {
                    listingType = "RENT";
                } else {
                    throw new NotFoundException("Listing not found: " + id);
                }
            }
        }

        // Handle SALE listing update
        if ("SALE".equalsIgnoreCase(listingType)) {
            return updateSaleListing(agentId, id, req);
        }
        // Handle RENT listing update
        else if ("RENT".equalsIgnoreCase(listingType)) {
            return updateRentalListing(agentId, id, req);
        } else {
            throw new IllegalArgumentException("Invalid listingType: " + listingType + " (use SALE or RENT)");
        }
    }

    private ListingResponse updateSaleListing(Long agentId, Long id, ListingRequest req) {
        SaleListing s = saleRepo.findByIdWithImages(id)
                .orElseThrow(() -> new NotFoundException("Sale listing not found: " + id));

        Agent agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Agent not found: " + agentId));

        Property p = s.getProperty();
        if (p == null) throw new IllegalStateException("Listing missing property");

        String desiredType = req.getProperty() != null ? req.getProperty().getPropertyType() : null;
        if (desiredType != null && !normType(desiredType).equals(currentType(p))) {
            switchSubtype(p.getPropertyId(), desiredType);
            p = em.find(Property.class, p.getPropertyId());
        }

        Long ownerId = req.getProperty() != null ? req.getProperty().getOwnerId() : null;
        if (ownerId != null) {
            Customer owner = customerRepo.findById(ownerId)
                    .orElseThrow(() -> new NotFoundException("Customer not found: " + ownerId));
            p.setOwner(owner);
        }

        if (req.getAddress() != null) {
            Address addr = p.getAddress() != null ? p.getAddress() : new Address();
            if (req.getAddress().getAddressId() != null) {
                addr = addressRepo.findById(req.getAddress().getAddressId())
                        .orElseThrow(() -> new NotFoundException("Address not found: " + req.getAddress().getAddressId()));
            }
            if (req.getAddress().getZipCode() != null) addr.setZipCode(req.getAddress().getZipCode());
            if (req.getAddress().getCity() != null) addr.setCity(req.getAddress().getCity());
            if (req.getAddress().getProvince() != null) addr.setProvince(req.getAddress().getProvince());
            if (req.getAddress().getStreet() != null) addr.setStreet(req.getAddress().getStreet());
            if (req.getAddress().getNation() != null) addr.setNation(req.getAddress().getNation());
            if (req.getAddress().getLatitude() != null) addr.setLatitude(req.getAddress().getLatitude());
            if (req.getAddress().getLongitude() != null) addr.setLongitude(req.getAddress().getLongitude());
            addr = addressRepo.save(addr);
            p.setAddress(addr);
        }

        ListingMapper.applyToProperty(p, req, customerRepo, em, jdbc);
        p = propertyRepo.save(p);
        // ✅ Reload property with features to avoid lazy loading in mapper
        p = propertyRepo.findByIdWithFeatures(p.getPropertyId())
                .orElseThrow(() -> new IllegalStateException("Property not found after save"));
        s.setProperty(p);

        s.setAgent(agent);
        ListingMapper.applyToSale(s, req);
        s = saleRepo.save(s);

        return ListingMapper.toResponseFromSale(s);
    }

    private ListingResponse updateRentalListing(Long agentId, Long id, ListingRequest req) {
        // ✅ IMPORTANT: fetch images to avoid LazyInitializationException when applyToRent touches images
        RentalListing r = rentRepo.findByIdWithImages(id)
                .orElseThrow(() -> new NotFoundException("Rental listing not found: " + id));

        Property p = r.getProperty();
        if (p == null) throw new IllegalStateException("Listing missing property");

        String desiredType = req.getProperty() != null ? req.getProperty().getPropertyType() : null;
        if (desiredType != null && !normType(desiredType).equals(currentType(p))) {
            switchSubtype(p.getPropertyId(), desiredType);
            p = em.find(Property.class, p.getPropertyId());
        }

        Long ownerId = req.getProperty() != null ? req.getProperty().getOwnerId() : null;
        if (ownerId != null) {
            Customer owner = customerRepo.findById(ownerId)
                    .orElseThrow(() -> new NotFoundException("Customer not found: " + ownerId));
            p.setOwner(owner);
        }

        if (req.getAddress() != null) {
            Address addr = p.getAddress() != null ? p.getAddress() : new Address();
            if (req.getAddress().getAddressId() != null) {
                addr = addressRepo.findById(req.getAddress().getAddressId())
                        .orElseThrow(() -> new NotFoundException("Address not found: " + req.getAddress().getAddressId()));
            }
            if (req.getAddress().getZipCode() != null) addr.setZipCode(req.getAddress().getZipCode());
            if (req.getAddress().getCity() != null) addr.setCity(req.getAddress().getCity());
            if (req.getAddress().getProvince() != null) addr.setProvince(req.getAddress().getProvince());
            if (req.getAddress().getStreet() != null) addr.setStreet(req.getAddress().getStreet());
            if (req.getAddress().getNation() != null) addr.setNation(req.getAddress().getNation());
            if (req.getAddress().getLatitude() != null) addr.setLatitude(req.getAddress().getLatitude());
            if (req.getAddress().getLongitude() != null) addr.setLongitude(req.getAddress().getLongitude());
            addr = addressRepo.save(addr);
            p.setAddress(addr);
        }

        ListingMapper.applyToProperty(p, req, customerRepo, em, jdbc);
        p = propertyRepo.save(p);
        // ✅ Reload property with features to avoid lazy loading in mapper
        p = propertyRepo.findByIdWithFeatures(p.getPropertyId())
                .orElseThrow(() -> new IllegalStateException("Property not found after save"));
        r.setProperty(p);

        ListingMapper.applyToRent(r, req);
        r = rentRepo.save(r);

        return ListingMapper.toResponseFromRent(r);
    }

    // ✅ Controller đang gọi service.delete(id)
    @Transactional
    public void delete(Long id) {
        // load kèm images cho chắc nếu DB đang ràng buộc cascade/orphanRemoval
        SaleListing s = saleRepo.findByIdWithImages(id)
                .orElseThrow(() -> new NotFoundException("Listing not found: " + id));
        saleRepo.delete(s);
    }

    // ✅ Controller đang gọi service.changeStatus(id, status)
    @Transactional
    public void changeStatus(Long id, String status) {
        SaleListing s = saleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found: " + id));
        try {
            s.setSaleStatus(SaleListingStatus.valueOf(status.trim().toUpperCase()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid saleStatus: " + status + " (use ACTIVE/PENDING/SOLD)");
        }
        saleRepo.save(s);
    }
}
