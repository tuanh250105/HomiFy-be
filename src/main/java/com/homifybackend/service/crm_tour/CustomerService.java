package com.homifybackend.service.crm_tour;

import com.homifybackend.dto.AgentPropertyDTO;
import com.homifybackend.mapper.CustomerMapper;
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

    @Transactional(readOnly = true)
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
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        if (details.getFullName() != null) customer.setFullName(details.getFullName());
        if (details.getPhoneNumber() != null) customer.setPhoneNumber(details.getPhoneNumber());
        if (details.getDemand() != null) customer.setDemand(details.getDemand());
        if (details.getEmail() != null && customer.getAccount() != null) {
            customer.getAccount().setEmail(details.getEmail());
        }

        return repo.save(customer);
    }

    @Transactional
    public Customer updateStatus(Long id, Map<String, String> statusUpdate) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        String newStatus = statusUpdate.get("pipelineStatus");
        if (newStatus != null) {
            customer.setPipelineStatus(newStatus);
            if ("success".equalsIgnoreCase(newStatus)) {
                customer.setInterestScore(100);
            }
        }
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

    @Transactional(readOnly = true)
    public List<AgentPropertyDTO> getSuggestedProperties(Long customerId) {
        Customer customer = repo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        String demand = customer.getDemand() != null ? customer.getDemand().toLowerCase() : "";

        double targetArea = 0;
        try {
            Matcher matcher = Pattern.compile("(\\d+)").matcher(demand);
            if (matcher.find()) {
                targetArea = Double.parseDouble(matcher.group(1));
            }
        } catch (Exception e) {

        }
        final double finalTargetArea = targetArea;

        // 3. Duyệt danh sách nhà và tính điểm Match Score
        return propertyRepository.findAll().stream()
                .map(p -> {
                    if (demand.isEmpty()) return null;

                    int score = 0;
                    String city = (p.getAddress() != null && p.getAddress().getCity() != null)
                            ? p.getAddress().getCity().toLowerCase() : "";
                    String street = (p.getAddress() != null && p.getAddress().getStreet() != null)
                            ? p.getAddress().getStreet().toLowerCase() : "";
                    String type = p.getPropertyType() != null ? p.getPropertyType().toLowerCase() : "";
                    String desc = p.getDescription() != null ? p.getDescription().toLowerCase() : "";


                    // Vị trí (Thành phố/Tên đường) - Tối đa 50%
                    if (!city.isEmpty() && demand.contains(city)) score += 30;
                    if (!street.isEmpty() && demand.contains(street)) score += 20;

                    // Loại nhà hoặc mô tả - Tối đa 30%
                    if (!type.isEmpty() && demand.contains(type)) score += 20;
                    if (desc.contains(demand) || demand.contains(desc)) score += 10;

                    // Diện tích (Sai số trong khoảng 30%) - Tối đa 20%
                    if (finalTargetArea > 0) {
                        double pArea = p.getArea();
                        if (pArea >= (finalTargetArea * 0.7) && pArea <= (finalTargetArea * 1.3)) {
                            score += 20;
                        }
                    }

                    if (score == 0) return null;

                    AgentPropertyDTO dto = CustomerMapper.toPropertyDTO(p);
                    dto.setMatchScore(score);
                    return dto;
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.getMatchScore().compareTo(a.getMatchScore())) // Sắp xếp căn khớp nhất lên đầu
                .limit(10)
                .collect(Collectors.toList());
    }
}