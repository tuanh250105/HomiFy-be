package com.homifybackend.accountSetting_myListings.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.homifybackend.dto.ListingRequest;
import com.homifybackend.dto.ListingResponse_Duy;
import com.homifybackend.exception.NotFoundException;
import com.homifybackend.mapper.ListingMapper_Agent;
import com.homifybackend.model.Address;
import com.homifybackend.model.Agent;
import com.homifybackend.model.Apartment;
import com.homifybackend.model.ApplianceRating;
import com.homifybackend.model.Customer;
import com.homifybackend.model.EntertainmentFeatures;
import com.homifybackend.model.OutdoorFeatures;
import com.homifybackend.model.Property;
import com.homifybackend.model.RentalListing;
import com.homifybackend.model.SaleListing;
import com.homifybackend.model.SaleListingStatus;
import com.homifybackend.model.SecurityFeatures;
import com.homifybackend.model.SingleHouse;
import com.homifybackend.model.TownHouse;
import com.homifybackend.model.TransportRating;
import com.homifybackend.model.Villa;
import com.homifybackend.repository.AddressRepository;
import com.homifybackend.repository.AgentRepository;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.repository.PropertyRepository;
import com.homifybackend.repository.RentalListingRepository;
import com.homifybackend.repository.SaleListingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentProfileService {

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
     * Fix for: TransientPropertyValueException
     * Property.applianceRating -> ApplianceRating (transient)
     *
     * Nếu mapper set new ApplianceRating / TransportRating mà entity mapping không cascade persist,
     * thì persist thủ công trước khi save Property.
     *
     * Lưu ý: nếu entity dùng getId() thay vì getRatingId() thì đổi ở 2 try/catch bên dưới.
     */
        private boolean isNewEntity(Object entity) {
        if (entity == null) return false;

        // Nếu đang managed thì không phải transient
        if (em.contains(entity)) return false;

        Object id = null;

        // Try getId()
        try {
            var m = entity.getClass().getMethod("getId");
            id = m.invoke(entity);
        } catch (Exception ignore) {}

        // Try getRatingId()
        if (id == null) {
            try {
                var m = entity.getClass().getMethod("getRatingId");
                id = m.invoke(entity);
            } catch (Exception ignore) {}
        }

        // ✅ null => transient
        if (id == null) return true;

        // ✅ số <= 0 => vẫn là transient (unsaved-value thường là 0)
        if (id instanceof Number n) {
            return n.longValue() <= 0;
        }

        // ✅ chuỗi rỗng => transient
        if (id instanceof String s) {
            return s.trim().isEmpty();
        }

        // Có id hợp lệ => coi như không transient
        return false;
    }


    private void persistRatingsAndFeaturesIfNeeded(Property p) {
        if (p == null) return;

        // Persist ratings
        ApplianceRating ar = p.getApplianceRating();
        if (ar != null && isNewEntity(ar)) {
            em.persist(ar);
            em.flush();
            p.setApplianceRating(ar);
        }

        TransportRating tr = p.getTransportRating();
        if (tr != null && isNewEntity(tr)) {
            em.persist(tr);
            em.flush();
            p.setTransportRating(tr);
        }

        // Persist features
        SecurityFeatures sec = p.getSecurityFeatures();
        if (sec != null && isNewEntity(sec)) {
            em.persist(sec);
            em.flush();
            p.setSecurityFeatures(sec);
        }

        EntertainmentFeatures ent = p.getEntertainmentFeatures();
        if (ent != null && isNewEntity(ent)) {
            em.persist(ent);
            em.flush();
            p.setEntertainmentFeatures(ent);
        }

        OutdoorFeatures out = p.getOutdoorFeatures();
        if (out != null && isNewEntity(out)) {
            em.persist(out);
            em.flush();
            p.setOutdoorFeatures(out);
        }
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
            default -> {
            }
        }

        // Detach cached Property instance (không clear cả persistence context)
        Property managed = em.find(Property.class, propertyId);
        if (managed != null) em.detach(managed);
    }

    @Transactional(readOnly = true)
    public List<ListingResponse_Duy> getMyListings(Long agentId) {
        return saleRepo.findByAgent_UserIdWithFeatures(agentId)
                .stream()
                .map(ListingMapper_Agent::toResponseFromSale)
                .toList();
    }

    @Transactional
    public ListingResponse_Duy create(Long agentId, ListingRequest req) {
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

        ListingMapper_Agent.applyToProperty(p, req, customerRepo, em, jdbc);

        // ✅ FIX transient ratings và features trước khi save property
        persistRatingsAndFeaturesIfNeeded(p);
        
        p = propertyRepo.save(p);

        if (type != null && !"PROPERTY".equalsIgnoreCase(type)) {
            switchSubtype(p.getPropertyId(), type);
            p = em.find(Property.class, p.getPropertyId());
        }

        SaleListing s = new SaleListing();
        s.setAgent(agent);
        s.setProperty(p);

        ListingMapper_Agent.applyToSale(s, req);

        persistRatingsAndFeaturesIfNeeded(s.getProperty());

        s = saleRepo.save(s);


        return ListingMapper_Agent.toResponseFromSale(s);
    }

    @Transactional
    public ListingResponse_Duy update(Long agentId, Long id, ListingRequest req) {
        validateOwnerIsCustomerIfPresent(req);

        String listingType = req.getListingType();
        if (listingType == null || listingType.isBlank()) {
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

        if ("SALE".equalsIgnoreCase(listingType)) {
            return updateSaleListing(agentId, id, req);
        } else if ("RENT".equalsIgnoreCase(listingType)) {
            return updateRentalListing(agentId, id, req);
        }

        throw new IllegalArgumentException("Invalid listingType: " + listingType + " (use SALE or RENT)");
    }

    private ListingResponse_Duy updateSaleListing(Long agentId, Long id, ListingRequest req) {
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
            Address addr = (p.getAddress() != null) ? p.getAddress() : new Address();

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

        ListingMapper_Agent.applyToProperty(p, req, customerRepo, em, jdbc);

        // ✅ FIX transient ratings và features trước khi save property
        persistRatingsAndFeaturesIfNeeded(p);

        p = propertyRepo.save(p);

        // Reload property with features (avoid lazy issues in mapper/response)
        p = propertyRepo.findByIdWithFeatures(p.getPropertyId())
                .orElseThrow(() -> new IllegalStateException("Property not found after save"));

        s.setProperty(p);
        s.setAgent(agent);

        ListingMapper_Agent.applyToSale(s, req);
        persistRatingsAndFeaturesIfNeeded(s.getProperty());
        s = saleRepo.save(s);

        return ListingMapper_Agent.toResponseFromSale(s);
    }

    private ListingResponse_Duy updateRentalListing(Long agentId, Long id, ListingRequest req) {
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
            Address addr = (p.getAddress() != null) ? p.getAddress() : new Address();

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

        ListingMapper_Agent.applyToProperty(p, req, customerRepo, em, jdbc);

        // ✅ FIX transient ratings và features trước khi save property
        persistRatingsAndFeaturesIfNeeded(p);

        p = propertyRepo.save(p);

        // Reload property with features
        p = propertyRepo.findByIdWithFeatures(p.getPropertyId())
                .orElseThrow(() -> new IllegalStateException("Property not found after save"));

        r.setProperty(p);

        ListingMapper_Agent.applyToRent(r, req);
        persistRatingsAndFeaturesIfNeeded(r.getProperty());
        r = rentRepo.save(r);

        return ListingMapper_Agent.toResponseFromRent(r);
    }

    @Transactional
    public void delete(Long id) {
        SaleListing s = saleRepo.findByIdWithImages(id)
                .orElseThrow(() -> new NotFoundException("Listing not found: " + id));

        try {
            saleRepo.delete(s);
            saleRepo.flush(); // bắt lỗi FK ngay tại đây
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Không thể xoá listing vì đang có dữ liệu liên quan (Tour Request).",
                    e
            );
        }
    }

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
