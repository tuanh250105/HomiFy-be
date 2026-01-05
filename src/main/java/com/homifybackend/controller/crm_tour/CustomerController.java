package com.homifybackend.controller.crm_tour;

import com.homifybackend.dto.CustomerDTO;
import com.homifybackend.dto.AgentPropertyDTO;
import com.homifybackend.mapper.CustomerMapper;
import com.homifybackend.model.Customer;
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
}
