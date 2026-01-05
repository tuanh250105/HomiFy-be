package com.homifybackend.saved_homes.controller;

import com.homifybackend.saved_homes.dto.SavedHomeCardDto;
import com.homifybackend.saved_homes.service.SavedHomesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class SavedHomesController {

    private final SavedHomesService service;

    public SavedHomesController(SavedHomesService service) {
        this.service = service;
    }

    @GetMapping("/saved-homes")
    public List<SavedHomeCardDto> getSavedHomes(@RequestParam(defaultValue = "2") Long customerId) {
        return service.getSavedHomes(customerId);
    }

    @DeleteMapping("/saved-homes/{propertyId}")
    public Map<String, Object> removeSavedHome(
            @PathVariable("propertyId") Long propertyId,
            @RequestParam(defaultValue = "2") Long customerId
    ) {
        service.removeSavedHome(customerId, propertyId);
        return Map.of("ok", true);
    }
}
