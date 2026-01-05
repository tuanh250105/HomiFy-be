package com.homifybackend.service.crm;

import com.homifybackend.model.Customer;
import com.homifybackend.model.Property;
import com.homifybackend.model.TourRequestStatus;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.repository.PropertyRepository;
import com.homifybackend.repository.TourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository repo;
    private final PropertyRepository propertyRepository;
    private final TourRepository tourRepository;

    public CustomerService(CustomerRepository repo,
                           PropertyRepository propertyRepository,
                           TourRepository tourRepository) {
        this.repo = repo;
        this.propertyRepository = propertyRepository;
        this.tourRepository = tourRepository;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = repo.findAll();
        for (Customer c : customers) {
            List<Property> viewed = tourRepository.findByRequesterUserIdAndStatus(c.getUserId(), TourRequestStatus.APPROVED.name())
                    .stream()
                    .filter(tour -> tour != null && tour.getSaleListing() != null)
                    .map(tour -> tour.getSaleListing().getProperty())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            c.setViewedHouses(viewed);
        }
        return customers;
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer details) {
        Customer customer = repo.findById(id).orElseThrow();
        if (details.getFullName() != null) customer.setFullName(details.getFullName());
        if (details.getPhoneNumber() != null) customer.setPhoneNumber(details.getPhoneNumber());
        customer.setDemand(details.getDemand());
        return repo.save(customer);
    }

    @Transactional
    public Customer updateStatus(Long id, Map<String, String> statusUpdate) {
        Customer customer = repo.findById(id).orElseThrow();
        String newStatus = statusUpdate.get("pipelineStatus");
        if (newStatus != null) customer.setPipelineStatus(newStatus);
        return repo.save(customer);
    }

    @Transactional
    public Customer toggleFavorite(Long id, Map<String, Boolean> favoriteUpdate) {
        Customer customer = repo.findById(id).orElseThrow();
        Boolean isFavorite = favoriteUpdate.get("isFavorite");
        if (isFavorite != null) customer.setIsFavorite(isFavorite);
        return repo.save(customer);
    }

    @Transactional
    public Customer createContract(Long id) {
        Customer customer = repo.findById(id).orElseThrow();
        customer.setPipelineStatus("success");
        customer.setInterestScore(100);
        customer.setIsFavorite(true);
        return repo.save(customer);
    }

    public List<Property> getSuggestedProperties(Long customerId) {
        Customer customer = repo.findById(customerId).orElseThrow();
        String demand = customer.getDemand() != null ? customer.getDemand().toLowerCase() : "";

        double targetArea = 0;
        try {
            Matcher matcher = Pattern.compile("(\\d+)").matcher(demand);
            if (matcher.find()) {
                targetArea = Double.parseDouble(matcher.group(1));
            }
        } catch (Exception e) { }

        final double finalTargetArea = targetArea;

        return propertyRepository.findAll().stream()
                .filter(p -> {
                    if (demand.isEmpty()) return false;
                    String city = (p.getAddress() != null && p.getAddress().getCity() != null)
                            ? p.getAddress().getCity().toLowerCase() : "";
                    String street = (p.getAddress() != null && p.getAddress().getStreet() != null)
                            ? p.getAddress().getStreet().toLowerCase() : "";
                    String desc = p.getDescription() != null ? p.getDescription().toLowerCase() : "";

                    boolean locationMatch = demand.contains(city) || city.contains(demand) ||
                            demand.contains(street) || desc.contains(demand);

                    boolean areaMatch = true;
                    if (finalTargetArea > 0) {
                        double pArea = p.getArea();
                        areaMatch = pArea >= (finalTargetArea * 0.7) && pArea <= (finalTargetArea * 1.3);
                    }
                    return locationMatch && areaMatch;
                })
                .limit(10)
                .collect(Collectors.toList());
    }
}