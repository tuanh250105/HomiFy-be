package com.homifybackend.saved_searches.service;

import com.homifybackend.model.SavedSearch;
import com.homifybackend.saved_searches.dto.CreateSavedSearchRequest;
import com.homifybackend.saved_searches.dto.SavedSearchDto;
import com.homifybackend.saved_searches.dto.UpdateSavedSearchRequest;
import com.homifybackend.saved_searches.repository.SavedSearchesRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavedSearchesService {

    private final SavedSearchesRepository repo;

    public SavedSearchesService(SavedSearchesRepository repo) {
        this.repo = repo;
    }

    public List<SavedSearchDto> list(Long customerId) {
        return repo.findAllByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SavedSearchDto create(Long customerId, CreateSavedSearchRequest req) {
        String addressText = safeTrim(req.addressText());
        if (addressText.isEmpty()) throw new IllegalArgumentException("addressText is required");

        // Optional: chặn trùng address
        if (repo.existsByCustomerIdAndAddressText(customerId, addressText)) {
            throw new IllegalArgumentException("This address is already saved.");
        }

        SavedSearch s = new SavedSearch();
        s.setCustomerId(customerId);
        s.setName(safeDefault(req.name(), "Saved search"));
        s.setAddressText(addressText);
        s.setListingType(safeDefault(req.listingType(), "BUY"));
        s.setSubscriptionsOn(req.subscriptionsOn() != null && req.subscriptionsOn());
        s.setFrequency(safeDefault(req.frequency(), "INSTANT"));

        return toDto(repo.save(s));
    }

    @Transactional
    public SavedSearchDto update(Long customerId, Long id, UpdateSavedSearchRequest req) {
        SavedSearch s = repo.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Saved search not found"));

        if (req.name() != null) s.setName(safeDefault(req.name(), s.getName()));
        if (req.addressText() != null) {
            String at = safeTrim(req.addressText());
            if (at.isEmpty()) throw new IllegalArgumentException("addressText cannot be empty");
            s.setAddressText(at);
        }
        if (req.listingType() != null) s.setListingType(req.listingType());
        if (req.subscriptionsOn() != null) s.setSubscriptionsOn(req.subscriptionsOn());
        if (req.frequency() != null) s.setFrequency(req.frequency());

        return toDto(repo.save(s));
    }

    @Transactional
    public void delete(Long customerId, Long id) {
        SavedSearch s = repo.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Saved search not found"));
        repo.delete(s);
    }

    @Transactional
    public SavedSearchDto toggleSubscription(Long customerId, Long id, boolean on) {
        SavedSearch s = repo.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Saved search not found"));
        s.setSubscriptionsOn(on);
        return toDto(repo.save(s));
    }

    private SavedSearchDto toDto(SavedSearch s) {
        return new SavedSearchDto(
                s.getId(),
                s.getName(),
                s.getAddressText(),
                s.getListingType(),
                s.getSubscriptionsOn(),
                s.getFrequency(),
                s.getCreatedAt()
        );
    }

    private static String safeTrim(String x) {
        return x == null ? "" : x.trim();
    }

    private static String safeDefault(String x, String def) {
        String t = safeTrim(x);
        return t.isEmpty() ? def : t;
    }
}
