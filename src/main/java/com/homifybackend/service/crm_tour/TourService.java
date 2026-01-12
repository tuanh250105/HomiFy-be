package com.homifybackend.service.crm_tour;

import com.homifybackend.dto.TourDTO;
import com.homifybackend.model.Customer;
import com.homifybackend.model.Tour;
import com.homifybackend.model.User;
import com.homifybackend.repository.UserRepository;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.repository.TourRepository;
import com.homifybackend.repository.SaleListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TourService {
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;
    private final SaleListingRepository saleListingRepository;
    private final UserRepository userRepository;

    public TourService(TourRepository tourRepository,
                       CustomerRepository customerRepository,
                       SaleListingRepository saleListingRepository,
                       UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.customerRepository = customerRepository;
        this.saleListingRepository = saleListingRepository;
        this.userRepository = userRepository;
    }

    // Hàm lấy danh sách Tour theo đúng Agent ID
    public List<Tour> findToursByAgent(Long agentId) {
        return tourRepository.findBySaleListingAgentUserId(agentId);
    }

    public List<Tour> findAllTours() {
        return tourRepository.findAll();
    }

    @Transactional
    public Tour createTourFromDTO(TourDTO dto, Long userId) {
        Tour tour = new Tour();
        tour.setStatus("PENDING");
        tour.setRescheduleCount(0);
        Customer currentCustomer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

        tour.setRequester(currentCustomer);
        tour.setBuyer(currentCustomer.getFullName());
        if (dto.getListingId() != null) {
            saleListingRepository.findById(dto.getListingId()).ifPresent(listing -> {
                tour.setSaleListing(listing);
                if (listing.getProperty() != null) {
                    tour.setProperty(listing.getProperty().getPropertyType());
                }
            });
        }
        if (dto.getPreferredTimes() != null && !dto.getPreferredTimes().isEmpty()) {
            tour.setDate(dto.getPreferredTimes().get(0).getDate());
            tour.setTime(dto.getPreferredTimes().get(0).getTime());
        }
        return tourRepository.save(tour);
    }

    @Transactional
    public Optional<Tour> updateTourStatus(Long id, Tour data) {
        return tourRepository.findById(id).map(existingTour -> {
            String oldStatus = existingTour.getStatus();
            String newStatus = data.getStatus();

            boolean isRescheduling = (data.getDate() != null && !data.getDate().equals(existingTour.getDate())) ||
                    (data.getTime() != null && !data.getTime().equals(existingTour.getTime()));

            if ("APPROVED".equalsIgnoreCase(newStatus) || isRescheduling) {
                String checkDate = data.getDate() != null ? data.getDate() : existingTour.getDate();
                String checkTime = data.getTime() != null ? data.getTime() : existingTour.getTime();

                boolean conflict = tourRepository.existsByDateAndTimeAndStatusIgnoreCaseAndIdNot(
                        checkDate, checkTime, "APPROVED", id);

                if (conflict) return null;
            }

            if (newStatus != null) existingTour.setStatus(newStatus.toUpperCase());
            if (data.getDate() != null) existingTour.setDate(data.getDate());
            if (data.getTime() != null) existingTour.setTime(data.getTime());
            if (data.getRescheduleCount() != null) existingTour.setRescheduleCount(data.getRescheduleCount());

            customerRepository.findByFullName(existingTour.getBuyer()).ifPresent(customer -> {
                int currentScore = customer.getInterestScore();
                int scoreChange = 0;

                if ("APPROVED".equalsIgnoreCase(newStatus) && !"APPROVED".equalsIgnoreCase(oldStatus)) {
                    scoreChange += 20;
                }
                if ("CANCELED".equalsIgnoreCase(newStatus) && !"CANCELED".equalsIgnoreCase(oldStatus)) {
                    scoreChange -= 20;
                }
                if (isRescheduling) {
                    scoreChange -= 10;
                }
                if (scoreChange != 0) {
                    int finalScore = Math.max(0, Math.min(100, currentScore + scoreChange));
                    customer.setInterestScore(finalScore);
                    customer.setIsFavorite(finalScore >= 85);
                    customerRepository.save(customer);
                }
            });

            return tourRepository.save(existingTour);
        });
    }
}