package com.demo.sb.controllers;


import com.demo.sb.entity.User;
import com.demo.sb.entity.UserType;
import com.demo.sb.dto.UserDto;
import com.demo.sb.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import java.net.MalformedURLException;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    // Comment out the redundant POST /teachers endpoint
    /*
    @PostMapping("/teachers")
    public ResponseEntity<?> createTeacher(@Valid @RequestBody UserDto userDto) {
        // Validate userType
        if (userDto.getUserType() != null && userDto.getUserType() != UserType.TEACHER) {
            return ResponseEntity.badRequest().body("UserType must be TEACHER for this endpoint.");
        }
        userDto.setUserType(UserType.TEACHER);  // Ensure it's set correctly
        UserDto createdUserDto = userService.createUser(userDto);
        return ResponseEntity.ok(createdUserDto);
    }
    */

    // Comment out the redundant POST /students endpoint as well
    /*
    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@Valid @RequestBody UserDto userDto) {
        // Validate userType
        if (userDto.getUserType() != null && userDto.getUserType() != UserType.STUDENT) {
            return ResponseEntity.badRequest().body("UserType must be STUDENT for this endpoint.");
        }
        userDto.setUserType(UserType.STUDENT);  // Ensure it's set correctly
        UserDto createdUserDto = userService.createUser(userDto);
        return ResponseEntity.ok(createdUserDto);
    }
    */

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

    @PostMapping("{id}/upload-profile-picture")
    public ResponseEntity<UserDto> uploadProfilePicture(@PathVariable int id, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        // Save the file to the specified directory
        Path uploadDir = Paths.get("C:\\Users\\altya\\Desktop\\proj_integ\\SBDB\\userPorfilePics");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        String fileName = id + "_profile.jpg";  // Simple naming convention
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, file.getBytes());
        
        // Update the user with the new file path
        UserDto userDto = userService.findById(id).map(UserDto::fromEntity).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userDto.setProfilePicture(uploadDir.resolve(fileName).toString());  // Set the full path
        UserDto updatedUserDto = userService.updateUser(userDto);
        return ResponseEntity.ok(updatedUserDto);
    }

    @GetMapping("{id}/profile-picture")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable int id) {
        try {
            UserDto userDto = userService.findById(id).map(UserDto::fromEntity).orElseThrow(() -> new EntityNotFoundException("User not found"));
            String filePathStr = userDto.getProfilePicture();  // Get the full path
            Path filePath = Paths.get(filePathStr);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)  // Assume JPEG, adjust if needed
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}