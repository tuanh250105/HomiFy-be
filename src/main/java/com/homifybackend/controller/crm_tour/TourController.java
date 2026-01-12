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

    // Lấy tour theo đúng Agent quản lý
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Tour>> getToursByAgent(@PathVariable Long agentId) {
        List<Tour> tours = tourService.findToursByAgent(agentId);
        return ResponseEntity.ok(tours);
    }

    @PostMapping
    public ResponseEntity<Tour> create(@RequestBody TourDTO tourDTO, @RequestParam Long userId) {
        Tour savedTour = tourService.createTourFromDTO(tourDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTour);
    }

    // Cập nhật trạng thái (Duyệt/Từ chối/Đổi lịch)
    @PutMapping("/{id}")
    public ResponseEntity<Tour> update(@PathVariable Long id, @RequestBody Tour data) {
        Optional<Tour> updatedTour = tourService.updateTourStatus(id, data);

        // Trả về nếu trùng lịch (logic null từ service)
        if (updatedTour == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return updatedTour.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}