package com.homifybackend.service.crm;

import com.homifybackend.model.Customer;
import com.homifybackend.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public List<Customer> getAllCustomers() {
        return repo.findAll();
    }

    @Transactional
    public Customer create(Customer customer) {
        return repo.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer details) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + id));

        if (details.getFullName() != null) {
            customer.setFullName(details.getFullName());
        }
        if (details.getPhoneNumber() != null) {
            customer.setPhoneNumber(details.getPhoneNumber());
        }

        customer.setDemand(details.getDemand());

        if (customer.getInterestScore() != null && customer.getInterestScore() >= 85) {
            customer.setIsFavorite(true);
        }

        return repo.save(customer);
    }

    @Transactional
    public Customer updateStatus(Long id, Map<String, String> statusUpdate) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + id));

        String newStatus = statusUpdate.get("pipelineStatus");
        if (newStatus != null) {
            customer.setPipelineStatus(newStatus);
        }

        return repo.save(customer);
    }

    // Thêm vào CustomerService.java
    @Transactional
    public Customer toggleFavorite(Long id, Map<String, Boolean> favoriteUpdate) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + id));

        Boolean isFavorite = favoriteUpdate.get("isFavorite");
        if (isFavorite != null) {
            // Kiểm tra điều kiện điểm số nếu bạn muốn giữ quy tắc >= 85 mới được yêu thích
            if (isFavorite && (customer.getInterestScore() == null || customer.getInterestScore() < 85)) {
                throw new RuntimeException("Điểm tiềm năng chưa đủ để yêu thích (>=85)");
            }
            customer.setIsFavorite(isFavorite);
        }
        return repo.save(customer);
    }

    @Transactional
    public Customer createContract(Long id) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + id));

        customer.setPipelineStatus("success");
        customer.setInterestScore(100);
        customer.setIsFavorite(true);

        return repo.save(customer);
    }
}