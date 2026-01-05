package com.homifybackend.mapper;

import com.homifybackend.dto.*;
import com.homifybackend.repository.RentalContractRepository;
import com.homifybackend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
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

      // Chỉ khi RENTED mới query contract + tenant
      if (rl.getRentalStatus() == RentalListingStatus.RENTED) {
        if (rl.getProperty() != null && rl.getProperty().getPropertyId() != null) {
          rentalContractRepository.findByRentalListing_Id(rl.getId())
              .ifPresent(contract -> {
                dto.setRentalContract(mapRentalContract(contract));
                if (contract.getTenant() != null) {
                  dto.setTenant(userMapper.toDto(contract.getTenant()));
                }
              });
        }
      }
    }

    // SUBTYPE MAPPING: dùng instanceof vì Property là superclass (JOINED inheritance)
    if (property instanceof Apartment) {
      dto.setApartment(mapApartment((Apartment) property));
    } else if (property instanceof TownHouse) {
      dto.setTownHouse(mapTownHouse((TownHouse) property));
    } else if (property instanceof SingleHouse) {
      dto.setSingleHouse(mapSingleHouse((SingleHouse) property));
    } else if (property instanceof Villa) {
      dto.setVilla(mapVilla((Villa) property));
    }

    return dto;
  }

  // ==================== Mapping methods ====================

  private AddressDTO mapAddress(Address a) {
    if (a == null) return null;
    AddressDTO d = new AddressDTO(a.getAddressId(), a.getCity(), a.getProvince(), a.getNation(), a.getStreet(), null, null, a.getZipCode());
    return d;
  }

  private PropertyDTO_Rental mapProperty(Property p) {
    if (p == null) return null;
    PropertyDTO_Rental d = new PropertyDTO_Rental();
    d.setPropertyId(p.getPropertyId());
    d.setOwnerId(p.getOwner() != null ? p.getOwner().getUserId() : null);
    d.setAddressId(p.getAddress() != null ? p.getAddress().getAddressId() : null);
    d.setYearBuilt(p.getYearBuilt());
    d.setFloors(p.getFloors());
    d.setBeds(p.getBeds());
    d.setBaths(p.getBaths());
    d.setArea(p.getArea());
    d.setDescription(p.getDescription());
    d.setTransportRatingId(p.getTransportRating() != null ? p.getTransportRating().getRatingId() : null);
    d.setApplianceRatingId(p.getApplianceRating() != null ? p.getApplianceRating().getRatingId() : null);
    d.setCreatedAt(p.getCreatedAt());
    // Auto-populate propertyType từ actual entity class
    d.setPropertyType(getPropertyType(p));
    return d;
  }

  private String getPropertyType(Property p) {
    if (p instanceof Apartment) return "APARTMENT";
    if (p instanceof TownHouse) return "TOWN_HOUSE";
    if (p instanceof SingleHouse) return "SINGLE_HOUSE";
    if (p instanceof Villa) return "VILLA";
    return null;
  }

  private RentalListingDTO_Rental mapRentalListing(RentalListing rl) {
    if (rl == null) return null;
    RentalListingDTO_Rental d = new RentalListingDTO_Rental();
    d.setId(rl.getId());
    d.setPropertyId(rl.getProperty().getPropertyId());
    d.setMonthlyRent(rl.getMonthlyRent());
    d.setDepositAmount(rl.getDepositAmount());
    d.setMaintenanceFee(rl.getMaintenanceFee());
    d.setAvailableFrom(rl.getAvailableFrom());
    d.setLeaseTermMonths(rl.getLeaseTermMonths());
    d.setPetAllowed(rl.getPetAllowed());
    d.setUtilitiesIncluded(rl.getUtilitiesIncluded());
    d.setListingStatus(rl.getRentalStatus() != null ? rl.getRentalStatus().name() : null);
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
    TransportRatingDTO d = new TransportRatingDTO(tr.getWalkScore(), tr.getBikeScore(), tr.getTransitScore());
    return d;
  }

  private ApplianceRatingDTO mapApplianceRating(ApplianceRating ar) {
    if (ar == null) return null;
    ApplianceRatingDTO d = new ApplianceRatingDTO(ar.getDishwasher(), ar.getDryer(), ar.getMicrowave(), ar.getOven(), ar.getRefrigerator(), ar.getWasher());
    return d;
  }

  // Subtype mapping
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

  // ==================== DTO -> ENTITY (ADD/UPDATE) ====================

  public void applyDtoToEntity(RentalPropertyDTO dto, Property entity) {
    if (dto == null || entity == null) return;

    // Address
    if (dto.getAddress() != null) {
      AddressDTO ad = dto.getAddress();
      Address addr = entity.getAddress();
      if (addr == null) {
        addr = new Address();
        entity.setAddress(addr);
      }
      if (ad.street() != null) addr.setStreet(ad.street());
      if (ad.city() != null) addr.setCity(ad.city());
      if (ad.province() != null) addr.setProvince(ad.province());
      if (ad.zipCode() != null) addr.setZipCode(ad.zipCode());
      if (ad.nation() != null) addr.setNation(ad.nation());
      if (ad.latitude() != null) addr.setLatitude(ad.latitude());
      if (ad.latitude() != null) addr.setLongitude(ad.latitude());
    }

    // Property core
    if (dto.getProperty() != null) {
      PropertyDTO_Rental p = dto.getProperty();
      if (p.getYearBuilt() != null) entity.setYearBuilt(p.getYearBuilt());
      if (p.getFloors() != null) entity.setFloors(p.getFloors());
      if (p.getBeds() != null) entity.setBeds(p.getBeds());
      if (p.getBaths() != null) entity.setBaths(p.getBaths());
      if (p.getArea() != null) entity.setArea(p.getArea());
      if (p.getDescription() != null) entity.setDescription(p.getDescription());
    }

    // RentalListing
    if (dto.getRentalListing() != null) {
      RentalListingDTO_Rental rld = dto.getRentalListing();
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
        rl.setRentalStatus(RentalListingStatus.valueOf(rld.getListingStatus().toUpperCase()));
      }
      if (rld.getDateListed() != null) rl.setDateListed(rld.getDateListed());
      if (rld.getDateRented() != null) rl.setDateRented(rld.getDateRented());
    }

    // Images
    if (dto.getRentalListingImages() != null) {
      RentalListing rl = entity.getRentalListing();
      if (rl == null) {
        rl = new RentalListing();
        rl.setProperty(entity);
        entity.setRentalListing(rl);
      }
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

    // Features & Amenities (giữ nguyên logic của bạn)
    if (dto.getSecurityFeatures() == null) {
      entity.setSecurityFeatures(null);
    } else {
      SecurityFeaturesDTO sfd = dto.getSecurityFeatures();
      SecurityFeatures sf = entity.getSecurityFeatures();
      if (sf == null) {
        sf = new SecurityFeatures();
        sf.setProperty(entity);
        entity.setSecurityFeatures(sf);
      }
      sf.setHasSecurityDoor(Boolean.TRUE.equals(sfd.getHasSecurityDoor()));
      sf.setHasCctv(Boolean.TRUE.equals(sfd.getHasCctv()));
    }

    // Outdoor, Entertainment, ApplianceRating giữ nguyên như code cũ của bạn
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

    if (dto.getApplianceRating() != null && entity.getApplianceRating() != null) {
      ApplianceRatingDTO ard = dto.getApplianceRating();
      ApplianceRating ar = entity.getApplianceRating();
      ar.setDishwasher(Boolean.TRUE.equals(ard.dishwasher()));
      ar.setDryer(Boolean.TRUE.equals(ard.dryer()));
      ar.setMicrowave(Boolean.TRUE.equals(ard.microwave()));
      ar.setOven(Boolean.TRUE.equals(ard.oven()));
      ar.setRefrigerator(Boolean.TRUE.equals(ard.refrigerator()));
      ar.setWasher(Boolean.TRUE.equals(ard.washer()));
    } else if (dto.getApplianceRating() == null && entity.getApplianceRating() != null) {
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
      applyApartment(dto.getApartment(), entity);
    } else if (dto.getTownHouse() != null) {
      applyTownHouse(dto.getTownHouse(), entity);
    } else if (dto.getSingleHouse() != null) {
      applySingleHouse(dto.getSingleHouse(), entity);
    } else if (dto.getVilla() != null) {
      applyVilla(dto.getVilla(), entity);
    }
  }
  // Helpers subtype apply
  private void applyApartment(ApartmentDTO d, Property p) {
    if (!(p instanceof Apartment)) return;
    Apartment a = (Apartment) p;
    if (d.getUsableArea() != null) a.setUsableArea(d.getUsableArea());
    if (d.getMaintenanceFee() != null) a.setMaintenanceFee(d.getMaintenanceFee());
    if (d.getLevel() != null) a.setLevel(d.getLevel());
    if (d.getHasElevatorAccess() != null) a.setHasElevatorAccess(d.getHasElevatorAccess());
    if (d.getSharedFacilities() != null) a.setSharedFacilities(d.getSharedFacilities());
    if (d.getTotalBuildingFloors() != null) a.setTotalBuildingFloors(d.getTotalBuildingFloors());
    if (d.getBalcony() != null) a.setBalcony(d.getBalcony());
    if (d.getPetAllowed() != null) a.setPetAllowed(d.getPetAllowed());
  }

  private void applyTownHouse(TownHouseDTO d, Property p) {
    if (!(p instanceof TownHouse)) return;
    TownHouse t = (TownHouse) p;
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
    if (!(p instanceof SingleHouse)) return;
    SingleHouse s = (SingleHouse) p;
    if (d.getLandArea() != null) s.setLandArea(d.getLandArea());
    if (d.getBackyardArea() != null) s.setBackyardArea(d.getBackyardArea());
    if (d.getFrontYardArea() != null) s.setFrontYardArea(d.getFrontYardArea());
    if (d.getHasGarage() != null) s.setHasGarage(d.getHasGarage());
    if (d.getHasBasement() != null) s.setHasBasement(d.getHasBasement());
  }

  private void applyVilla(VillaDTO d, Property p) {
    if (!(p instanceof Villa)) return;
    Villa v = (Villa) p;
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

  public Property createEntityFromDto(RentalPropertyDTO dto) {
    if (dto == null) return new Property();

    if (dto.getApartment() != null) return new Apartment();
    if (dto.getTownHouse() != null) return new TownHouse();
    if (dto.getSingleHouse() != null) return new SingleHouse();
    if (dto.getVilla() != null) return new Villa();

    return new Property();
  }
}
