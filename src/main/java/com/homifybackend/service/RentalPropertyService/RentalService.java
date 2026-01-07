package com.homifybackend.service.RentalPropertyService;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homifybackend.dto.RentalPropertyDTO;
import com.homifybackend.mapper.RentalPropertyMapper;
import com.homifybackend.model.Customer;
import com.homifybackend.model.Property;
import com.homifybackend.model.RentalListingImage;
import com.homifybackend.model.RentalListingStatus;
import com.homifybackend.repository.AddressRepository;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.repository.RentalManagerRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalService {

  private final RentalManagerRepository rentalManagerRepository;
  private final RentalPropertyMapper rentalPropertyMapper;
  private final CustomerRepository customerRepository;
  private final AddressRepository addressRepository;

  // READ
  public List<RentalPropertyDTO> getAllProperties(Long ownerId) {
    return rentalManagerRepository.findAllByOwnerIdWithFullRelations(ownerId)
        .stream()
        .map(rentalPropertyMapper::toDto)
        .toList();
  }

  public RentalPropertyDTO getPropertyById(Long id) {
    return rentalManagerRepository.findByIdWithFullRelations(id)
        .map(rentalPropertyMapper::toDto)
        .orElse(null);
  }

  // ADD
  public RentalPropertyDTO addProperty(Long ownerId, RentalPropertyDTO dto) {
    Property property = rentalPropertyMapper.createEntityFromDto(dto);

    Customer owner = customerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy owner với ID: " + ownerId));
    property.setOwner(owner);

    rentalPropertyMapper.applyDtoToEntity(dto, property);

    if (property.getAddress() != null && property.getAddress().getAddressId() == null) {
      property.setAddress(addressRepository.save(property.getAddress()));
    }

    if (property.getRentalListing() != null) {
      property.getRentalListing().setProperty(property);

      if (property.getRentalListing().getImages() != null) {
        for (RentalListingImage img : property.getRentalListing().getImages()) {
          img.setRentalListing(property.getRentalListing());
        }
      }
    }

    property = rentalManagerRepository.save(property);
    return rentalPropertyMapper.toDto(property);
  }

  // UPDATE
  public RentalPropertyDTO updateProperty(Long id, RentalPropertyDTO dto) {
    Property property = rentalManagerRepository.findByIdWithFullRelations(id).orElse(null);
    if (property == null) return null;

    rentalPropertyMapper.applyDtoToEntity(dto, property);

    if (property.getAddress() != null && property.getAddress().getAddressId() == null) {
      property.setAddress(addressRepository.save(property.getAddress()));
    }

    if (property.getRentalListing() != null && property.getRentalListing().getImages() != null) {
      for (RentalListingImage img : property.getRentalListing().getImages()) {
        img.setRentalListing(property.getRentalListing());
      }
    }

    property = rentalManagerRepository.save(property);
    return rentalPropertyMapper.toDto(property);
  }

  // DEACTIVATE
  public boolean deactivateProperty(Long id) {
    Property p = rentalManagerRepository.findById(id).orElse(null);
    if (p == null || p.getRentalListing() == null) return false;

    p.getRentalListing().setRentalStatus(RentalListingStatus.INACTIVE);
    rentalManagerRepository.save(p);
    return true;
  }

  // ACTIVATE
  public boolean activateProperty(Long id) {
    Property p = rentalManagerRepository.findById(id).orElse(null);
    if (p == null || p.getRentalListing() == null) return false;

    p.getRentalListing().setRentalStatus(RentalListingStatus.ACTIVE);
    rentalManagerRepository.save(p);
    return true;
  }

  // DELETE
  public boolean deleteProperty(Long id) {
    if (!rentalManagerRepository.existsById(id)) return false;
    rentalManagerRepository.deleteById(id);
    return true;
  }
}
