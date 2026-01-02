package com.homifybackend.accountSetting_myListings.mapper;

import com.homifybackend.accountSetting_myListings.dto.listing.ListingRequest;
import com.homifybackend.accountSetting_myListings.dto.listing.ListingResponse;
import com.homifybackend.accountSetting_myListings.model.Address;
import com.homifybackend.accountSetting_myListings.model.Property;
import com.homifybackend.accountSetting_myListings.model.RentalListing;
import com.homifybackend.accountSetting_myListings.model.SaleListing;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ListingMapper {

    private ListingMapper() {}

    public static Property toProperty(ListingRequest req) {
        ListingRequest.PropertyDto p = req.getProperty();
        ListingRequest.AddressDto a = req.getAddress();

        // IMPORTANT:
        // DB schema enforces properties.owner_id NOT NULL.
        // FE create form may omit ownerId, so we default it from agentId.
        Long ownerId = req.getProperty() != null ? req.getProperty().getOwnerId() : null;
        if (ownerId == null) {
            throw new IllegalArgumentException("property.ownerId (CUSTOMER id) is required");
        }

        Address addr = Address.builder()
                .addressId(a != null ? a.getAddressId() : null)
                .street(a != null ? a.getStreet() : null)
                .city(a != null ? a.getCity() : null)
                .province(a != null ? a.getProvince() : null)
                .nation(a != null ? a.getNation() : null)
                .zipCode(a != null ? a.getZipCode() : null)
                .latitude(a != null ? a.getLatitude() : null)
                .longitude(a != null ? a.getLongitude() : null)
                .build();

        return Property.builder()
                .propertyId(p != null ? p.getPropertyId() : null)
                .ownerId(ownerId)
                .yearBuilt(p != null ? p.getYearBuilt() : null)
                .floors(p != null ? p.getFloors() : null)
                .beds(p != null ? p.getBeds() : null)
                .baths(p != null ? p.getBaths() : null)
                .area(p != null ? p.getArea() : null)
                .description(p != null ? p.getDescription() : null)
                .address(addr)
                .build();
    }

    public static SaleListing applyToSale(SaleListing entity, ListingRequest req) {
        entity.setAgentId(req.getAgent() != null ? req.getAgent().getAgentId() : null);
        entity.setProperty(toProperty(req));

        if (req.getPricing() != null) {
            entity.setCurrentPrice(req.getPricing().getCurrentPrice());
            entity.setEstimateValue(req.getPricing().getEstimateValue());
        }
        entity.setSaleStatus(req.getStatus());
        entity.setMarketingDescription(req.getMarketing() != null ? req.getMarketing().getMarketingDescription() : null);

        if (entity.getDateListed() == null) entity.setDateListed(LocalDateTime.now());
        return entity;
    }

    public static RentalListing applyToRent(RentalListing entity, ListingRequest req) {
        entity.setProperty(toProperty(req));
        if (req.getPricing() != null) {
            entity.setMonthlyRent(req.getPricing().getMonthlyRent());
            entity.setDepositAmount(req.getPricing().getDepositAmount());
            entity.setMaintenanceFee(req.getPricing().getMaintenanceFee());
            entity.setLeaseTermMonths(req.getPricing().getLeaseTermMonths());
            entity.setPetAllowed(req.getPricing().getPetAllowed());
            entity.setUtilitiesIncluded(req.getPricing().getUtilitiesIncluded());
        }
        entity.setListingStatus(req.getStatus());
        entity.setMarketingDescription(req.getMarketing() != null ? req.getMarketing().getMarketingDescription() : null);

        if (entity.getDateListed() == null) entity.setDateListed(LocalDateTime.now());
        return entity;
    }

    public static ListingResponse toResponseFromSale(SaleListing s) {
        Map<String, Object> pricing = new HashMap<>();
        pricing.put("currentPrice", s.getCurrentPrice());
        pricing.put("estimateValue", s.getEstimateValue());
        pricing.put("currency", "VND");

        Map<String, Object> marketing = new HashMap<>();
        marketing.put("title", null); // FE đang có title trong marketing object, bạn có thể lưu DB sau
        marketing.put("marketingDescription", s.getMarketingDescription());

        Map<String, Object> address = addrMap(s.getProperty() != null ? s.getProperty().getAddress() : null);
        Map<String, Object> property = propMap(s.getProperty());

        return ListingResponse.builder()
                .id(s.getId())
                .listingType("SALE")
                .status(s.getSaleStatus())
                .pricing(pricing)
                .marketing(marketing)
                .address(address)
                .property(property)
                .media(new HashMap<>()) // images FE xử lý trước, bạn muốn persist thì mình add bảng image tiếp
                .createdAt(s.getDateListed() != null ? s.getDateListed().toString() : null)
                .updatedAt(LocalDateTime.now().toString())
                .build();
    }

    public static ListingResponse toResponseFromRent(RentalListing r) {
        Map<String, Object> pricing = new HashMap<>();
        pricing.put("monthlyRent", r.getMonthlyRent());
        pricing.put("depositAmount", r.getDepositAmount());
        pricing.put("maintenanceFee", r.getMaintenanceFee());
        pricing.put("availableFrom", r.getAvailableFrom() != null ? r.getAvailableFrom().toString() : null);
        pricing.put("leaseTermMonths", r.getLeaseTermMonths());
        pricing.put("petAllowed", r.getPetAllowed());
        pricing.put("utilitiesIncluded", r.getUtilitiesIncluded());
        pricing.put("currency", "VND");

        Map<String, Object> marketing = new HashMap<>();
        marketing.put("title", null);
        marketing.put("marketingDescription", r.getMarketingDescription());

        Map<String, Object> address = addrMap(r.getProperty() != null ? r.getProperty().getAddress() : null);
        Map<String, Object> property = propMap(r.getProperty());

        return ListingResponse.builder()
                .id(r.getId())
                .listingType("RENT")
                .status(r.getListingStatus())
                .pricing(pricing)
                .marketing(marketing)
                .address(address)
                .property(property)
                .media(new HashMap<>())
                .createdAt(r.getDateListed() != null ? r.getDateListed().toString() : null)
                .updatedAt(LocalDateTime.now().toString())
                .build();
    }

    private static Map<String, Object> addrMap(Address a) {
        Map<String, Object> m = new HashMap<>();
        if (a == null) return m;
        m.put("addressId", a.getAddressId());
        m.put("street", a.getStreet());
        m.put("city", a.getCity());
        m.put("province", a.getProvince());
        m.put("nation", a.getNation());
        m.put("zipCode", a.getZipCode());
        m.put("latitude", a.getLatitude());
        m.put("longitude", a.getLongitude());
        return m;
    }

    private static Map<String, Object> propMap(Property p) {
        Map<String, Object> m = new HashMap<>();
        if (p == null) return m;
        m.put("propertyId", p.getPropertyId());
        m.put("ownerId", p.getOwnerId());
        m.put("yearBuilt", p.getYearBuilt());
        m.put("floors", p.getFloors());
        m.put("beds", p.getBeds());
        m.put("baths", p.getBaths());
        m.put("area", p.getArea());
        m.put("description", p.getDescription());
        return m;
    }
}
