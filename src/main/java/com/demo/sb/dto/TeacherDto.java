package com.demo.sb.dto;

import com.demo.sb.entity.Teacher;
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
        // Set from User fields
        com.demo.sb.entity.User user = this.toEntity();  // From UserDto
        teacher.setId(user.getId());
        teacher.setUsername(user.getUsername());
        teacher.setEmail(user.getEmail());
        teacher.setFullName(user.getFullName());
        teacher.setProfilePicture(user.getProfilePicture());
        teacher.setBio(user.getBio());
        teacher.setLocation(user.getLocation());
        teacher.setPreferredLanguage(user.getPreferredLanguage());
        teacher.setSocialLinks(user.getSocialLinks());
        teacher.setExpertise(this.getExpertise());
        teacher.setQualification(this.getQualification());
        return teacher;
    }
} 