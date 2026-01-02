package com.homifybackend.manageRentals.mapper;

import com.homifybackend.manageRentals.dto.*;
import com.homifybackend.model.*;
import com.homifybackend.manageRentals.repository.RentalContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RentalPropertyMapper {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private RentalContractRepository rentalContractRepository;

  public RentalPropertyDTO toDto(Property property) {
    if (property == null) return null;

    RentalPropertyDTO dto = new RentalPropertyDTO();

    // Core
    dto.setAddress(mapAddress(property.getAddress()));
    dto.setProperty(mapProperty(property));

    // Features
    if (property.getSecurityFeatures() != null) dto.setSecurityFeatures(mapSecurityFeatures(property.getSecurityFeatures()));
    if (property.getOutdoorFeatures() != null) dto.setOutdoorFeatures(mapOutdoorFeatures(property.getOutdoorFeatures()));
    if (property.getEntertainmentFeatures() != null) dto.setEntertainmentFeatures(mapEntertainmentFeatures(property.getEntertainmentFeatures()));

    // Ratings
    if (property.getTransportRating() != null) dto.setTransportRating(mapTransportRating(property.getTransportRating()));
    if (property.getApplianceRating() != null) dto.setApplianceRating(mapApplianceRating(property.getApplianceRating()));

    // Rental Listing + Images
    if (property.getRentalListing() != null) {
      RentalListing rl = property.getRentalListing();
      dto.setRentalListing(mapRentalListing(rl));

      dto.setRentalListingImages(
          rl.getImages().stream()
              .map(this::mapImage)
              .collect(Collectors.toList())
      );

      // Chỉ khi RENTED mới query thêm contract + tenant
      if ("RENTED".equals(rl.getListingStatus())) {
        rentalContractRepository.findByRentalListing_Id(rl.getId())
            .ifPresent(contract -> {
              dto.setRentalContract(mapRentalContract(contract));
              if (contract.getTenant() != null) {
                dto.setTenant(userMapper.toDto(contract.getTenant()));
              }
            });
      }
    }

    // SUBTYPE MAPPING (thay thế phần set null cũ)
    dto.setApartment(mapApartment(property.getApartment()));
    dto.setTownHouse(mapTownHouse(property.getTownHouse()));
    dto.setSingleHouse(mapSingleHouse(property.getSingleHouse()));
    dto.setVilla(mapVilla(property.getVilla()));

    return dto;
  }

  // ==================== Mapping methods ====================

  private AddressDTO mapAddress(Address a) {
    if (a == null) return null;
    AddressDTO d = new AddressDTO();
    d.setAddressId(a.getAddressId());
    d.setZipCode(a.getZipCode());
    d.setCity(a.getCity());
    d.setProvince(a.getProvince());
    d.setStreet(a.getStreet());
    d.setNation(a.getNation());
    d.setLatitude(a.getLatitude());
    d.setLongitude(a.getLongitude());
    return d;
  }

  private PropertyDTO mapProperty(Property p) {
    if (p == null) return null;
    PropertyDTO d = new PropertyDTO();
    d.setPropertyId(p.getPropertyId());
    d.setOwnerId(p.getOwner().getUserId());
    d.setAddressId(p.getAddress().getAddressId());
    d.setYearBuilt(p.getYearBuilt());
    d.setFloors(p.getFloors());
    d.setBeds(p.getBeds());
    d.setBaths(p.getBaths());
    d.setArea(p.getArea());
    d.setDescription(p.getDescription());
    d.setTransportRatingId(p.getTransportRating() != null ? p.getTransportRating().getRatingId() : null);
    d.setApplianceRatingId(p.getApplianceRating() != null ? p.getApplianceRating().getRatingId() : null);
    d.setCreatedAt(p.getCreatedAt());
    return d;
  }

  private RentalListingDTO mapRentalListing(RentalListing rl) {
    if (rl == null) return null;
    RentalListingDTO d = new RentalListingDTO();
    d.setId(rl.getId());
    d.setPropertyId(rl.getProperty().getPropertyId());
    d.setMonthlyRent(rl.getMonthlyRent());
    d.setDepositAmount(rl.getDepositAmount());
    d.setMaintenanceFee(rl.getMaintenanceFee());
    d.setAvailableFrom(rl.getAvailableFrom());
    d.setLeaseTermMonths(rl.getLeaseTermMonths());
    d.setPetAllowed(rl.getPetAllowed());
    d.setUtilitiesIncluded(rl.getUtilitiesIncluded());
    d.setListingStatus(rl.getListingStatus());
    d.setMarketingDescription(rl.getMarketingDescription());
    d.setDateListed(rl.getDateListed());
    d.setDateRented(rl.getDateRented());
    return d;
  }

  private RentalListingImageDTO mapImage(RentalListingImage img) {
    if (img == null) return null;
    RentalListingImageDTO d = new RentalListingImageDTO();
    d.setId(img.getId());
    d.setRentalListingId(img.getRentalListing().getId());
    d.setUrl(img.getUrl());
    d.setIsPrimary(img.getIsPrimary());
    return d;
  }

  private RentalContractDTO mapRentalContract(RentalContract c) {
    if (c == null) return null;
    RentalContractDTO d = new RentalContractDTO();
    d.setId(c.getId());
    d.setRentalListingId(c.getRentalListing().getId());
    d.setTenantId(c.getTenant().getUserId());
    d.setMonthlyRent(c.getMonthlyRent());
    d.setDepositPaid(c.getDepositPaid());
    d.setStartDate(c.getStartDate());
    d.setEndDate(c.getEndDate());
    d.setSignedDate(c.getSignedDate());
    d.setContractStatus(c.getContractStatus());
    d.setPaymentDueDay(c.getPaymentDueDay());
    return d;
  }

  private SecurityFeaturesDTO mapSecurityFeatures(SecurityFeatures sf) {
    if (sf == null) return null;
    SecurityFeaturesDTO d = new SecurityFeaturesDTO();
    d.setPropertyId(sf.getPropertyId());
    d.setHasSecurityDoor(sf.getHasSecurityDoor());
    d.setHasCctv(sf.getHasCctv());
    return d;
  }

  private OutdoorFeaturesDTO mapOutdoorFeatures(OutdoorFeatures of) {
    if (of == null) return null;
    OutdoorFeaturesDTO d = new OutdoorFeaturesDTO();
    d.setPropertyId(of.getPropertyId());
    d.setHasSwimmingPool(of.getHasSwimmingPool());
    d.setHasChildrensPlayground(of.getHasChildrensPlayground());
    return d;
  }

  private EntertainmentFeaturesDTO mapEntertainmentFeatures(EntertainmentFeatures ef) {
    if (ef == null) return null;
    EntertainmentFeaturesDTO d = new EntertainmentFeaturesDTO();
    d.setPropertyId(ef.getPropertyId());
    d.setHasMovieCinema(ef.getHasMovieCinema());
    d.setHasHomeGym(ef.getHasHomeGym());
    d.setHasGameRoom(ef.getHasGameRoom());
    return d;
  }

  private TransportRatingDTO mapTransportRating(TransportRating tr) {
    if (tr == null) return null;
    TransportRatingDTO d = new TransportRatingDTO();
    d.setRatingId(tr.getRatingId());
    d.setWalkScore(tr.getWalkScore());
    d.setBikeScore(tr.getBikeScore());
    d.setTransitScore(tr.getTransitScore());
    return d;
  }

  private ApplianceRatingDTO mapApplianceRating(ApplianceRating ar) {
    if (ar == null) return null;
    ApplianceRatingDTO d = new ApplianceRatingDTO();
    d.setRatingId(ar.getRatingId());
    d.setDishwasher(ar.getDishwasher());
    d.setDryer(ar.getDryer());
    d.setMicrowave(ar.getMicrowave());
    d.setOven(ar.getOven());
    d.setRefrigerator(ar.getRefrigerator());
    d.setWasher(ar.getWasher());
    return d;
  }

  // ==================== Subtype mapping methods ====================

  private ApartmentDTO mapApartment(Apartment a) {
    if (a == null) return null;
    ApartmentDTO d = new ApartmentDTO();
    d.setPropertyId(a.getPropertyId());
    d.setUsableArea(a.getUsableArea());
    d.setMaintenanceFee(a.getMaintenanceFee());
    d.setLevel(a.getLevel());
    d.setHasElevatorAccess(a.getHasElevatorAccess());
    d.setPetAllowed(a.getPetAllowed());
    d.setSharedFacilities(a.getSharedFacilities());
    d.setTotalBuildingFloors(a.getTotalBuildingFloors());
    d.setBalcony(a.getBalcony());
    return d;
  }

  private TownHouseDTO mapTownHouse(TownHouse t) {
    if (t == null) return null;
    TownHouseDTO d = new TownHouseDTO();
    d.setPropertyId(t.getPropertyId());
    d.setLandArea(t.getLandArea());
    d.setNumberOfFloors(t.getNumberOfFloors());
    d.setCornerLot(t.getCornerLot());
    d.setFrontWidth(t.getFrontWidth());
    d.setDepth(t.getDepth());
    d.setCarAccessible(t.getCarAccessible());
    d.setCctvInstalled(t.getCctvInstalled());
    d.setMaintenanceFee(t.getMaintenanceFee());
    d.setClubhouseAccess(t.getClubhouseAccess());
    d.setPoolAccess(t.getPoolAccess());
    d.setGymAccess(t.getGymAccess());
    d.setGreenSpace(t.getGreenSpace());
    d.setRoadWidth(t.getRoadWidth());
    return d;
  }

  private SingleHouseDTO mapSingleHouse(SingleHouse s) {
    if (s == null) return null;
    SingleHouseDTO d = new SingleHouseDTO();
    d.setPropertyId(s.getPropertyId());
    d.setLandArea(s.getLandArea());
    d.setBackyardArea(s.getBackyardArea());
    d.setFrontYardArea(s.getFrontYardArea());
    d.setHasGarage(s.getHasGarage());
    d.setHasBasement(s.getHasBasement());
    return d;
  }

  private VillaDTO mapVilla(Villa v) {
    if (v == null) return null;
    VillaDTO d = new VillaDTO();
    d.setPropertyId(v.getPropertyId());
    d.setLotArea(v.getLotArea());
    d.setBackyardArea(v.getBackyardArea());
    d.setFrontYardArea(v.getFrontYardArea());
    d.setGardenArea(v.getGardenArea());
    d.setParkingSpaces(v.getParkingSpaces());
    d.setHasGarage(v.getHasGarage());
    d.setHasBasement(v.getHasBasement());
    d.setGarageArea(v.getGarageArea());
    d.setViewType(v.getViewType());
    d.setSmartHomeLevel(v.getSmartHomeLevel());
    d.setServiceArea(v.getServiceArea());
    return d;
  }
  // ==================== MỚI THÊM: MAPPER NGƯỢC DTO → ENTITY ====================

  /**
   * Apply dữ liệu từ DTO vào entity hiện có (dùng cho update)
   */
// ==================== DTO -> ENTITY (ADD/UPDATE) ====================
  public void applyDtoToEntity(RentalPropertyDTO dto, Property entity) {
    if (dto == null || entity == null) return;

    // =========================================================
    // STEP 1: Address
    // =========================================================
    if (dto.getAddress() != null) {
      AddressDTO ad = dto.getAddress();
      Address addr = entity.getAddress();

      // Nếu ADD mới hoặc entity chưa có address
      if (addr == null) {
        addr = new Address();
        // nếu Address có @GeneratedValue thì KHÔNG nên set id
        // nếu Address id do client gửi (hiếm) thì mới set:
        if (ad.getAddressId() != null) addr.setAddressId(ad.getAddressId());
        entity.setAddress(addr);
      }

      // Update fields (chỉ set khi != null để tránh overwrite bằng null)
      if (ad.getStreet() != null) addr.setStreet(ad.getStreet());
      if (ad.getCity() != null) addr.setCity(ad.getCity());
      if (ad.getProvince() != null) addr.setProvince(ad.getProvince());
      if (ad.getZipCode() != null) addr.setZipCode(ad.getZipCode());
      if (ad.getNation() != null) addr.setNation(ad.getNation());
      if (ad.getLatitude() != null) addr.setLatitude(ad.getLatitude());
      if (ad.getLongitude() != null) addr.setLongitude(ad.getLongitude());
    }

    // =========================================================
    // STEP 2 + STEP 5: Property core fields (yearBuilt, floors, desc...)
    // =========================================================
    if (dto.getProperty() != null) {
      PropertyDTO p = dto.getProperty();

      if (p.getYearBuilt() != null) entity.setYearBuilt(p.getYearBuilt());
      if (p.getFloors() != null) entity.setFloors(p.getFloors());
      if (p.getBeds() != null) entity.setBeds(p.getBeds());
      if (p.getBaths() != null) entity.setBaths(p.getBaths());
      if (p.getArea() != null) entity.setArea(p.getArea());
      if (p.getDescription() != null) entity.setDescription(p.getDescription());

      // owner / addressId / ratingId: thường không update ở mapper (quan hệ DB)
      // - Owner: nên set trong Service khi ADD (CURRENT_OWNER_ID)
      // - transportRatingId/applianceRatingId: nếu bạn muốn update thì phải repo.findById rồi set entity.setTransportRating(...)
    }

    // =========================================================
    // STEP 3: RentalListing (monthlyRent, deposit, availableFrom...)
    // =========================================================
    if (dto.getRentalListing() != null) {
      RentalListingDTO rld = dto.getRentalListing();
      RentalListing rl = entity.getRentalListing();

      if (rl == null) {
        rl = new RentalListing();
        rl.setProperty(entity);
        // id chỉ set nếu bạn chắc chắn đây là update và id tồn tại
        if (rld.getId() != null) rl.setId(rld.getId());
        entity.setRentalListing(rl);
      }

      if (rld.getMonthlyRent() != null) rl.setMonthlyRent(rld.getMonthlyRent());
      if (rld.getDepositAmount() != null) rl.setDepositAmount(rld.getDepositAmount());
      if (rld.getMaintenanceFee() != null) rl.setMaintenanceFee(rld.getMaintenanceFee());
      if (rld.getAvailableFrom() != null) rl.setAvailableFrom(rld.getAvailableFrom());
      if (rld.getLeaseTermMonths() != null) rl.setLeaseTermMonths(rld.getLeaseTermMonths());
      if (rld.getPetAllowed() != null) rl.setPetAllowed(rld.getPetAllowed());
      if (rld.getUtilitiesIncluded() != null) rl.setUtilitiesIncluded(rld.getUtilitiesIncluded());
      if (rld.getMarketingDescription() != null) rl.setMarketingDescription(rld.getMarketingDescription());

      if (rld.getListingStatus() != null) {
        rl.setListingStatus(rld.getListingStatus().toUpperCase());
      }

      // dateListed/dateRented: thường server tự set theo logic business
      // nếu muốn cho update thì mở:
      if (rld.getDateListed() != null) rl.setDateListed(rld.getDateListed());
      if (rld.getDateRented() != null) rl.setDateRented(rld.getDateRented());
    }

    // =========================================================
    // STEP 5 (images): rentalListingImages (sync list)
    // =========================================================
    if (dto.getRentalListingImages() != null) {
      // cần rentalListing tồn tại để gắn FK
      RentalListing rl = entity.getRentalListing();
      if (rl == null) {
        rl = new RentalListing();
        rl.setProperty(entity);
        entity.setRentalListing(rl);
      }

      // orphanRemoval=true => clear rồi add lại là an toàn nhất
      rl.getImages().clear();

      for (RentalListingImageDTO imgDto : dto.getRentalListingImages()) {
        if (imgDto == null) continue;

        RentalListingImage img = new RentalListingImage();
        if (imgDto.getId() != null) img.setId(imgDto.getId());
        img.setRentalListing(rl);

        if (imgDto.getUrl() != null) img.setUrl(imgDto.getUrl());
        if (imgDto.getIsPrimary() != null) img.setIsPrimary(imgDto.getIsPrimary());

        rl.getImages().add(img);
      }
    }

    // =========================================================
    // STEP 4: Features / Amenities blocks (security/outdoor/entertainment/applianceRating)
    // =========================================================

    if (dto.getSecurityFeatures() == null) {
      // FE không gửi block => hiểu là không chọn gì => xóa block cho sạch
      entity.setSecurityFeatures(null); // orphanRemoval => delete record
    } else {
      SecurityFeaturesDTO sfd = dto.getSecurityFeatures();
      SecurityFeatures sf = entity.getSecurityFeatures();
      if (sf == null) {
        sf = new SecurityFeatures();
        sf.setProperty(entity);     // @MapsId cần setProperty
        entity.setSecurityFeatures(sf);
      }
      // overwrite đầy đủ (null coi như false)
      sf.setHasSecurityDoor(Boolean.TRUE.equals(sfd.getHasSecurityDoor()));
      sf.setHasCctv(Boolean.TRUE.equals(sfd.getHasCctv()));
    }

    // --- OutdoorFeatures ---
    if (dto.getOutdoorFeatures() == null) {
      entity.setOutdoorFeatures(null);
    } else {
      OutdoorFeaturesDTO ofd = dto.getOutdoorFeatures();
      OutdoorFeatures of = entity.getOutdoorFeatures();
      if (of == null) {
        of = new OutdoorFeatures();
        of.setProperty(entity);
        entity.setOutdoorFeatures(of);
      }
      of.setHasSwimmingPool(Boolean.TRUE.equals(ofd.getHasSwimmingPool()));
      of.setHasChildrensPlayground(Boolean.TRUE.equals(ofd.getHasChildrensPlayground()));
    }

    // --- EntertainmentFeatures ---
    if (dto.getEntertainmentFeatures() == null) {
      entity.setEntertainmentFeatures(null);
    } else {
      EntertainmentFeaturesDTO efd = dto.getEntertainmentFeatures();
      EntertainmentFeatures ef = entity.getEntertainmentFeatures();
      if (ef == null) {
        ef = new EntertainmentFeatures();
        ef.setProperty(entity);
        entity.setEntertainmentFeatures(ef);
      }
      ef.setHasMovieCinema(Boolean.TRUE.equals(efd.getHasMovieCinema()));
      ef.setHasHomeGym(Boolean.TRUE.equals(efd.getHasHomeGym()));
      ef.setHasGameRoom(Boolean.TRUE.equals(efd.getHasGameRoom()));
    }

    // --- ApplianceRating (đang là ManyToOne) ---
    // Nếu FE dùng nó như amenities block, bạn cần overwrite booleans.
    // LƯU Ý: nếu 1 ApplianceRating bị share giữa nhiều property thì sẽ ảnh hưởng chéo (nhưng thường bạn không share).
    if (dto.getApplianceRating() != null && entity.getApplianceRating() != null) {
      ApplianceRatingDTO ard = dto.getApplianceRating();
      ApplianceRating ar = entity.getApplianceRating();
      ar.setDishwasher(Boolean.TRUE.equals(ard.getDishwasher()));
      ar.setDryer(Boolean.TRUE.equals(ard.getDryer()));
      ar.setMicrowave(Boolean.TRUE.equals(ard.getMicrowave()));
      ar.setOven(Boolean.TRUE.equals(ard.getOven()));
      ar.setRefrigerator(Boolean.TRUE.equals(ard.getRefrigerator()));
      ar.setWasher(Boolean.TRUE.equals(ard.getWasher()));
    } else if (dto.getApplianceRating() == null && entity.getApplianceRating() != null) {
      // FE không gửi appliance block => reset hết về false (để “bỏ tick” có hiệu lực)
      ApplianceRating ar = entity.getApplianceRating();
      ar.setDishwasher(false);
      ar.setDryer(false);
      ar.setMicrowave(false);
      ar.setOven(false);
      ar.setRefrigerator(false);
      ar.setWasher(false);
    }

    // =========================================================
    // SUBTYPE (Apartment / TownHouse / SingleHouse / Villa)
    // - chỉ 1 subtype được phép tồn tại tại 1 thời điểm
    // =========================================================

    // Nếu client gửi apartment => set apartment + clear others
    if (dto.getApartment() != null) {
      ensureOnlyApartment(entity);
      applyApartment(dto.getApartment(), entity);
    } else if (dto.getTownHouse() != null) {
      ensureOnlyTownHouse(entity);
      applyTownHouse(dto.getTownHouse(), entity);
    } else if (dto.getSingleHouse() != null) {
      ensureOnlySingleHouse(entity);
      applySingleHouse(dto.getSingleHouse(), entity);
    } else if (dto.getVilla() != null) {
      ensureOnlyVilla(entity);
      applyVilla(dto.getVilla(), entity);
    }

    // transportRating: @ManyToOne nên thường read-only trong update
    // nếu muốn update phải fetch TransportRating theo id và set vào property
  }


// ==================== Helpers: subtype ensure & apply ====================

  private void ensureOnlyApartment(Property p) {
    // clear others (orphanRemoval=true => delete record)
    p.setTownHouse(null);
    p.setSingleHouse(null);
    p.setVilla(null);

    if (p.getApartment() == null) {
      Apartment a = new Apartment();
      a.setProperty(p);
      p.setApartment(a);
    }
  }

  private void ensureOnlyTownHouse(Property p) {
    p.setApartment(null);
    p.setSingleHouse(null);
    p.setVilla(null);

    if (p.getTownHouse() == null) {
      TownHouse t = new TownHouse();
      t.setProperty(p);
      p.setTownHouse(t);
    }
  }

  private void ensureOnlySingleHouse(Property p) {
    p.setApartment(null);
    p.setTownHouse(null);
    p.setVilla(null);

    if (p.getSingleHouse() == null) {
      SingleHouse s = new SingleHouse();
      s.setProperty(p);
      p.setSingleHouse(s);
    }
  }

  private void ensureOnlyVilla(Property p) {
    p.setApartment(null);
    p.setTownHouse(null);
    p.setSingleHouse(null);

    if (p.getVilla() == null) {
      Villa v = new Villa();
      v.setProperty(p);
      p.setVilla(v);
    }
  }

  private void applyApartment(ApartmentDTO d, Property p) {
    Apartment a = p.getApartment();
    if (d.getUsableArea() != null) a.setUsableArea(d.getUsableArea());
    if (d.getMaintenanceFee() != null) a.setMaintenanceFee(d.getMaintenanceFee());
    if (d.getLevel() != null) a.setLevel(d.getLevel());
    if (d.getHasElevatorAccess() != null) a.setHasElevatorAccess(d.getHasElevatorAccess());
    if (d.getSharedFacilities() != null) a.setSharedFacilities(d.getSharedFacilities());
    if (d.getTotalBuildingFloors() != null) a.setTotalBuildingFloors(d.getTotalBuildingFloors());
    if (d.getBalcony() != null) a.setBalcony(d.getBalcony());

    // petAllowed trong subtype: nếu schema có thì vẫn map (nhưng FE bạn muốn chỉ dùng rentalListing.petAllowed)
    if (d.getPetAllowed() != null) a.setPetAllowed(d.getPetAllowed());
  }

  private void applyTownHouse(TownHouseDTO d, Property p) {
    TownHouse t = p.getTownHouse();
    if (d.getLandArea() != null) t.setLandArea(d.getLandArea());
    if (d.getNumberOfFloors() != null) t.setNumberOfFloors(d.getNumberOfFloors());
    if (d.getCornerLot() != null) t.setCornerLot(d.getCornerLot());
    if (d.getFrontWidth() != null) t.setFrontWidth(d.getFrontWidth());
    if (d.getDepth() != null) t.setDepth(d.getDepth());
    if (d.getCarAccessible() != null) t.setCarAccessible(d.getCarAccessible());
    if (d.getCctvInstalled() != null) t.setCctvInstalled(d.getCctvInstalled());
    if (d.getMaintenanceFee() != null) t.setMaintenanceFee(d.getMaintenanceFee());
    if (d.getClubhouseAccess() != null) t.setClubhouseAccess(d.getClubhouseAccess());
    if (d.getPoolAccess() != null) t.setPoolAccess(d.getPoolAccess());
    if (d.getGymAccess() != null) t.setGymAccess(d.getGymAccess());
    if (d.getGreenSpace() != null) t.setGreenSpace(d.getGreenSpace());
    if (d.getRoadWidth() != null) t.setRoadWidth(d.getRoadWidth());
  }

  private void applySingleHouse(SingleHouseDTO d, Property p) {
    SingleHouse s = p.getSingleHouse();
    if (d.getLandArea() != null) s.setLandArea(d.getLandArea());
    if (d.getBackyardArea() != null) s.setBackyardArea(d.getBackyardArea());
    if (d.getFrontYardArea() != null) s.setFrontYardArea(d.getFrontYardArea());
    if (d.getHasGarage() != null) s.setHasGarage(d.getHasGarage());
    if (d.getHasBasement() != null) s.setHasBasement(d.getHasBasement());
  }

  private void applyVilla(VillaDTO d, Property p) {
    Villa v = p.getVilla();
    if (d.getLotArea() != null) v.setLotArea(d.getLotArea());
    if (d.getBackyardArea() != null) v.setBackyardArea(d.getBackyardArea());
    if (d.getFrontYardArea() != null) v.setFrontYardArea(d.getFrontYardArea());
    if (d.getGardenArea() != null) v.setGardenArea(d.getGardenArea());
    if (d.getParkingSpaces() != null) v.setParkingSpaces(d.getParkingSpaces());
    if (d.getHasGarage() != null) v.setHasGarage(d.getHasGarage());
    if (d.getHasBasement() != null) v.setHasBasement(d.getHasBasement());
    if (d.getGarageArea() != null) v.setGarageArea(d.getGarageArea());
    if (d.getViewType() != null) v.setViewType(d.getViewType());
    if (d.getSmartHomeLevel() != null) v.setSmartHomeLevel(d.getSmartHomeLevel());
    if (d.getServiceArea() != null) v.setServiceArea(d.getServiceArea());
  }
}