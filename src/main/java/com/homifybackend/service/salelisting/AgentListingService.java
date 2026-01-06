package com.homifybackend.service.salelisting;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homifybackend.dto.AddressDTO;
import com.homifybackend.dto.CreateDraftListingRequest;
import com.homifybackend.dto.CreateDraftListingResponse;
import com.homifybackend.dto.ListingResponse;
import com.homifybackend.dto.PropertyDTO;
import com.homifybackend.dto.SubtypeDTO;
import com.homifybackend.dto.UpdateListingRequest;
import com.homifybackend.model.Address;
import com.homifybackend.model.Agent;
import com.homifybackend.model.Customer;
import com.homifybackend.model.Property;
import com.homifybackend.model.PropertyType;
import com.homifybackend.model.SaleListing;
import com.homifybackend.model.SaleListingStatus;
import com.homifybackend.model.SaleStatus;
import com.homifybackend.repository.AddressRepository;
import com.homifybackend.repository.AgentRepository;
import com.homifybackend.repository.ApartmentRepository;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.repository.PropertyRepository;
import com.homifybackend.repository.SaleListingRepository;
import com.homifybackend.repository.SingleHouseRepository;
import com.homifybackend.repository.TownHouseRepository;
import com.homifybackend.repository.VillaRepository;

@Service
public class AgentListingService {
    
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
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private AgentRepository agentRepository;
    
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
        System.out.println("=== createDraft START - ownerId from request: " + request.getOwnerId());
        
        // Step 1: Insert Address
        Address address = new Address();
        address.setStreet(request.getAddress().getStreet());
        address.setCity(request.getAddress().getCity());
        address.setProvince(request.getAddress().getProvince());
        address.setNation(request.getAddress().getNation());
        address.setLatitude(request.getAddress().getLatitude());
        address.setLongitude(request.getAddress().getLongitude());
        address = addressRepository.save(address);
        System.out.println("=== Address saved: " + address.getAddressId());
        
        // Step 2: Try ownerId=1, fallback to first available
        Long ownerIdToUse = 1L;
        Customer owner = customerRepository.findById(ownerIdToUse)
            .orElseGet(() -> {
                System.out.println("⚠️ Customer with ID 1 not found, finding first available...");
                return customerRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No customers found in database"));
            });
        System.out.println("=== Using owner: " + owner.getUserId());
        
        // Step 3: Create Property based on type (for JOINED inheritance)
        Property property;
        switch (request.getPropertyType()) {
            case SINGLE_HOUSE:
                property = new com.homifybackend.model.SingleHouse();
                System.out.println("=== Creating SingleHouse");
                break;
                
            case TOWN_HOUSE:
                property = new com.homifybackend.model.TownHouse();
                System.out.println("=== Creating TownHouse");
                break;
                
            case APARTMENT:
                property = new com.homifybackend.model.Apartment();
                System.out.println("=== Creating Apartment");
                break;
                
            case VILLA:
                property = new com.homifybackend.model.Villa();
                System.out.println("=== Creating Villa");
                break;
                
            default:
                throw new RuntimeException("Unsupported property type: " + request.getPropertyType());
        }
        
        // Set common property fields
        property.setOwner(owner);
        property.setAddress(address);
        property.setYearBuilt(request.getStructureData().getYearBuilt());
        property.setFloors(request.getStructureData().getFloors());
        property.setBeds(request.getStructureData().getBeds());
        property.setBaths(request.getStructureData().getBaths());
        property.setArea(request.getStructureData().getArea());
        property.setDescription(request.getStructureData().getDescription());
        property.setPropertyType(request.getPropertyType().name());
        
        // Save property (will save to both properties and subtype table due to JOINED inheritance)
        property = propertyRepository.save(property);
        System.out.println("=== Property saved: " + property.getPropertyId());
        
        // Step 5: Try agentId=2, fallback to first available
        Long agentIdToUse = 2L;
        Agent agent = agentRepository.findById(agentIdToUse)
            .orElseGet(() -> {
                System.out.println("⚠️ Agent with ID 2 not found, finding first available...");
                return agentRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No agents found in database"));
            });
        System.out.println("=== Using agent: " + agent.getUserId());
        
        // Step 6: Insert SaleListing with DRAFT status
        SaleListing saleListing = new SaleListing();
        saleListing.setAgent(agent);
        
        saleListing.setProperty(property);
        saleListing.setSaleStatus(SaleListingStatus.DRAFT);
        saleListing = saleListingRepository.save(saleListing);
        
        // Return response
        return new CreateDraftListingResponse(
            property.getPropertyId(),
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
        
        Property property = saleListing.getProperty();
        
        // Update Property if structureData is provided
        if (request.getStructureData() != null) {
            // Update property fields from request
            if (request.getStructureData().containsKey("yearBuilt")) {
                Object yearBuilt = request.getStructureData().get("yearBuilt");
                if (yearBuilt != null) {
                    property.setYearBuilt(yearBuilt instanceof Integer ? (Integer) yearBuilt : Integer.parseInt(yearBuilt.toString()));
                }
            }
            if (request.getStructureData().containsKey("floors")) {
                Object floors = request.getStructureData().get("floors");
                if (floors != null) {
                    property.setFloors(floors instanceof Integer ? (Integer) floors : Integer.parseInt(floors.toString()));
                }
            }
            if (request.getStructureData().containsKey("beds")) {
                Object beds = request.getStructureData().get("beds");
                if (beds != null) {
                    property.setBeds(beds instanceof Integer ? (Integer) beds : Integer.parseInt(beds.toString()));
                }
            }
            if (request.getStructureData().containsKey("baths")) {
                Object baths = request.getStructureData().get("baths");
                if (baths != null) {
                    property.setBaths(baths instanceof Integer ? (Integer) baths : Integer.parseInt(baths.toString()));
                }
            }
            if (request.getStructureData().containsKey("area")) {
                Object area = request.getStructureData().get("area");
                if (area != null) {
                    property.setArea(area instanceof Double ? (Double) area : Double.parseDouble(area.toString()));
                }
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
        
        // Auto-submit if listing is DRAFT and has basic data
        if (saleListing.getSaleStatus() == SaleListingStatus.DRAFT) {
            // Check if has minimum required data
            if (property.getBeds() != null && property.getBaths() != null && property.getArea() != null) {
                // Auto-set price if missing
                if (saleListing.getCurrentPrice() == null || saleListing.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    if (saleListing.getEstimateValue() != null && saleListing.getEstimateValue().compareTo(BigDecimal.ZERO) > 0) {
                        saleListing.setCurrentPrice(saleListing.getEstimateValue());
                    } else {
                        saleListing.setCurrentPrice(BigDecimal.ZERO);
                    }
                }
                // Change status to ACTIVE
                saleListing.setSaleStatus(SaleListingStatus.ACTIVE);
                saleListingRepository.save(saleListing);
                System.out.println("=== Auto-submitted listing " + listingId + " from DRAFT to ACTIVE");
            }
        }
        
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
        // Find listing with eager fetch (solves lazy loading issue)
        SaleListing saleListing = saleListingRepository.findByIdWithDetails(listingId)
            .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));
        
        Property property = saleListing.getProperty();
        Address address = property.getAddress();
        
        // Determine property type by checking subtype tables
        PropertyType propertyType = determinePropertyType(property.getPropertyId());
        
        // Build response
        ListingResponse response = new ListingResponse();
        response.setListingId(saleListing.getId());
        response.setPropertyId(property.getPropertyId());
        
        // Map SaleListingStatus to SaleStatus (handle PENDING/SOLD)
        SaleStatus dtoStatus;
        switch (saleListing.getSaleStatus()) {
            case DRAFT:
                dtoStatus = SaleStatus.DRAFT;
                break;
            case ACTIVE:
            case PENDING:
            case SOLD:
                dtoStatus = SaleStatus.ACTIVE;
                break;
            default:
                dtoStatus = SaleStatus.DRAFT;
        }
        response.setSaleStatus(dtoStatus);
        
        response.setPropertyType(propertyType);
        response.setCurrentPrice(saleListing.getCurrentPrice());
        response.setEstimateValue(saleListing.getEstimateValue());
        response.setMarketingDescription(saleListing.getMarketingDescription());
        
        // Build PropertyDTO (no Entity reference)
        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setPropertyId(property.getPropertyId());
        propertyDTO.setYearBuilt(property.getYearBuilt());
        propertyDTO.setFloors(property.getFloors());
        propertyDTO.setBeds(property.getBeds());
        propertyDTO.setBaths(property.getBaths());
        propertyDTO.setArea(property.getArea());
        propertyDTO.setDescription(property.getDescription());
        response.setProperty(propertyDTO);
        
        // Build AddressDTO (no Entity reference)
        AddressDTO addressDTO = new AddressDTO(property.getAddress().getAddressId(), property.getAddress().getCity(), property.getAddress().getProvince(), property.getAddress().getNation(), null, null, null, null);
        response.setAddress(addressDTO);
        
        // Build SubtypeDTO (wrapper for Map)
        Map<String, Object> subtypeData = getSubtypeData(property.getPropertyId(), propertyType);
        SubtypeDTO subtypeDTO = new SubtypeDTO(subtypeData);
        response.setSubtype(subtypeDTO);
        
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
        
        Property property = saleListing.getProperty();
        
        // Validate structure data
        if (property.getBeds() == null || property.getBaths() == null || property.getArea() == null) {
            throw new RuntimeException("Basic structure data (beds, baths, area) is required");
        }
        
        // Auto-set currentPrice from estimateValue if not provided
        if (saleListing.getCurrentPrice() == null || saleListing.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            if (saleListing.getEstimateValue() != null && saleListing.getEstimateValue().compareTo(BigDecimal.ZERO) > 0) {
                saleListing.setCurrentPrice(saleListing.getEstimateValue());
            } else {
                // If no price at all, set a default to allow submission
                saleListing.setCurrentPrice(BigDecimal.ZERO);
            }
        }
        
        // TODO: Validate at least 1 image exists
        
        // Update status to ACTIVE
        saleListing.setSaleStatus(SaleListingStatus.ACTIVE);
        saleListingRepository.save(saleListing);
    }
    
    // Helper methods
    
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
        
        // Property exists but no subtype record - read from property.property_type
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new RuntimeException("Property not found: " + propertyId));
        
        String typeStr = property.getPropertyType();
        if (typeStr == null) {
            throw new RuntimeException("Property type not set for propertyId: " + propertyId);
        }
        
        // Return based on property_type column
        PropertyType type = PropertyType.valueOf(typeStr);
        System.out.println("⚠️ Property " + propertyId + " has no subtype record, using property_type: " + type);
        return type;
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
        System.out.println("=== getAllListings called with agentId=" + agentId + ", ownerId=" + ownerId + ", status=" + status);
        
        // Use eager fetch query to avoid lazy loading issues
        List<SaleListing> listings = saleListingRepository.findAllWithDetails();
        System.out.println("=== Total listings from DB: " + listings.size());
        
        // Filter by agentId if provided
        if (agentId != null) {
            listings = listings.stream()
                .filter(l -> l.getAgent() != null && l.getAgent().getUserId().equals(agentId))
                .toList();
            System.out.println("=== After agentId filter: " + listings.size());
        }
        
        // Filter by status if provided
        if (status != null) {
            listings = listings.stream()
                .filter(l -> l.getSaleStatus().name().equalsIgnoreCase(status))
                .toList();
            System.out.println("=== After status filter: " + listings.size());
        }
        
        // Filter by ownerId if provided
        if (ownerId != null) {
            listings = listings.stream()
                .filter(l -> {
                    Property property = l.getProperty();
                    return property != null && property.getOwner() != null && 
                           property.getOwner().getUserId().equals(ownerId);
                })
                .toList();
            System.out.println("=== After ownerId filter: " + listings.size());
        }
        
        // Convert to ListingResponse
        List<ListingResponse> responses = listings.stream()
            .map(listing -> getListing(listing.getId()))
            .toList();
        System.out.println("=== Final responses: " + responses.size());
        return responses;
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
