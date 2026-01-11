package com.homifybackend.controller.crm_tour;

import com.homifybackend.auth.security.CustomUserDetails;
import com.homifybackend.auth.service.UserService;
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
    private final UserService userService;
    public TourController(TourService tourService, UserService userService) {
        this.tourService = tourService;
        this.userService = userService;
    }

    // Helper method: Lấy userId hiện tại từ SecurityContext
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        return ((CustomUserDetails) auth.getPrincipal()).getUserId();
    }

    // 1. Lấy tour theo đúng Agent quản lý (Chuẩn rồi)
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Tour>> getToursByAgent(@PathVariable Long agentId) {
        List<Tour> tours = tourService.findToursByAgent(agentId);
        return ResponseEntity.ok(tours);
    }

    // 2. Tạo tour mới từ khách hàng || KHÔI
    @PostMapping("/{listingId}/tour-request")
    public ResponseEntity<?> create(@PathVariable Long listingId, @RequestBody TourDTO tourDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        User requester = userService.getUserById(userId);
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