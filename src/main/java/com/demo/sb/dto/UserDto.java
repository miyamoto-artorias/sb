package com.demo.sb.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;
import com.demo.sb.entity.UserType;

@Data
public class UserDto {
    private int id;
    private String username;
    private String email;
    private String password;  // Note: In production, handle securely
    private String fullName;
    private String profilePicture;  // File path or URL
    private String bio;
    private String location;  // e.g., "city, country"
    private List<String> preferredLanguage;
    private Map<String, String> socialLinks;
    private UserType userType;
    // Add mapper method
    public static UserDto fromEntity(com.demo.sb.entity.User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());  // Handle securely
        dto.setFullName(user.getFullName());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setPreferredLanguage(user.getPreferredLanguage());
        dto.setSocialLinks(user.getSocialLinks());
        dto.setUserType(user.getUserType());
        return dto;
    }

    public com.demo.sb.entity.User toEntity() {
        com.demo.sb.entity.User user = new com.demo.sb.entity.User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);  // Hash if needed
        user.setFullName(this.fullName);
        user.setProfilePicture(this.profilePicture);
        user.setBio(this.bio);
        user.setLocation(this.location);
        user.setPreferredLanguage(this.preferredLanguage);
        user.setSocialLinks(this.socialLinks);
        user.setUserType(this.userType);
        return user;
    }
} 