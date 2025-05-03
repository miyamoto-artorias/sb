package com.demo.sb.dto;

import com.demo.sb.entity.Student;
import com.demo.sb.entity.UserType;
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
        // Set common fields from UserDto part
        student.setId(this.getId());
        student.setUsername(this.getUsername());
        student.setEmail(this.getEmail());
        student.setPassword(this.getPassword());
        student.setFullName(this.getFullName());
        student.setProfilePicture(this.getProfilePicture());
        student.setBio(this.getBio());
        student.setLocation(this.getLocation());
        student.setPreferredLanguage(this.getPreferredLanguage());
        student.setSocialLinks(this.getSocialLinks());
        student.setUserType(UserType.STUDENT); // Explicitly set type

        return student;
    }
}