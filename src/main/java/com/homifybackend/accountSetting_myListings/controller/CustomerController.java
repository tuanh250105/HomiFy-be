package com.homifybackend.accountSetting_myListings.controller;

import com.homifybackend.model.Customer;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;

    @GetMapping
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
