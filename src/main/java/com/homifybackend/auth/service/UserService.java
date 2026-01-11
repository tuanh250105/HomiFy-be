package com.homifybackend.auth.service;

import com.homifybackend.auth.repository.UserRepository;
import com.homifybackend.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }
}
