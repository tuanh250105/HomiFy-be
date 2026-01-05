package com.homifybackend.controller.crm_tour;

import com.homifybackend.model.Customer;
import com.homifybackend.model.Property;
import com.homifybackend.service.crm_tour.CustomerService;
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

    @GetMapping("/{id}/suggestions")
    public List<Property> getSuggestions(@PathVariable Long id) {
        return service.getSuggestedProperties(id);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @RequestBody Customer customer) {
        return service.updateCustomer(id, customer);
    }

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
}
