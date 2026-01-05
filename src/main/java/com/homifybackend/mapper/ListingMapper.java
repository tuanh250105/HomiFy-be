package com.homifybackend.mapper;

import com.homifybackend.dto.ListingRequest;
import com.homifybackend.dto.ListingResponse;
import com.homifybackend.dto.ListingImageDto;
import com.homifybackend.model.*;
import com.homifybackend.repository.CustomerRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.ArrayList;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Mapper aligned with the **model chung** DTOs (ListingRequest/ListingResponse)
 * and
 * the JOINED-inheritance Property model
 * (Apartment/Villa/TownHouse/SingleHouse...).
 */
public class ListingMapper {

    private static BigDecimal bd(BigDecimal v) {
        return v;
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank())
            return null;
        try {
            return LocalDate.parse(s);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static SaleListingStatus parseSaleStatus(String s) {
        if (s == null || s.isBlank())
            return null;
        try {
            return SaleListingStatus.valueOf(s.trim().toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static RentalListingStatus parseRentStatus(String s) {
        if (s == null || s.isBlank())
            return null;
        try {
            return RentalListingStatus.valueOf(s.trim().toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String propertyTypeOf(Property p) {
        if (p == null)
            return null;
        // For JOINED inheritance, class name is the type.
        // Example: TownHouse -> TOWNHOUSE
        return p.getClass().getSimpleName().toUpperCase();
    }

    private static Map<String, Object> mapOrNull(Map<String, Object> m) {
        return (m == null || m.isEmpty()) ? null : m;
    }

    private static Map<String, Object> transportToMap(TransportRating t) {
        if (t == null)
            return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("walkScore", t.getWalkScore());
        m.put("bikeScore", t.getBikeScore());
        m.put("transitScore", t.getTransitScore());
        return mapOrNull(m);
    }

    private static Map<String, Object> applianceToMap(ApplianceRating a) {
        if (a == null)
            return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("dishwasher", a.getDishwasher());
        m.put("dryer", a.getDryer());
        m.put("microwave", a.getMicrowave());
        m.put("oven", a.getOven());
        m.put("refrigerator", a.getRefrigerator());
        m.put("washer", a.getWasher());
        return mapOrNull(m);
    }

    private static Map<String, Object> featuresToMap(Property p) {
        if (p == null)
            return null;
        Map<String, Object> features = new LinkedHashMap<>();

        // Security features
        if (p.getSecurityFeatures() != null) {
            Map<String, Object> security = new LinkedHashMap<>();
            security.put("cctv", p.getSecurityFeatures().getHasCctv());
            security.put("securityDoor", p.getSecurityFeatures().getHasSecurityDoor());
            features.put("security", security);
        }

        // Entertainment features
        if (p.getEntertainmentFeatures() != null) {
            Map<String, Object> entertainment = new LinkedHashMap<>();
            entertainment.put("movieCinema", p.getEntertainmentFeatures().getHasMovieCinema());
            entertainment.put("homeGym", p.getEntertainmentFeatures().getHasHomeGym());
            entertainment.put("gameRoom", p.getEntertainmentFeatures().getHasGameRoom());
            features.put("entertainment", entertainment);
        }

        // Outdoor features
        if (p.getOutdoorFeatures() != null) {
            Map<String, Object> outdoor = new LinkedHashMap<>();
            outdoor.put("swimmingPool", p.getOutdoorFeatures().getHasSwimmingPool());
            outdoor.put("childrenPlayground", p.getOutdoorFeatures().getHasChildrensPlayground());
            features.put("outdoor", outdoor);
        }

        return features.isEmpty() ? null : features;
    }

    /**
     * Apply normalized Property block from ListingRequest into Property entity.
     * NOTE: subtype switching is handled by AgentListingService
     * (newPropertyByType/switchSubtype).
     */
    public static void applyToProperty(Property target,
            ListingRequest req,
            CustomerRepository customerRepo,
            EntityManager em,
            JdbcTemplate jdbc) {
        if (target == null || req == null)
            return;

        ListingRequest.PropertyDto p = req.getProperty();
        if (p == null)
            return;

        // owner
        if (p.getOwnerId() != null) {
            Customer owner = em.getReference(Customer.class, p.getOwnerId());
            target.setOwner(owner);
        }

        // basic scalar fields
        if (p.getYearBuilt() != null)
            target.setYearBuilt(p.getYearBuilt());
        if (p.getFloors() != null)
            target.setFloors(p.getFloors());
        if (p.getBeds() != null)
            target.setBeds(p.getBeds());
        if (p.getBaths() != null)
            target.setBaths(p.getBaths());
        if (p.getArea() != null)
            target.setArea(p.getArea());
        if (p.getDescription() != null)
            target.setDescription(p.getDescription());

        // TransportDto -> TransportRating entity
        if (p.getTransport() != null) {
            TransportRating tr = target.getTransportRating();
            if (tr == null)
                tr = new TransportRating();
            if (p.getTransport().getWalkScore() != null)
                tr.setWalkScore(p.getTransport().getWalkScore());
            if (p.getTransport().getBikeScore() != null)
                tr.setBikeScore(p.getTransport().getBikeScore());
            if (p.getTransport().getTransitScore() != null)
                tr.setTransitScore(p.getTransport().getTransitScore());
            target.setTransportRating(tr);
        }

        // AppliancesDto -> ApplianceRating entity
        if (p.getAppliances() != null) {
            ApplianceRating ar = target.getApplianceRating();
            if (ar == null)
                ar = new ApplianceRating();
            if (p.getAppliances().getDishwasher() != null)
                ar.setDishwasher(p.getAppliances().getDishwasher());
            if (p.getAppliances().getDryer() != null)
                ar.setDryer(p.getAppliances().getDryer());
            if (p.getAppliances().getMicrowave() != null)
                ar.setMicrowave(p.getAppliances().getMicrowave());
            if (p.getAppliances().getOven() != null)
                ar.setOven(p.getAppliances().getOven());
            if (p.getAppliances().getRefrigerator() != null)
                ar.setRefrigerator(p.getAppliances().getRefrigerator());
            if (p.getAppliances().getWasher() != null)
                ar.setWasher(p.getAppliances().getWasher());
            target.setApplianceRating(ar);
        }

        // FeaturesDto -> *Features entities
        if (p.getFeatures() != null) {
            // Security features
            if (p.getFeatures().getSecurity() != null) {
                SecurityFeatures sec = target.getSecurityFeatures();
                if (sec == null)
                    sec = new SecurityFeatures();
                sec.setProperty(target);
                // Support both old keys (cctvInstalled, securityGuard) and new keys (cctv,
                // securityDoor)
                sec.setHasCctv(asBoolean(p.getFeatures().getSecurity().get("cctv")));
                sec.setHasSecurityDoor(asBoolean(p.getFeatures().getSecurity().get("securityDoor")));
                target.setSecurityFeatures(sec);
            }
            // Entertainment features
            if (p.getFeatures().getEntertainment() != null) {
                EntertainmentFeatures ent = target.getEntertainmentFeatures();
                if (ent == null)
                    ent = new EntertainmentFeatures();
                ent.setProperty(target);
                // Support new keys: movieCinema, homeGym, gameRoom
                ent.setHasMovieCinema(asBoolean(p.getFeatures().getEntertainment().get("movieCinema")));
                ent.setHasHomeGym(asBoolean(p.getFeatures().getEntertainment().get("homeGym")));
                ent.setHasGameRoom(asBoolean(p.getFeatures().getEntertainment().get("gameRoom")));
                target.setEntertainmentFeatures(ent);
            }
            // Outdoor features
            if (p.getFeatures().getOutdoor() != null) {
                OutdoorFeatures out = target.getOutdoorFeatures();
                if (out == null)
                    out = new OutdoorFeatures();
                out.setProperty(target);
                // Support new keys: swimmingPool, childrenPlayground
                out.setHasSwimmingPool(asBoolean(p.getFeatures().getOutdoor().get("swimmingPool")));
                out.setHasChildrensPlayground(asBoolean(p.getFeatures().getOutdoor().get("childrenPlayground")));
                target.setOutdoorFeatures(out);
            }
        }
    }

    private static Integer asInteger(Object v) {
        if (v == null)
            return null;
        if (v instanceof Integer i)
            return i;
        if (v instanceof Number n)
            return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Boolean asBoolean(Object v) {
        if (v == null)
            return null;
        if (v instanceof Boolean b)
            return b;
        String s = String.valueOf(v).trim().toLowerCase();
        if ("true".equals(s))
            return true;
        if ("false".equals(s))
            return false;
        return null;
    }

    public static SaleListing applyToSale(SaleListing target, ListingRequest req) {
        if (target == null || req == null)
            return target;

        // pricing
        if (req.getPricing() != null) {
            if (req.getPricing().getCurrentPrice() != null)
                target.setCurrentPrice(bd(req.getPricing().getCurrentPrice()));
            if (req.getPricing().getEstimateValue() != null)
                target.setEstimateValue(bd(req.getPricing().getEstimateValue()));
        }

        // status
        String statusStr = req.getStatus();
        if (statusStr != null) {
            try {
                target.setSaleStatus(SaleListingStatus.valueOf(statusStr.trim().toUpperCase()));
            } catch (Exception ignored) {
                // keep old value if invalid
            }
        }

        // marketing
        if (req.getMarketing() != null && req.getMarketing().getMarketingDescription() != null) {
            target.setMarketingDescription(req.getMarketing().getMarketingDescription());
        }

        // media/images
        if (req.getMedia() != null && req.getMedia().getImages() != null) {
            List<ListingImage> currentImages = target.getImages();
            if (currentImages == null) {
                currentImages = new ArrayList<>();
                target.setImages(currentImages);
            }
            currentImages.clear();
            for (ListingImageDto dto : req.getMedia().getImages()) {
                ListingImage img = new ListingImage();
                img.setUrl(dto.getUrl());
                img.setIsPrimary(dto.getIsPrimary());
                img.setSaleListing(target);
                currentImages.add(img);
            }
        }

        return target;
    }

    public static RentalListing applyToRent(RentalListing target, ListingRequest req) {
        if (target == null || req == null)
            return target;

        if (req.getPricing() != null) {
            if (req.getPricing().getMonthlyRent() != null)
                target.setMonthlyRent(bd(req.getPricing().getMonthlyRent()));
            if (req.getPricing().getDepositAmount() != null)
                target.setDepositAmount(bd(req.getPricing().getDepositAmount()));
            if (req.getPricing().getMaintenanceFee() != null)
                target.setMaintenanceFee(bd(req.getPricing().getMaintenanceFee()));
        }

        if (req.getPricing() != null) {
            if (req.getPricing().getAvailableFrom() != null) {
                LocalDate d = parseDate(req.getPricing().getAvailableFrom());
                if (d != null)
                    target.setAvailableFrom(d);
            }
            if (req.getPricing().getLeaseTermMonths() != null)
                target.setLeaseTermMonths(req.getPricing().getLeaseTermMonths());
            if (req.getPricing().getPetAllowed() != null)
                target.setPetAllowed(req.getPricing().getPetAllowed());
            if (req.getPricing().getUtilitiesIncluded() != null)
                target.setUtilitiesIncluded(req.getPricing().getUtilitiesIncluded());
        }

        // status
        String statusStr = req.getStatus();
        if (statusStr != null) {
            try {
                target.setRentalStatus(RentalListingStatus.valueOf(statusStr.trim().toUpperCase()));
            } catch (Exception ignored) {
                // keep old value if invalid
            }
        }

        if (req.getMarketing() != null && req.getMarketing().getMarketingDescription() != null) {
            target.setMarketingDescription(req.getMarketing().getMarketingDescription());
        }

        // media/images for rental
        if (req.getMedia() != null && req.getMedia().getImages() != null) {
            List<RentalListingImage> currentImages = target.getImages();
            if (currentImages == null) {
                currentImages = new ArrayList<>();
                target.setImages(currentImages);
            }
            currentImages.clear();
            for (ListingImageDto dto : req.getMedia().getImages()) {
                RentalListingImage img = new RentalListingImage();
                img.setUrl(dto.getUrl());
                img.setIsPrimary(dto.getIsPrimary());
                img.setRentalListing(target);
                currentImages.add(img);
            }
        }

        return target;
    }

    public static ListingResponse toResponseFromSale(SaleListing x) {
        ListingResponse r = new ListingResponse();
        r.setId(x.getId());
        r.setListingType("SALE");

        // agent
        if (x.getAgent() != null) {
            Map<String, Object> a = new HashMap<>();
            a.put("agentId", x.getAgent().getUserId());
            a.put("name", x.getAgent().getFullName());
            // Agent extends User, so need to get email from account relationship
            String email = (x.getAgent().getAccount() != null) ? x.getAgent().getAccount().getEmail() : null;
            a.put("email", email);
            r.setAgent(mapOrNull(a));
        }

        // status
        r.setStatus(x.getSaleStatus() != null ? x.getSaleStatus().name() : null);

        // property
        Property p = x.getProperty();
        if (p != null) {
            Map<String, Object> pm = new HashMap<>();
            pm.put("propertyId", p.getPropertyId());
            pm.put("ownerId", p.getOwner() != null ? p.getOwner().getUserId() : null);
            pm.put("propertyType", propertyTypeOf(p));
            pm.put("yearBuilt", p.getYearBuilt());
            pm.put("floors", p.getFloors());
            pm.put("beds", p.getBeds());
            pm.put("baths", p.getBaths());
            pm.put("area", p.getArea());
            pm.put("description", p.getDescription());
            pm.put("transport", transportToMap(p.getTransportRating()));
            pm.put("appliances", applianceToMap(p.getApplianceRating()));
            pm.put("features", featuresToMap(p));
            r.setProperty(mapOrNull(pm));

            // address
            if (p.getAddress() != null) {
                Address ad = p.getAddress();
                Map<String, Object> am = new HashMap<>();
                am.put("addressId", ad.getAddressId());
                am.put("zipCode", ad.getZipCode());
                am.put("city", ad.getCity());
                am.put("province", ad.getProvince());
                am.put("street", ad.getStreet());
                am.put("nation", ad.getNation());
                am.put("latitude", ad.getLatitude());
                am.put("longitude", ad.getLongitude());
                r.setAddress(mapOrNull(am));
            }
        }

        // pricing
        Map<String, Object> pricing = new HashMap<>();
        pricing.put("currentPrice", x.getCurrentPrice());
        pricing.put("estimateValue", x.getEstimateValue());
        r.setPricing(mapOrNull(pricing));

        // marketing
        Map<String, Object> m = new HashMap<>();
        m.put("marketingDescription", x.getMarketingDescription());
        r.setMarketing(mapOrNull(m));
        // media/images ✅ (FE expects media.images)
        Map<String, Object> media = new HashMap<>();
        List<Map<String, Object>> imgs = new ArrayList<>();
        if (x.getImages() != null) {
            for (ListingImage li : x.getImages()) {
                Map<String, Object> im = new HashMap<>();
                im.put("url", li.getUrl());
                im.put("isPrimary", li.getIsPrimary());
                imgs.add(im);
            }
        }
        media.put("images", imgs);
        r.setMedia(mapOrNull(media));

        return r;
    }

    public static ListingResponse toResponseFromRent(RentalListing x) {
        ListingResponse r = new ListingResponse();
        r.setId(x.getId());
        r.setListingType("RENT");

        r.setStatus(x.getRentalStatus() != null ? x.getRentalStatus().name() : null);

        Property p = x.getProperty();
        if (p != null) {
            Map<String, Object> pm = new HashMap<>();
            pm.put("propertyId", p.getPropertyId());
            pm.put("ownerId", p.getOwner() != null ? p.getOwner().getUserId() : null);
            pm.put("propertyType", propertyTypeOf(p));
            pm.put("yearBuilt", p.getYearBuilt());
            pm.put("floors", p.getFloors());
            pm.put("beds", p.getBeds());
            pm.put("baths", p.getBaths());
            pm.put("area", p.getArea());
            pm.put("description", p.getDescription());
            pm.put("transport", transportToMap(p.getTransportRating()));
            pm.put("appliances", applianceToMap(p.getApplianceRating()));
            pm.put("features", featuresToMap(p));
            r.setProperty(mapOrNull(pm));

            if (p.getAddress() != null) {
                Address ad = p.getAddress();
                Map<String, Object> am = new HashMap<>();
                am.put("addressId", ad.getAddressId());
                am.put("zipCode", ad.getZipCode());
                am.put("city", ad.getCity());
                am.put("province", ad.getProvince());
                am.put("street", ad.getStreet());
                am.put("nation", ad.getNation());
                am.put("latitude", ad.getLatitude());
                am.put("longitude", ad.getLongitude());
                r.setAddress(mapOrNull(am));
            }
        }

        Map<String, Object> pricing = new HashMap<>();
        pricing.put("monthlyRent", x.getMonthlyRent());
        pricing.put("depositAmount", x.getDepositAmount());
        pricing.put("maintenanceFee", x.getMaintenanceFee());
        pricing.put("availableFrom", x.getAvailableFrom() != null ? x.getAvailableFrom().toString() : null);
        pricing.put("leaseTermMonths", x.getLeaseTermMonths());
        pricing.put("petAllowed", x.getPetAllowed());
        pricing.put("utilitiesIncluded", x.getUtilitiesIncluded());
        r.setPricing(mapOrNull(pricing));

        Map<String, Object> m = new HashMap<>();
        m.put("marketingDescription", x.getMarketingDescription());
        r.setMarketing(mapOrNull(m));

        // media/images for rental
        Map<String, Object> media = new HashMap<>();
        List<Map<String, Object>> imgs = new ArrayList<>();
        if (x.getImages() != null) {
            for (RentalListingImage rli : x.getImages()) {
                Map<String, Object> im = new HashMap<>();
                im.put("url", rli.getUrl());
                im.put("isPrimary", rli.getIsPrimary());
                imgs.add(im);
            }
        }
        media.put("images", imgs);
        r.setMedia(mapOrNull(media));

        return r;
    }

}
