package com.homifybackend.controller.crm_tour;

import com.homifybackend.dto.TourDTO;
import com.homifybackend.model.Tour;
import com.homifybackend.service.crm_tour.TourService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = "http://localhost:3000")
public class TourController {

    private final TourService tourService;
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public List<Tour> getAll() {
        return tourService.findAllTours();
    }

    @PostMapping
    public ResponseEntity<Tour> create(@RequestBody TourDTO tourDTO) {
        Tour savedTour = tourService.createTourFromDTO(tourDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTour);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tour> update(@PathVariable Long id, @RequestBody Tour data) {
        Optional<Tour> updatedTour = tourService.updateTourStatus(id, data);
        if (updatedTour == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return updatedTour.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}