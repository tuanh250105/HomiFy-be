package com.homifybackend.controller.crm_tour;

import com.homifybackend.model.Customer;
import com.homifybackend.service.crm.CustomerService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crm/customers")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> getAll() {
        return service.getAllCustomers();
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @RequestBody Customer customer) {
        return service.updateCustomer(id, customer);
    }
    // Thêm vào CustomerController.java
    @PutMapping("/{id}/favorite")
    public Customer toggleFavorite(@PathVariable Long id, @RequestBody Map<String, Boolean> update) {
        return service.toggleFavorite(id, update);
    }

    @PutMapping("/{id}/status")
    public Customer updateStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        return service.updateStatus(id, statusUpdate);
    }

    @PostMapping("/{id}/contract")
    public Customer createContract(@PathVariable Long id) {
        return service.createContract(id);
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return service.create(customer);
    }
}