package com.homifybackend.salelisting.service;

import com.homifybackend.salelisting.dto.CreateDraftListingRequest;
import com.homifybackend.salelisting.dto.CreateDraftListingResponse;
import com.homifybackend.salelisting.dto.ListingResponse;
import com.homifybackend.salelisting.dto.UpdateListingRequest;
import com.homifybackend.model.*;
import com.homifybackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ListingService {
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private SaleListingRepository saleListingRepository;
    
    @Autowired
    private SingleHouseRepository singleHouseRepository;
    
    @Autowired
    private TownHouseRepository townHouseRepository;
    
    @Autowired
    private ApartmentRepository apartmentRepository;
    
    @Autowired
    private VillaRepository villaRepository;
    
    /**
     * 1. Create Draft Listing
     * Transaction bao gồm 4 bước:
     * - Insert addresses
     * - Insert properties
     * - Insert subtype table theo propertyType
     * - Insert sale_listings với sale_status = DRAFT
     */
    @Transactional
    public CreateDraftListingResponse createDraft(CreateDraftListingRequest request) {
        // Step 1: Insert Address
        Address address = new Address();
        address.setStreet(request.getAddress().getStreet());
        address.setCity(request.getAddress().getCity());
        address.setProvince(request.getAddress().getProvince());
        address.setNation(request.getAddress().getNation());
        address.setLatitude(request.getAddress().getLatitude());
        address.setLongitude(request.getAddress().getLongitude());
        address = addressRepository.save(address);
        
        // Step 2: Insert Property
        Property property = new Property();
        property.setOwnerId(request.getOwnerId());
        property.setAddressId(address.getAddressId());
        property.setYearBuilt(request.getStructureData().getYearBuilt());
        property.setFloors(request.getStructureData().getFloors());
        property.setBeds(request.getStructureData().getBeds());
        property.setBaths(request.getStructureData().getBaths());
        property.setArea(request.getStructureData().getArea());
        property.setDescription(request.getStructureData().getDescription());
        property = propertyRepository.save(property);
        
        // Step 3: Insert Subtype based on PropertyType
        Long propertyId = property.getPropertyId();
        insertSubtype(propertyId, request.getPropertyType(), request.getSubtypeData());
        
        // Step 4: Insert SaleListing with DRAFT status
        SaleListing saleListing = new SaleListing();
        saleListing.setAgentId(request.getAgentId());
        saleListing.setPropertyId(propertyId);
        saleListing.setSaleStatus(SaleStatus.DRAFT);
        saleListing = saleListingRepository.save(saleListing);
        
        // Return response
        return new CreateDraftListingResponse(
            propertyId,
            saleListing.getId(),
            SaleStatus.DRAFT
        );
    }
    
    /**
     * 2. Update Listing
     * Lấy sale_listings → propertyId
     * Update: properties, subtype, rooms, features, ratings, images
     * KHÔNG đổi status
     */
    @Transactional
    public void updateListing(Long listingId, UpdateListingRequest request) {
        // Find listing
        SaleListing saleListing = saleListingRepository.findById(listingId)
            .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));
        
        Long propertyId = saleListing.getPropertyId();
        
        // Update Property if structureData is provided
        if (request.getStructureData() != null) {
            Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));
            
            // Update property fields from request
            if (request.getStructureData().containsKey("yearBuilt")) {
                property.setYearBuilt((Integer) request.getStructureData().get("yearBuilt"));
            }
            if (request.getStructureData().containsKey("floors")) {
                property.setFloors((Integer) request.getStructureData().get("floors"));
            }
            if (request.getStructureData().containsKey("beds")) {
                property.setBeds((Integer) request.getStructureData().get("beds"));
            }
            if (request.getStructureData().containsKey("baths")) {
                property.setBaths((Integer) request.getStructureData().get("baths"));
            }
            if (request.getStructureData().containsKey("area")) {
                property.setArea((Double) request.getStructureData().get("area"));
            }
            if (request.getStructureData().containsKey("description")) {
                property.setDescription((String) request.getStructureData().get("description"));
            }
            
            propertyRepository.save(property);
        }
        
        // Update pricing if provided
        if (request.getPricing() != null) {
            if (request.getPricing().containsKey("currentPrice")) {
                Object priceObj = request.getPricing().get("currentPrice");
                if (priceObj != null) {
                    BigDecimal price = new BigDecimal(priceObj.toString());
                    saleListing.setCurrentPrice(price);
                }
            }
            if (request.getPricing().containsKey("estimateValue")) {
                Object valueObj = request.getPricing().get("estimateValue");
                if (valueObj != null) {
                    BigDecimal value = new BigDecimal(valueObj.toString());
                    saleListing.setEstimateValue(value);
                }
            }
        }
        
        // Update marketing if provided
        if (request.getMarketing() != null && request.getMarketing().containsKey("description")) {
            saleListing.setMarketingDescription((String) request.getMarketing().get("description"));
        }
        
        saleListingRepository.save(saleListing);
        
        // TODO: Update rooms, features, ratings, images
        // Implement based on actual schema for these tables
    }
    
    /**
     * 3. Get Listing
     * JOIN toàn bộ bảng liên quan
     * Trả về ListingResponse
     */
    @Transactional(readOnly = true)
    public ListingResponse getListing(Long listingId) {
        // Find listing
        SaleListing saleListing = saleListingRepository.findById(listingId)
            .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));
        
        // Find property
        Property property = propertyRepository.findById(saleListing.getPropertyId())
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        // Find address
        Address address = addressRepository.findById(property.getAddressId())
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Determine property type by checking subtype tables
        PropertyType propertyType = determinePropertyType(property.getPropertyId());
        
        // Build response
        ListingResponse response = new ListingResponse();
        response.setListingId(saleListing.getId());
        response.setPropertyId(property.getPropertyId());
        response.setSaleStatus(saleListing.getSaleStatus());
        response.setPropertyType(propertyType);
        response.setCurrentPrice(saleListing.getCurrentPrice());
        response.setEstimateValue(saleListing.getEstimateValue());
        response.setMarketingDescription(saleListing.getMarketingDescription());
        
        // Build property map
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("propertyId", property.getPropertyId());
        propertyMap.put("yearBuilt", property.getYearBuilt());
        propertyMap.put("floors", property.getFloors());
        propertyMap.put("beds", property.getBeds());
        propertyMap.put("baths", property.getBaths());
        propertyMap.put("area", property.getArea());
        propertyMap.put("description", property.getDescription());
        response.setProperty(propertyMap);
        
        // Build address map
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("street", address.getStreet());
        addressMap.put("city", address.getCity());
        addressMap.put("province", address.getProvince());
        addressMap.put("nation", address.getNation());
        addressMap.put("latitude", address.getLatitude());
        addressMap.put("longitude", address.getLongitude());
        response.setAddress(addressMap);
        
        // Build subtype map
        Map<String, Object> subtypeMap = getSubtypeData(property.getPropertyId(), propertyType);
        response.setSubtype(subtypeMap);
        
        // TODO: Add rooms, features, ratings, images
        
        return response;
    }
    
    /**
     * 4. Submit Listing
     * Update sale_status = ACTIVE
     * Validate: có price, có ít nhất 1 image, có structureData cơ bản
     */
    @Transactional
    public void submitListing(Long listingId) {
        // Find listing
        SaleListing saleListing = saleListingRepository.findById(listingId)
            .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));
        
        // Validate
        if (saleListing.getCurrentPrice() == null || saleListing.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Current price is required to submit listing");
        }
        
        // Find property to validate structure data
        Property property = propertyRepository.findById(saleListing.getPropertyId())
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        if (property.getBeds() == null || property.getBaths() == null || property.getArea() == null) {
            throw new RuntimeException("Basic structure data (beds, baths, area) is required");
        }
        
        // TODO: Validate at least 1 image exists
        
        // Update status to ACTIVE
        saleListing.setSaleStatus(SaleStatus.ACTIVE);
        saleListingRepository.save(saleListing);
    }
    
    // Helper methods
    
    private void insertSubtype(Long propertyId, PropertyType propertyType, Map<String, Object> subtypeData) {
        if (subtypeData == null) {
            subtypeData = new HashMap<>();
        }
        
        switch (propertyType) {
            case SINGLE_HOUSE:
                SingleHouse singleHouse = new SingleHouse();
                singleHouse.setPropertyId(propertyId);
                singleHouse.setLandArea(getDoubleValue(subtypeData, "landArea"));
                singleHouse.setBackyardArea(getDoubleValue(subtypeData, "backyardArea"));
                singleHouse.setFrontYardArea(getDoubleValue(subtypeData, "frontYardArea"));
                singleHouse.setHasGarage(getBooleanValue(subtypeData, "hasGarage"));
                singleHouse.setHasBasement(getBooleanValue(subtypeData, "hasBasement"));
                singleHouseRepository.save(singleHouse);
                break;
                
            case TOWN_HOUSE:
                TownHouse townHouse = new TownHouse();
                townHouse.setPropertyId(propertyId);
                townHouse.setLandArea(getDoubleValue(subtypeData, "landArea"));
                townHouse.setNumberOfFloors(getIntegerValue(subtypeData, "numberOfFloors"));
                townHouse.setCornerLot(getBooleanValue(subtypeData, "cornerLot"));
                townHouse.setFrontWidth(getDoubleValue(subtypeData, "frontWidth"));
                townHouse.setDepth(getDoubleValue(subtypeData, "depth"));
                townHouse.setRoadWidth(getDoubleValue(subtypeData, "roadWidth"));
                townHouseRepository.save(townHouse);
                break;
                
            case APARTMENT:
                Apartment apartment = new Apartment();
                apartment.setPropertyId(propertyId);
                apartment.setUsableArea(getDoubleValue(subtypeData, "usableArea"));
                apartment.setLevel(getIntegerValue(subtypeData, "level"));
                apartment.setHasElevatorAccess(getBooleanValue(subtypeData, "hasElevatorAccess"));
                apartment.setPetAllowed(getBooleanValue(subtypeData, "petAllowed"));
                apartment.setBalcony(getBooleanValue(subtypeData, "balcony"));
                apartment.setTotalBuildingFloors(getIntegerValue(subtypeData, "totalBuildingFloors"));
                apartmentRepository.save(apartment);
                break;
                
            case VILLA:
                Villa villa = new Villa();
                villa.setPropertyId(propertyId);
                villa.setLotArea(getDoubleValue(subtypeData, "lotArea"));
                villa.setBackyardArea(getDoubleValue(subtypeData, "backyardArea"));
                villa.setFrontYardArea(getDoubleValue(subtypeData, "frontYardArea"));
                villa.setGardenArea(getDoubleValue(subtypeData, "gardenArea"));
                villa.setParkingSpaces(getIntegerValue(subtypeData, "parkingSpaces"));
                villa.setHasGarage(getBooleanValue(subtypeData, "hasGarage"));
                villa.setHasBasement(getBooleanValue(subtypeData, "hasBasement"));
                villa.setViewType(getStringValue(subtypeData, "viewType"));
                villa.setSmartHomeLevel(getIntegerValue(subtypeData, "smartHomeLevel"));
                villaRepository.save(villa);
                break;
        }
    }
    
    private PropertyType determinePropertyType(Long propertyId) {
        if (singleHouseRepository.existsById(propertyId)) {
            return PropertyType.SINGLE_HOUSE;
        } else if (townHouseRepository.existsById(propertyId)) {
            return PropertyType.TOWN_HOUSE;
        } else if (apartmentRepository.existsById(propertyId)) {
            return PropertyType.APARTMENT;
        } else if (villaRepository.existsById(propertyId)) {
            return PropertyType.VILLA;
        }
        throw new RuntimeException("Property type not found for propertyId: " + propertyId);
    }
    
    private Map<String, Object> getSubtypeData(Long propertyId, PropertyType propertyType) {
        Map<String, Object> subtypeMap = new HashMap<>();
        
        switch (propertyType) {
            case SINGLE_HOUSE:
                singleHouseRepository.findById(propertyId).ifPresent(sh -> {
                    subtypeMap.put("landArea", sh.getLandArea());
                    subtypeMap.put("backyardArea", sh.getBackyardArea());
                    subtypeMap.put("frontYardArea", sh.getFrontYardArea());
                    subtypeMap.put("hasGarage", sh.getHasGarage());
                    subtypeMap.put("hasBasement", sh.getHasBasement());
                });
                break;
                
            case TOWN_HOUSE:
                townHouseRepository.findById(propertyId).ifPresent(th -> {
                    subtypeMap.put("landArea", th.getLandArea());
                    subtypeMap.put("numberOfFloors", th.getNumberOfFloors());
                    subtypeMap.put("cornerLot", th.getCornerLot());
                    subtypeMap.put("frontWidth", th.getFrontWidth());
                    subtypeMap.put("depth", th.getDepth());
                    subtypeMap.put("roadWidth", th.getRoadWidth());
                });
                break;
                
            case APARTMENT:
                apartmentRepository.findById(propertyId).ifPresent(apt -> {
                    subtypeMap.put("usableArea", apt.getUsableArea());
                    subtypeMap.put("level", apt.getLevel());
                    subtypeMap.put("hasElevatorAccess", apt.getHasElevatorAccess());
                    subtypeMap.put("petAllowed", apt.getPetAllowed());
                    subtypeMap.put("balcony", apt.getBalcony());
                    subtypeMap.put("totalBuildingFloors", apt.getTotalBuildingFloors());
                });
                break;
                
            case VILLA:
                villaRepository.findById(propertyId).ifPresent(v -> {
                    subtypeMap.put("lotArea", v.getLotArea());
                    subtypeMap.put("backyardArea", v.getBackyardArea());
                    subtypeMap.put("frontYardArea", v.getFrontYardArea());
                    subtypeMap.put("gardenArea", v.getGardenArea());
                    subtypeMap.put("parkingSpaces", v.getParkingSpaces());
                    subtypeMap.put("hasGarage", v.getHasGarage());
                    subtypeMap.put("hasBasement", v.getHasBasement());
                    subtypeMap.put("viewType", v.getViewType());
                    subtypeMap.put("smartHomeLevel", v.getSmartHomeLevel());
                });
                break;
        }
        
        return subtypeMap;
    }
    
    /**
     * 5. Get All Listings (with filters)
     * Lấy danh sách listings với optional filters
     */
    public List<ListingResponse> getAllListings(Long agentId, Long ownerId, String status) {
        List<SaleListing> listings;
        
        // Apply filters
        if (agentId != null) {
            listings = saleListingRepository.findByAgentId(agentId);
        } else {
            listings = saleListingRepository.findAll();
        }
        
        // Filter by status if provided
        if (status != null) {
            SaleStatus saleStatus = SaleStatus.valueOf(status.toUpperCase());
            listings = listings.stream()
                .filter(l -> l.getSaleStatus() == saleStatus)
                .toList();
        }
        
        // Filter by ownerId if provided
        if (ownerId != null) {
            listings = listings.stream()
                .filter(l -> {
                    Property property = propertyRepository.findById(l.getPropertyId()).orElse(null);
                    return property != null && property.getOwnerId().equals(ownerId);
                })
                .toList();
        }
        
        // Convert to ListingResponse
        return listings.stream()
            .map(listing -> getListing(listing.getId()))
            .toList();
    }
    
    // Utility methods for extracting values from Map
    
    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }
    
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }
    
    private Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }
    
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
}
