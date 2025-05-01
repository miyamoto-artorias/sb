package com.demo.sb.controllers;


import com.demo.sb.entity.User;
import com.demo.sb.entity.UserType;
import com.demo.sb.dto.UserDto;
import com.demo.sb.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/teachers")
    public ResponseEntity<UserDto> createTeacher(@Valid @RequestBody UserDto userDto) {
        userDto.setUserType(UserType.TEACHER);  // Set userType on the DTO
        UserDto createdUserDto = userService.createUser(userDto);
        return ResponseEntity.ok(createdUserDto);
    }

    @PostMapping("/students")
    public ResponseEntity<UserDto> createStudent(@Valid @RequestBody UserDto userDto) {
        userDto.setUserType(UserType.STUDENT);  // Set userType on the DTO
        UserDto createdUserDto = userService.createUser(userDto);
        return ResponseEntity.ok(createdUserDto);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUserDto = userService.createUser(userDto);
        return ResponseEntity.ok(createdUserDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUser(id));
    }




    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable int id, @Valid @RequestBody UserDto userDto) {
        userDto.setId(id);  // Ensure ID is set
        UserDto updatedUserDto = userService.updateUser(userDto);
        return ResponseEntity.ok(updatedUserDto);
    }
}