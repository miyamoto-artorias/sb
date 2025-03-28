package com.demo.sb.service;


import com.demo.sb.entity.User;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    public User getUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }


    @Transactional
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        return userRepository.save(user);
    }
/* //same thing as above but withou encoding
    public User createUser(User user) {
        return userRepository.save(user);
    } */

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("User not found");
        }
        return userRepository.save(user);
    }
}