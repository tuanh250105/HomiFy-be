package com.homifybackend.controller;

import com.homifybackend.model.Tour;
import com.homifybackend.repository.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = "http://localhost:3000")
public class TourController {

    private final TourRepository repository;

    public TourController(TourRepository repository) {
        this.repository = repository;
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
        return repository.findById(id)
                .map(existingTour -> {
                    String newStatus = data.getStatus();
                    String newDate = data.getDate();
                    String newTime = data.getTime();

                    boolean isConfirming = "CONFIRMED".equalsIgnoreCase(newStatus);
                    boolean isChangingSchedule = newDate != null || newTime != null;

                    if (isConfirming || isChangingSchedule) {
                        String checkDate = newDate != null ? newDate : existingTour.getDate();
                        String checkTime = newTime != null ? newTime : existingTour.getTime();

                        boolean conflict = repository.existsByDateAndTimeAndStatusIgnoreCaseAndIdNot(
                                checkDate, checkTime, "CONFIRMED", id);

                        if (conflict) {
                            return ResponseEntity.status(HttpStatus.CONFLICT).<Tour>build();
                        }
                    }
                    
                    if (newStatus != null) {
                        existingTour.setStatus(newStatus.toUpperCase());
                    }
                    if (newDate != null) {
                        existingTour.setDate(newDate);
                    }
                    if (newTime != null) {
                        existingTour.setTime(newTime);
                    }
                    if (data.getRescheduleCount() != null) {
                        existingTour.setRescheduleCount(data.getRescheduleCount());
                    }

                    Tour saved = repository.save(existingTour);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}