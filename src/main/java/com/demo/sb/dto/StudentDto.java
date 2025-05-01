package com.demo.sb.dto;

import com.demo.sb.entity.Student;
import lombok.Data;

@Data
public class StudentDto extends UserDto {
    // Student doesn't have additional fields in the provided code, but it inherits all from UserDto
    public static StudentDto fromEntity(Student student) {
        StudentDto dto = new StudentDto();
        // Copy from UserDto
        UserDto baseDto = UserDto.fromEntity(student);  // Assuming UserDto has fromEntity
        dto.setId(student.getId());
        dto.setUsername(student.getUsername());
        dto.setEmail(student.getEmail());
        dto.setFullName(student.getFullName());
        dto.setProfilePicture(student.getProfilePicture());
        dto.setBio(student.getBio());
        dto.setLocation(student.getLocation());
        dto.setPreferredLanguage(student.getPreferredLanguage());
        dto.setSocialLinks(student.getSocialLinks());
        return dto;
    }

    public Student toEntity() {
        Student student = new Student();
        com.demo.sb.entity.User user = this.toEntity();  // From UserDto
        student.setId(user.getId());
        student.setUsername(user.getUsername());
        student.setEmail(user.getEmail());
        student.setFullName(user.getFullName());
        student.setProfilePicture(user.getProfilePicture());
        student.setBio(user.getBio());
        student.setLocation(user.getLocation());
        student.setPreferredLanguage(user.getPreferredLanguage());
        student.setSocialLinks(user.getSocialLinks());
        return student;
    }
} 