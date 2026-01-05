package com.homifybackend.manage_tours.service;

import com.homifybackend.manage_tours.dto.CustomerRescheduleRequest;
import com.homifybackend.manage_tours.dto.CustomerTourItemDTO;
import com.homifybackend.manage_tours.dto.CustomerToursResponseDTO;
import com.homifybackend.manage_tours.repository.CustomerToursRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerToursService {

    private final CustomerToursRepository repo;

    public CustomerToursService(CustomerToursRepository repo) {
        this.repo = repo;
    }

    public CustomerToursResponseDTO getCustomerTours(long customerId) {
        List<CustomerTourItemDTO> all = repo.findAllByCustomer(customerId);

        LocalDate today = LocalDate.now();

        List<CustomerTourItemDTO> upcoming = new ArrayList<>();
        List<CustomerTourItemDTO> past = new ArrayList<>();

        for (CustomerTourItemDTO t : all) {
            // Map DB status -> UI status (KHÔNG ĐỔI MODEL/ENUM)
            // DB: APPROVED/PENDING/CANCELED
            // UI: CONFIRMED/PENDING/CANCELED
            t.setStatus(mapStatusForUi(t.getStatus()));

            boolean isCanceled = "CANCELED".equalsIgnoreCase(t.getStatus());
            LocalDate d = t.getRequestedDate();

            boolean isUpcoming = (d != null && !d.isBefore(today)) && !isCanceled;

            if (isUpcoming) upcoming.add(t);
            else past.add(t);
        }

        return new CustomerToursResponseDTO(upcoming, past);
    }

    public void cancel(long customerId, long tourId) {
        int updated = repo.cancelIfOwnedByCustomer(tourId, customerId);
        if (updated == 0) {
            throw new RuntimeException("Tour not found or not allowed.");
        }
    }

    public void reschedule(long customerId, long tourId, CustomerRescheduleRequest req) {
        if (req == null || req.getRequestedDate() == null || req.getRequestedDate().isBlank()
                || req.getTimeSlot() == null || req.getTimeSlot().isBlank()) {
            throw new RuntimeException("Invalid request.");
        }

        LocalDate date = LocalDate.parse(req.getRequestedDate().trim());
        String timeSlot = req.getTimeSlot().trim();

        int updated = repo.rescheduleIfOwnedByCustomer(tourId, customerId, date, timeSlot);
        if (updated == 0) {
            throw new RuntimeException("Tour not found or not allowed.");
        }
    }

    private String mapStatusForUi(String dbStatus) {
        if (dbStatus == null) return "PENDING";
        String s = dbStatus.trim().toUpperCase();
        if ("APPROVED".equals(s)) return "CONFIRMED";
        if ("PENDING".equals(s)) return "PENDING";
        if ("CANCELED".equals(s)) return "CANCELED";
        // fallback an toàn
        return s;
    }
}
