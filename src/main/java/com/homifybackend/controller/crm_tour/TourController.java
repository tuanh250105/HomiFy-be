package com.homifybackend.controller.crm_tour;

import com.homifybackend.model.Tour;
import com.homifybackend.repository.TourRepository;
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

    private final TourRepository repository;
    private final TourService tourService;

    public TourController(TourRepository repository, TourService tourService) {
        this.repository = repository;
        this.tourService = tourService;
    }

    @GetMapping
    public List<Tour> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Tour create(@RequestBody Tour data) {
        if (data.getStatus() == null) {
            data.setStatus("PENDING");
        }
        if (data.getRescheduleCount() == null) {
            data.setRescheduleCount(0);
        }
        return repository.save(data);
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