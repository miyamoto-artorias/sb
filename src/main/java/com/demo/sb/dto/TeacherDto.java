package com.demo.sb.dto;

import com.demo.sb.entity.Teacher;
import com.demo.sb.entity.UserType;
import lombok.Data;
import java.util.List;

@Data
public class TeacherDto extends UserDto {
    private List<String> expertise;  // List of expertise
    private List<String> qualification;  // List of qualifications

    public static TeacherDto fromEntity(Teacher teacher) {
        TeacherDto dto = new TeacherDto();
        // Copy from UserDto
        UserDto baseDto = UserDto.fromEntity(teacher);  // Assuming UserDto has fromEntity
        // Manually copy or use a mapper if needed, but for simplicity:
        dto.setId(teacher.getId());
        dto.setUsername(teacher.getUsername());
        dto.setEmail(teacher.getEmail());
        dto.setFullName(teacher.getFullName());
        dto.setProfilePicture(teacher.getProfilePicture());
        dto.setBio(teacher.getBio());
        dto.setLocation(teacher.getLocation());
        dto.setPreferredLanguage(teacher.getPreferredLanguage());
        dto.setSocialLinks(teacher.getSocialLinks());
        dto.setExpertise(teacher.getExpertise());
        dto.setQualification(teacher.getQualification());
        return dto;
    }

    public Teacher toEntity() {
        Teacher teacher = new Teacher();
        // Set common fields from UserDto part
        teacher.setId(this.getId());
        teacher.setUsername(this.getUsername());
        teacher.setEmail(this.getEmail());
        teacher.setPassword(this.getPassword());
        teacher.setFullName(this.getFullName());
        teacher.setProfilePicture(this.getProfilePicture());
        teacher.setBio(this.getBio());
        teacher.setLocation(this.getLocation());
        teacher.setPreferredLanguage(this.getPreferredLanguage());
        teacher.setSocialLinks(this.getSocialLinks());
        teacher.setUserType(UserType.TEACHER); // Explicitly set type

        // Set Teacher-specific fields
        teacher.setExpertise(this.getExpertise());
        teacher.setQualification(this.getQualification());
        
        // Add any other Teacher fields if they exist

        return teacher;
    }
} 