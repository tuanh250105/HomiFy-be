package com.homifybackend.manageRentals.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalPropertyDTO {

  private AddressDTO address;
  private PropertyDTO property;
  private RentalListingDTO rentalListing;
  private List<RentalListingImageDTO> rentalListingImages;

  // Subtype (chỉ 1 trong 4 sẽ có giá trị)
  private ApartmentDTO apartment;
  private TownHouseDTO townHouse;
  private SingleHouseDTO singleHouse;
  private VillaDTO villa;

  // Features
  private SecurityFeaturesDTO securityFeatures;
  private OutdoorFeaturesDTO outdoorFeatures;
  private EntertainmentFeaturesDTO entertainmentFeatures;

  // Ratings
  private TransportRatingDTO transportRating;
  private ApplianceRatingDTO applianceRating;

  // Tenant & Contract (nếu rented)
  private UserDTO tenant;
  private RentalContractDTO rentalContract;
}