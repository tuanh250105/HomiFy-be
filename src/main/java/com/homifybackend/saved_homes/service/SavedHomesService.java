package com.homifybackend.saved_homes.service;

import com.homifybackend.saved_homes.dto.SavedHomeCardDto;
import com.homifybackend.saved_homes.repository.SavedHomesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedHomesService {

    private final SavedHomesRepository savedHomesRepo;

    public SavedHomesService(SavedHomesRepository savedHomesRepo) {
        this.savedHomesRepo = savedHomesRepo;
    }

    public List<SavedHomeCardDto> getSavedHomes(Long customerId) {
        return savedHomesRepo.findSavedHomes(customerId)
                .stream()
                .map(r -> new SavedHomeCardDto(
                        r.getId(),
                        r.getListingId(),
                        r.getListingType(),
                        r.getTitle(),
                        r.getAddress(),
                        r.getPrice(),
                        r.getStatus(),
                        r.getBeds(),
                        r.getBaths(),
                        r.getSqft(),
                        r.getImg(),
                        r.getType(),
                        r.getAddedAt()
                ))
                .toList();
    }

    public void removeSavedHome(Long customerId, Long propertyId) {
        savedHomesRepo.deleteFavorite(customerId, propertyId);
    }
}
