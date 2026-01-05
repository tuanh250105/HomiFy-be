package com.homifybackend.controller.crm_tour;

import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.dto.CustomerDTO;
import com.homifybackend.dto.AgentPropertyDTO;
import com.homifybackend.mapper.CustomerMapper;
import com.homifybackend.model.Customer;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.service.crm_tour.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crm/customers")
public class CustomerController {

    private final CustomerService service;
    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;

    public CustomerController(CustomerService service, CustomerRepository customerRepo, UserRepository userRepo) {
        this.service = service;
        this.customerRepo = customerRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public List<CustomerDTO> getAll() {
        return service.getAllCustomers()
                .stream()
                .map(CustomerMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}/suggestions")
    public List<AgentPropertyDTO> getSuggestions(@PathVariable Long id) {
        return service.getSuggestedProperties(id)
                .stream()
                .map(CustomerMapper::toPropertyDTO)
                .toList();
    }

    @PutMapping("/{id}")
    public CustomerDTO update(@PathVariable Long id, @RequestBody Customer customer) {
        return CustomerMapper.toDTO(service.updateCustomer(id, customer));
    }

    @PutMapping("/{id}/favorite")
    public CustomerDTO toggleFavorite(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> update) {
        return CustomerMapper.toDTO(service.toggleFavorite(id, update));
    }

    @PutMapping("/{id}/status")
    public CustomerDTO updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        return CustomerMapper.toDTO(service.updateStatus(id, statusUpdate));
    }

    @PostMapping("/{id}/contract")
    public CustomerDTO createContract(@PathVariable Long id) {
        return CustomerMapper.toDTO(service.createContract(id));
    }
    @GetMapping("/mylisting")
    public List<Map<String, Object>> listCustomers() {
        List<Customer> customers = customerRepo.findAll();
        List<Map<String, Object>> out = new ArrayList<>();

        for (Customer c : customers) {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", c.getUserId());
            userRepo.findById(c.getUserId()).ifPresent(u -> {
                m.put("fullName", u.getFullName());
                m.put("email", null); // nếu users có email thì bạn thêm field vào entity User
                m.put("phoneNumber", u.getPhoneNumber());
            });
            out.add(m);
        }
        return out;
    }


}
