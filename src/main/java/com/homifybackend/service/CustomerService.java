package com.homifybackend.service;

import com.homifybackend.model.Customer;
import com.homifybackend.repository.CustomerRepository;
import org.springframework.stereotype.Service;
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

    public Customer create(Customer customer) {
        return repo.save(customer);
    }

    public Customer updateCustomer(Long id, Customer details) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + id));
        customer.setFullName(details.getFullName());
        customer.setPhoneNumber(details.getPhoneNumber());
        customer.setDemand(details.getDemand());
        if (customer.getInterestScore() != null && customer.getInterestScore() >= 85) {
            customer.setIsFavorite(true);
        }
        return repo.save(customer);
    }

    public Customer updateStatus(Long id, Map<String, String> statusUpdate) {
        Customer customer = repo.findById(id).orElseThrow();
        customer.setPipelineStatus(statusUpdate.get("pipelineStatus"));
        return repo.save(customer);
    }

    public Customer createContract(Long id) {
        Customer customer = repo.findById(id).orElseThrow();
        customer.setPipelineStatus("success");
        customer.setInterestScore(100);
        customer.setIsFavorite(true);
        return repo.save(customer);
    }
}