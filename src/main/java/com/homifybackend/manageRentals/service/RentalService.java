package com.homifybackend.manageRentals.service;

import com.homifybackend.manageRentals.dto.RentalPropertyDTO;
import com.homifybackend.manageRentals.mapper.RentalPropertyMapper;
import com.homifybackend.manageRentals.repository.PropertyRepository;
import com.homifybackend.manageRentals.repository.CustomerRepository;
import com.homifybackend.model.Customer;
import com.homifybackend.model.Property;
import com.homifybackend.model.RentalListingImage;
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
    Property property = new Property();

    // Fix: Set owner entity đúng cách
    Customer owner = customerRepository.findById(CURRENT_OWNER_ID)
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy owner với ID: " + CURRENT_OWNER_ID));
    property.setOwner(owner);

    // Map DTO → entity
    rentalPropertyMapper.applyDtoToEntity(dto, property);

    // ====== FIX RELATIONS ======
    if (property.getRentalListing() != null) {
      property.getRentalListing().setProperty(property);

      // SỬA: Dùng for thường thay vì lambda để tránh lỗi effectively final
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

    // SỬA: Cũng dùng for thường ở đây
    if (property.getRentalListing() != null && property.getRentalListing().getImages() != null) {
      for (RentalListingImage img : property.getRentalListing().getImages()) {
        img.setRentalListing(property.getRentalListing());
      }
    }

    property = propertyRepository.save(property);
    return rentalPropertyMapper.toDto(property);
  }

  // Các method còn lại giữ nguyên
  public boolean deactivateProperty(Long id) {
    Property p = propertyRepository.findById(id).orElse(null);
    if (p == null || p.getRentalListing() == null) return false;
    p.getRentalListing().setListingStatus("INACTIVE");
    propertyRepository.save(p);
    return true;
  }

  public boolean activateProperty(Long id) {
    Property p = propertyRepository.findById(id).orElse(null);
    if (p == null || p.getRentalListing() == null) return false;
    p.getRentalListing().setListingStatus("ACTIVE");
    propertyRepository.save(p);
    return true;
  }

  public boolean deleteProperty(Long id) {
    if (!propertyRepository.existsById(id)) return false;
    propertyRepository.deleteById(id);
    return true;
  }
}