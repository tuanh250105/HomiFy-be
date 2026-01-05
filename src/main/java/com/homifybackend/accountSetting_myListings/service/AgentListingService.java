package com.homifybackend.accountSetting_myListings.service;

import com.homifybackend.accountSetting_myListings.dto.listing.ListingRequest;
import com.homifybackend.accountSetting_myListings.dto.listing.ListingResponse;
import com.homifybackend.accountSetting_myListings.exception.NotFoundException;
import com.homifybackend.accountSetting_myListings.mapper.ListingMapper;
import com.homifybackend.accountSetting_myListings.model.*;
import com.homifybackend.accountSetting_myListings.repository.*;
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

    /**
     * If property type changes, we must move the row between subtype tables (JOINED inheritance).
     * We do this with SQL to avoid Hibernate 'null identifier' / mixed entity state.
     */
    private void switchSubtype(Long propertyId, String newType) {
        String t = normType(newType);
        if (t == null) return;

        // clear all subtype rows
        jdbc.update("DELETE FROM town_houses WHERE property_id=?", propertyId);
        jdbc.update("DELETE FROM apartments WHERE property_id=?", propertyId);
        jdbc.update("DELETE FROM villas WHERE property_id=?", propertyId);
        jdbc.update("DELETE FROM single_houses WHERE property_id=?", propertyId);

        // insert minimal row to target subtype table
        switch (t) {
            case "TOWN_HOUSE" -> jdbc.update("INSERT INTO town_houses(property_id) VALUES (?)", propertyId);
            case "APARTMENT" -> jdbc.update("INSERT INTO apartments(property_id) VALUES (?)", propertyId);
            case "VILLA" -> jdbc.update("INSERT INTO villas(property_id) VALUES (?)", propertyId);
            case "SINGLE_HOUSE" -> jdbc.update("INSERT INTO single_houses(property_id) VALUES (?)", propertyId);
            default -> {
                // no subtype
            }
        }
        // IMPORTANT: clear persistence context so Hibernate doesn't keep old subtype instance
        em.clear();
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

        // Load agent
        Agent agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Agent not found: " + agentId));

        // Owner
        Long ownerId = req.getProperty() != null ? req.getProperty().getOwnerId() : null;
        if (ownerId == null) throw new IllegalArgumentException("ownerId is required (property.ownerId)");
        Customer owner = customerRepo.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + ownerId));

        // Address
        if (req.getAddress() == null) throw new IllegalArgumentException("address is required");
        Address addr;
        if (req.getAddress().getAddressId() != null) {
            addr = addressRepo.findById(req.getAddress().getAddressId())
                    .orElseThrow(() -> new NotFoundException("Address not found: " + req.getAddress().getAddressId()));
        } else {
            addr = Address.builder()
                    .zipCode(req.getAddress().getZipCode())
                    .city(req.getAddress().getCity())
                    .province(req.getAddress().getProvince())
                    .street(req.getAddress().getStreet())
                    .nation(req.getAddress().getNation())
                    .latitude(req.getAddress().getLatitude())
                    .longitude(req.getAddress().getLongitude())
                    .build();
            addr = addressRepo.save(addr);
        }

        // Property (new)
        String type = req.getProperty() != null ? req.getProperty().getPropertyType() : null;
        Property p = newPropertyByType(type);
        p.setOwner(owner);
        p.setAddress(addr);
        ListingMapper.applyToProperty(p, req);
        p = propertyRepo.save(p);

        // If subtype was requested, ensure subtype row exists (when INSERT only base table happened)
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

        // SALE only in this module
        SaleListing s = saleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale listing not found: " + id));

        Agent agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Agent not found: " + agentId));

        // Property must exist
        Property p = s.getProperty();
        if (p == null) throw new IllegalStateException("Listing missing property");

        // If FE requests type change
        String desiredType = req.getProperty() != null ? req.getProperty().getPropertyType() : null;
        if (desiredType != null && !normType(desiredType).equals(currentType(p))) {
            switchSubtype(p.getPropertyId(), desiredType);
            p = em.find(Property.class, p.getPropertyId());
        }

        // Owner update (optional)
        Long ownerId = req.getProperty() != null ? req.getProperty().getOwnerId() : null;
        if (ownerId != null) {
            Customer owner = customerRepo.findById(ownerId)
                    .orElseThrow(() -> new NotFoundException("Customer not found: " + ownerId));
            p.setOwner(owner);
        }

        // Address update
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

        ListingMapper.applyToProperty(p, req);
        p = propertyRepo.save(p);
        s.setProperty(p);

        s.setAgent(agent);
        ListingMapper.applyToSale(s, req);
        s = saleRepo.save(s);

        return ListingMapper.toResponseFromSale(s);
    }

    @Transactional
    public void delete(Long id) {
        if (!saleRepo.existsById(id)) throw new NotFoundException("Listing not found: " + id);
        saleRepo.deleteById(id);
    }

    @Transactional
    public void changeStatus(Long id, String status) {
        SaleListing s = saleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found: " + id));
        s.setSaleStatus(SaleListingStatus.valueOf(status));
        saleRepo.save(s);
    }
}
