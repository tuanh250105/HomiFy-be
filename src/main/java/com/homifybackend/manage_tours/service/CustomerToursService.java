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
        List<CustomerTourItemDTO> all = repo.findToursByCustomerId(customerId);

        List<CustomerTourItemDTO> upcoming = new ArrayList<>();
        List<CustomerTourItemDTO> past = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (CustomerTourItemDTO t : all) {
            String st = (t.getStatus() == null) ? "" : t.getStatus().toUpperCase();
            boolean isPastStatus = st.equals("COMPLETED")
                    || st.equals("CANCELED_BY_AGENT")
                    || st.equals("CANCELED_BY_CUSTOMER");

            LocalDate d = t.getRequestedDate();
            boolean isPastDate = (d != null && d.isBefore(today));

            if (isPastStatus || isPastDate) past.add(t);
            else upcoming.add(t);
        }

        return new CustomerToursResponseDTO(upcoming, past);
    }

    public void cancel(long customerId, long tourId) {
        int updated = repo.cancelIfOwnedByCustomer(tourId, customerId);
        if (updated == 0) {
            throw new RuntimeException("This tour is no longer available.");
        }
    }

    public void reschedule(long customerId, long tourId, CustomerRescheduleRequest req) {
        if (req.getRequestedDate() == null || req.getRequestedDate().isBlank()
                || req.getTimeSlot() == null || req.getTimeSlot().isBlank()) {
            throw new RuntimeException("Invalid request.");
        }

        LocalDate date = LocalDate.parse(req.getRequestedDate());
        int updated = repo.rescheduleIfOwnedByCustomer(tourId, customerId, date, req.getTimeSlot().trim());
        if (updated == 0) {
            throw new RuntimeException("This tour is no longer available.");
        }
    }
}
