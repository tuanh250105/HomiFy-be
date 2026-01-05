package com.homifybackend.manageRentals.service;

import com.homifybackend.manageRentals.dto.RentalPropertyDTO;
import com.homifybackend.manageRentals.mapper.RentalPropertyMapper;
import com.homifybackend.manageRentals.repository.PropertyRepository;
import com.homifybackend.manageRentals.repository.CustomerRepository;
import com.homifybackend.model.Customer;
import com.homifybackend.model.Property;
import com.homifybackend.model.RentalListingImage;
import com.homifybackend.model.RentalListingStatus; // ← THÊM IMPORT NÀY
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalService {

  private final PropertyRepository propertyRepository;
  private final RentalPropertyMapper rentalPropertyMapper;
  private final CustomerRepository customerRepository;

  private static final Long CURRENT_OWNER_ID = 101L;

  // READ
  public List<RentalPropertyDTO> getAllProperties() {
    return propertyRepository.findAllByOwnerIdWithFullRelations(CURRENT_OWNER_ID)
        .stream()
        .map(rentalPropertyMapper::toDto)
        .toList();
  }

  public RentalPropertyDTO getPropertyById(Long id) {
    return propertyRepository.findByIdWithFullRelations(id)
        .map(rentalPropertyMapper::toDto)
        .orElse(null);
  }

  // ADD
  public RentalPropertyDTO addProperty(RentalPropertyDTO dto) {
    Property property = rentalPropertyMapper.createEntityFromDto(dto);

    // Set owner entity
    Customer owner = customerRepository.findById(CURRENT_OWNER_ID)
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy owner với ID: " + CURRENT_OWNER_ID));
    property.setOwner(owner);

    // Map DTO → entity
    rentalPropertyMapper.applyDtoToEntity(dto, property);

    // Fix relations
    if (property.getRentalListing() != null) {
      property.getRentalListing().setProperty(property);

      if (property.getRentalListing().getImages() != null) {
        for (RentalListingImage img : property.getRentalListing().getImages()) {
          img.setRentalListing(property.getRentalListing());
        }
      }
    }

    property = propertyRepository.save(property);
    return rentalPropertyMapper.toDto(property);
  }

  // UPDATE
  public RentalPropertyDTO updateProperty(Long id, RentalPropertyDTO dto) {
    Property property = propertyRepository.findByIdWithFullRelations(id).orElse(null);
    if (property == null) return null;

    rentalPropertyMapper.applyDtoToEntity(dto, property);

    if (property.getRentalListing() != null && property.getRentalListing().getImages() != null) {
      for (RentalListingImage img : property.getRentalListing().getImages()) {
        img.setRentalListing(property.getRentalListing());
      }
    }

    property = propertyRepository.save(property);
    return rentalPropertyMapper.toDto(property);
  }

  // DEACTIVATE
  public boolean deactivateProperty(Long id) {
    Property p = propertyRepository.findById(id).orElse(null);
    if (p == null || p.getRentalListing() == null) return false;

    p.getRentalListing().setRentalStatus(RentalListingStatus.INACTIVE); // ← SỬA Ở ĐÂY
    propertyRepository.save(p);
    return true;
  }

  // ACTIVATE
  public boolean activateProperty(Long id) {
    Property p = propertyRepository.findById(id).orElse(null);
    if (p == null || p.getRentalListing() == null) return false;

    p.getRentalListing().setRentalStatus(RentalListingStatus.ACTIVE);
    propertyRepository.save(p);
    return true;
  }

  // DELETE
  public boolean deleteProperty(Long id) {
    if (!propertyRepository.existsById(id)) return false;
    propertyRepository.deleteById(id);
    return true;
  }
}