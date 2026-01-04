package com.homifybackend.manage_tours.controller;

import com.homifybackend.manage_tours.dto.CustomerRescheduleRequest;
import com.homifybackend.manage_tours.dto.CustomerToursResponseDTO;
import com.homifybackend.manage_tours.service.CustomerToursService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customer-tours")
@CrossOrigin(origins = "*")
public class CustomerToursController {

    private final CustomerToursService service;

    public CustomerToursController(CustomerToursService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<CustomerToursResponseDTO> getTours(@RequestParam long customerId) {
        return ResponseEntity.ok(service.getCustomerTours(customerId));
    }

    @PostMapping("/{tourId}/cancel")
    public ResponseEntity<?> cancel(@RequestParam long customerId, @PathVariable long tourId) {
        service.cancel(customerId, tourId);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/{tourId}/reschedule")
    public ResponseEntity<?> reschedule(@RequestParam long customerId,
                                        @PathVariable long tourId,
                                        @RequestBody CustomerRescheduleRequest req) {
        service.reschedule(customerId, tourId, req);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
