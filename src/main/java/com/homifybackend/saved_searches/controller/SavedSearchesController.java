package com.homifybackend.saved_searches.controller;

import com.homifybackend.saved_searches.dto.CreateSavedSearchRequest;
import com.homifybackend.saved_searches.dto.SavedSearchDto;
import com.homifybackend.saved_searches.dto.UpdateSavedSearchRequest;
import com.homifybackend.saved_searches.service.SavedSearchesService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class SavedSearchesController {

    private final SavedSearchesService service;

    public SavedSearchesController(SavedSearchesService service) {
        this.service = service;
    }

    @GetMapping("/saved-searches")
    public List<SavedSearchDto> list(@RequestParam(defaultValue = "2") Long customerId) {
        return service.list(customerId);
    }

    @PostMapping("/saved-searches")
    public SavedSearchDto create(
            @RequestParam(defaultValue = "2") Long customerId,
            @RequestBody CreateSavedSearchRequest req
    ) {
        return service.create(customerId, req);
    }

    // ✅ để Edit (lúc nãy bạn bị: Request method 'PUT' is not supported)
    @PutMapping("/saved-searches/{id}")
    public SavedSearchDto update(
            @PathVariable Long id,
            @RequestParam(defaultValue = "2") Long customerId,
            @RequestBody UpdateSavedSearchRequest req
    ) {
        return service.update(customerId, id, req);
    }

    @DeleteMapping("/saved-searches/{id}")
    public Map<String, Object> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "2") Long customerId
    ) {
        service.delete(customerId, id);
        return Map.of("ok", true);
    }

    @PatchMapping("/saved-searches/{id}/subscription")
    public SavedSearchDto toggle(
            @PathVariable Long id,
            @RequestParam(defaultValue = "2") Long customerId,
            @RequestParam boolean on
    ) {
        return service.toggleSubscription(customerId, id, on);
    }
}
