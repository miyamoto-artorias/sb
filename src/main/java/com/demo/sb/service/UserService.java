package com.demo.sb.service;


import com.demo.sb.dto.UserDto;
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
    public UserDto createUser(UserDto userDto) {
        User user = userDto.toEntity();  // Convert DTO to entity
        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);  // Convert back to DTO
    }

    /*@Transactional
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        return userRepository.save(user);
    } */
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
    public UserDto updateUser(UserDto userDto) {
        if (!userRepository.existsById(userDto.getId())) {
            throw new EntityNotFoundException("User not found");
        }
        
        // Check if we're updating an existing user
        User existingUser = userRepository.findById(userDto.getId()).orElse(null);
        User user = userDto.toEntity();  // Convert DTO to entity
        
        // Only encrypt the password if it's different from the stored one
        // This prevents double-encryption when updating user details without changing password
        if (existingUser != null && !userDto.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return UserDto.fromEntity(updatedUser);  // Convert back to DTO
    }
}