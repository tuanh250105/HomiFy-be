package com.homifybackend.controller.crm_tour;

import com.homifybackend.dto.TourDTO;
import com.homifybackend.model.Tour;
import com.homifybackend.model.User;
import com.homifybackend.service.crm_tour.TourService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // 1. Lấy tour theo đúng Agent quản lý (Chuẩn rồi)
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Tour>> getToursByAgent(@PathVariable Long agentId) {
        List<Tour> tours = tourService.findToursByAgent(agentId);
        return ResponseEntity.ok(tours);
    }

    // 2. Tạo tour mới từ khách hàng || KHÔI
    @PostMapping("/{listingId}/tour-request")
    public ResponseEntity<Tour> create(@RequestHeader("Authorization") String token, @PathVariable Long listingId, @RequestBody TourDTO tourDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User requester = (User) auth.getPrincipal();
        Tour savedTour = tourService.createTourFromDTO(requester, listingId, tourDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTour);
    }

    // 3. Cập nhật trạng thái (Duyệt/Từ chối/Đổi lịch)
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