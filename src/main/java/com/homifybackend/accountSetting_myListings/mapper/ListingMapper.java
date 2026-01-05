package com.homifybackend.accountSetting_myListings.mapper;

import com.homifybackend.accountSetting_myListings.dto.listing.ListingImageDto;
import com.homifybackend.accountSetting_myListings.dto.listing.ListingRequest;
import com.homifybackend.accountSetting_myListings.dto.listing.ListingResponse;
import com.homifybackend.accountSetting_myListings.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ListingMapper {

  private ListingMapper() {}

  private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public static String propertyTypeOf(Property p) {
    if (p instanceof TownHouse) return "TOWN_HOUSE";
    if (p instanceof SingleHouse) return "SINGLE_HOUSE";
    if (p instanceof Apartment) return "APARTMENT";
    if (p instanceof Villa) return "VILLA";
    return null;
  }

  private static String iso(LocalDateTime t) {
    return t == null ? null : t.format(ISO);
  }

  private static BigDecimal bd(Object o) {
    if (o == null) return null;
    if (o instanceof BigDecimal b) return b;
    try { return new BigDecimal(o.toString()); } catch (Exception e) { return null; }
  }

  public static void applyToProperty(Property target, ListingRequest req) {
    if (target == null || req == null) return;
    var p = req.getProperty();
    if (p != null) {
      if (p.getYearBuilt() != null) target.setYearBuilt(p.getYearBuilt());
      if (p.getFloors() != null) target.setFloors(p.getFloors());
      if (p.getBeds() != null) target.setBeds(p.getBeds());
      if (p.getBaths() != null) target.setBaths(p.getBaths());
      if (p.getArea() != null) target.setArea(p.getArea());
      if (p.getDescription() != null) target.setDescription(p.getDescription());

      // Ratings
      if (p.getTransportRatings() != null) {
        TransportRating tr = target.getTransportRating();
        if (tr == null) tr = new TransportRating();
        Object w = p.getTransportRatings().get("walkScore");
        Object b = p.getTransportRatings().get("bikeScore");
        Object t = p.getTransportRatings().get("transitScore");
        if (w != null) tr.setWalkScore(Integer.valueOf(w.toString()));
        if (b != null) tr.setBikeScore(Integer.valueOf(b.toString()));
        if (t != null) tr.setTransitScore(Integer.valueOf(t.toString()));
        target.setTransportRating(tr);
      }

      if (p.getAppliances() != null) {
        ApplianceRating ar = target.getApplianceRating();
        if (ar == null) ar = new ApplianceRating();
        Map<String,Object> a = p.getAppliances();
        if (a.containsKey("dishwasher")) ar.setDishwasher(Boolean.valueOf(a.get("dishwasher").toString()));
        if (a.containsKey("dryer")) ar.setDryer(Boolean.valueOf(a.get("dryer").toString()));
        if (a.containsKey("microwave")) ar.setMicrowave(Boolean.valueOf(a.get("microwave").toString()));
        if (a.containsKey("oven")) ar.setOven(Boolean.valueOf(a.get("oven").toString()));
        if (a.containsKey("refrigerator")) ar.setRefrigerator(Boolean.valueOf(a.get("refrigerator").toString()));
        if (a.containsKey("washer")) ar.setWasher(Boolean.valueOf(a.get("washer").toString()));
        target.setApplianceRating(ar);
      }

      // Amenities -> Features
      if (p.getAmenities() != null) {
        Map<String,Object> am = p.getAmenities();
        if (am.get("security") instanceof Map<?,?> sec) {
          SecurityFeatures sf = target.getSecurityFeatures();
          if (sf == null) { sf = new SecurityFeatures(); sf.setProperty(target); }
          if (sec.containsKey("hasSecurityDoor")) sf.setHasSecurityDoor(Boolean.valueOf(sec.get("hasSecurityDoor").toString()));
          if (sec.containsKey("hasCctv")) sf.setHasCctv(Boolean.valueOf(sec.get("hasCctv").toString()));
          target.setSecurityFeatures(sf);
        }
        if (am.get("outdoor") instanceof Map<?,?> out) {
          OutdoorFeatures of = target.getOutdoorFeatures();
          if (of == null) { of = new OutdoorFeatures(); of.setProperty(target); }
          if (out.containsKey("hasSwimmingPool")) of.setHasSwimmingPool(Boolean.valueOf(out.get("hasSwimmingPool").toString()));
          if (out.containsKey("hasChildrensPlayground")) of.setHasChildrensPlayground(Boolean.valueOf(out.get("hasChildrensPlayground").toString()));
          target.setOutdoorFeatures(of);
        }
        if (am.get("entertainment") instanceof Map<?,?> ent) {
          EntertainmentFeatures ef = target.getEntertainmentFeatures();
          if (ef == null) { ef = new EntertainmentFeatures(); ef.setProperty(target); }
          if (ent.containsKey("hasMovieCinema")) ef.setHasMovieCinema(Boolean.valueOf(ent.get("hasMovieCinema").toString()));
          if (ent.containsKey("hasHomeGym")) ef.setHasHomeGym(Boolean.valueOf(ent.get("hasHomeGym").toString()));
          if (ent.containsKey("hasGameRoom")) ef.setHasGameRoom(Boolean.valueOf(ent.get("hasGameRoom").toString()));
          target.setEntertainmentFeatures(ef);
        }
      }

      // Subtype
      if (p.getSubtype() != null) {
        Map<String,Object> st = p.getSubtype();
        if (target instanceof TownHouse th) {
          if (st.get("landArea") != null) th.setLandArea(Double.valueOf(st.get("landArea").toString()));
          if (st.get("numberOfFloors") != null) th.setNumberOfFloors(Integer.valueOf(st.get("numberOfFloors").toString()));
          if (st.get("cornerLot") != null) th.setCornerLot(Boolean.valueOf(st.get("cornerLot").toString()));
          if (st.get("frontWidth") != null) th.setFrontWidth(Double.valueOf(st.get("frontWidth").toString()));
          if (st.get("depth") != null) th.setDepth(Double.valueOf(st.get("depth").toString()));
          if (st.get("carAccessible") != null) th.setCarAccessible(Boolean.valueOf(st.get("carAccessible").toString()));
          if (st.get("cctvInstalled") != null) th.setCctvInstalled(Boolean.valueOf(st.get("cctvInstalled").toString()));
          if (st.get("maintenanceFee") != null) th.setMaintenanceFee(bd(st.get("maintenanceFee")));
          if (st.get("clubhouseAccess") != null) th.setClubhouseAccess(Boolean.valueOf(st.get("clubhouseAccess").toString()));
          if (st.get("poolAccess") != null) th.setPoolAccess(Boolean.valueOf(st.get("poolAccess").toString()));
          if (st.get("gymAccess") != null) th.setGymAccess(Boolean.valueOf(st.get("gymAccess").toString()));
          if (st.get("greenSpace") != null) th.setGreenSpace(Boolean.valueOf(st.get("greenSpace").toString()));
          if (st.get("roadWidth") != null) th.setRoadWidth(Double.valueOf(st.get("roadWidth").toString()));
        }
        if (target instanceof Apartment ap) {
          if (st.get("usableArea") != null) ap.setUsableArea(Double.valueOf(st.get("usableArea").toString()));
          if (st.get("maintenanceFee") != null) ap.setMaintenanceFee(bd(st.get("maintenanceFee")));
          if (st.get("level") != null) ap.setLevel(Integer.valueOf(st.get("level").toString()));
          if (st.get("hasElevatorAccess") != null) ap.setHasElevatorAccess(Boolean.valueOf(st.get("hasElevatorAccess").toString()));
          if (st.get("petAllowed") != null) ap.setPetAllowed(Boolean.valueOf(st.get("petAllowed").toString()));
          if (st.get("sharedFacilities") != null) ap.setSharedFacilities(Boolean.valueOf(st.get("sharedFacilities").toString()));
          if (st.get("totalBuildingFloors") != null) ap.setTotalBuildingFloors(Integer.valueOf(st.get("totalBuildingFloors").toString()));
          if (st.get("balcony") != null) ap.setBalcony(Boolean.valueOf(st.get("balcony").toString()));
        }
        if (target instanceof Villa v) {
          if (st.get("lotArea") != null) v.setLotArea(Double.valueOf(st.get("lotArea").toString()));
          if (st.get("backyardArea") != null) v.setBackyardArea(Double.valueOf(st.get("backyardArea").toString()));
          if (st.get("frontYardArea") != null) v.setFrontYardArea(Double.valueOf(st.get("frontYardArea").toString()));
          if (st.get("gardenArea") != null) v.setGardenArea(Double.valueOf(st.get("gardenArea").toString()));
          if (st.get("parkingSpaces") != null) v.setParkingSpaces(Integer.valueOf(st.get("parkingSpaces").toString()));
          if (st.get("hasGarage") != null) v.setHasGarage(Boolean.valueOf(st.get("hasGarage").toString()));
          if (st.get("hasBasement") != null) v.setHasBasement(Boolean.valueOf(st.get("hasBasement").toString()));
          if (st.get("garageArea") != null) v.setGarageArea(Double.valueOf(st.get("garageArea").toString()));
          if (st.get("viewType") != null) v.setViewType(st.get("viewType").toString());
          if (st.get("smartHomeLevel") != null) v.setSmartHomeLevel(Integer.valueOf(st.get("smartHomeLevel").toString()));
          if (st.get("serviceArea") != null) v.setServiceArea(Double.valueOf(st.get("serviceArea").toString()));
        }
        if (target instanceof SingleHouse sh) {
          if (st.get("landArea") != null) sh.setLandArea(Double.valueOf(st.get("landArea").toString()));
          if (st.get("backyardArea") != null) sh.setBackyardArea(Double.valueOf(st.get("backyardArea").toString()));
          if (st.get("frontYardArea") != null) sh.setFrontYardArea(Double.valueOf(st.get("frontYardArea").toString()));
          if (st.get("hasGarage") != null) sh.setHasGarage(Boolean.valueOf(st.get("hasGarage").toString()));
          if (st.get("hasBasement") != null) sh.setHasBasement(Boolean.valueOf(st.get("hasBasement").toString()));
        }
      }
    }

    // Rooms: replace-all strategy (simpler, FE sends full list)
    if (req.getRooms() != null) {
      List<Room> rooms = new ArrayList<>();
      for (var rd : req.getRooms()) {
        Room r = new Room();
        r.setRoomId(rd.getRoomId());
        r.setProperty(target);
        r.setType(rd.getType());
        r.setArea(rd.getArea());
        r.setWidth(rd.getWidth());
        r.setLength(rd.getLength());
        r.setHeight(rd.getHeight());
        rooms.add(r);
      }
      target.getRooms().clear();
      target.getRooms().addAll(rooms);
    }
  }

  public static SaleListing applyToSale(SaleListing target, ListingRequest req) {
    if (target == null || req == null) return target;
    if (req.getCurrentPrice() != null) target.setCurrentPrice(bd(req.getCurrentPrice()));
    if (req.getEstimateValue() != null) target.setEstimateValue(bd(req.getEstimateValue()));
    if (req.getSaleStatus() != null) target.setSaleStatus(SaleListingStatus.valueOf(req.getSaleStatus()));
    if (req.getMarketingDescription() != null) target.setMarketingDescription(req.getMarketingDescription());
    return target;
  }

  public static RentalListing applyToRent(RentalListing target, ListingRequest req) {
    if (target == null || req == null) return target;
    if (req.getMonthlyRent() != null) target.setMonthlyRent(bd(req.getMonthlyRent()));
    if (req.getDepositAmount() != null) target.setDepositAmount(bd(req.getDepositAmount()));
    if (req.getMaintenanceFee() != null) target.setMaintenanceFee(bd(req.getMaintenanceFee()));
    if (req.getAvailableFrom() != null) target.setAvailableFrom(req.getAvailableFrom());
    if (req.getLeaseTermMonths() != null) target.setLeaseTermMonths(req.getLeaseTermMonths());
    if (req.getPetAllowed() != null) target.setPetAllowed(req.getPetAllowed());
    if (req.getUtilitiesIncluded() != null) target.setUtilitiesIncluded(req.getUtilitiesIncluded());
    if (req.getListingStatus() != null) target.setListingStatus(RentalListingStatus.valueOf(req.getListingStatus()));
    if (req.getMarketingDescription() != null) target.setMarketingDescription(req.getMarketingDescription());
    return target;
  }

  public static ListingResponse toResponseFromSale(SaleListing s) {
    ListingResponse r = new ListingResponse();
    r.setId(s.getId());
    r.setListingType("SALE");
    r.setStatus(s.getSaleStatus() != null ? s.getSaleStatus().name() : null);
    r.setCurrentPrice(s.getCurrentPrice());
    r.setEstimateValue(s.getEstimateValue());
    r.setMarketingDescription(s.getMarketingDescription());
    r.setDateListed(iso(s.getDateListed()));

    if (s.getAgent() != null) r.setAgentId(s.getAgent().getUserId());
    if (s.getProperty() != null) {
      Property p = s.getProperty();
      r.setPropertyId(p.getPropertyId());
      r.setOwnerId(p.getOwner() != null ? p.getOwner().getUserId() : null);
      r.setPropertyType(propertyTypeOf(p));
      r.setYearBuilt(p.getYearBuilt());
      r.setFloors(p.getFloors());
      r.setBeds(p.getBeds());
      r.setBaths(p.getBaths());
      r.setArea(p.getArea());
      r.setDescription(p.getDescription());
      if (p.getAddress() != null) {
        r.setAddressId(p.getAddress().getAddressId());
        r.setZipCode(p.getAddress().getZipCode());
        r.setCity(p.getAddress().getCity());
        r.setProvince(p.getAddress().getProvince());
        r.setStreet(p.getAddress().getStreet());
        r.setNation(p.getAddress().getNation());
        r.setLatitude(p.getAddress().getLatitude());
        r.setLongitude(p.getAddress().getLongitude());
      }
      // images
      if (s.getImages() != null) {
        List<ListingImageDto> imgs = new ArrayList<>();
        s.getImages().forEach(img -> imgs.add(new ListingImageDto(img.getId(), img.getUrl(), img.getIsPrimary())));
        r.setImages(imgs);
      }
    }
    return r;
  }

  public static ListingResponse toResponseFromRent(RentalListing x) {
    ListingResponse r = new ListingResponse();
    r.setId(x.getId());
    r.setListingType("RENT");
    r.setStatus(x.getListingStatus() != null ? x.getListingStatus().name() : null);
    r.setMonthlyRent(x.getMonthlyRent());
    r.setDepositAmount(x.getDepositAmount());
    r.setMaintenanceFee(x.getMaintenanceFee());
    r.setAvailableFrom(x.getAvailableFrom());
    r.setLeaseTermMonths(x.getLeaseTermMonths());
    r.setPetAllowed(x.getPetAllowed());
    r.setUtilitiesIncluded(x.getUtilitiesIncluded());
    r.setMarketingDescription(x.getMarketingDescription());
    r.setDateListed(iso(x.getDateListed()));

    if (x.getProperty() != null) {
      Property p = x.getProperty();
      r.setPropertyId(p.getPropertyId());
      r.setOwnerId(p.getOwner() != null ? p.getOwner().getUserId() : null);
      r.setPropertyType(propertyTypeOf(p));
      r.setYearBuilt(p.getYearBuilt());
      r.setFloors(p.getFloors());
      r.setBeds(p.getBeds());
      r.setBaths(p.getBaths());
      r.setArea(p.getArea());
      r.setDescription(p.getDescription());
      if (p.getAddress() != null) {
        r.setAddressId(p.getAddress().getAddressId());
        r.setZipCode(p.getAddress().getZipCode());
        r.setCity(p.getAddress().getCity());
        r.setProvince(p.getAddress().getProvince());
        r.setStreet(p.getAddress().getStreet());
        r.setNation(p.getAddress().getNation());
        r.setLatitude(p.getAddress().getLatitude());
        r.setLongitude(p.getAddress().getLongitude());
      }
    }
    return r;
  }
}
