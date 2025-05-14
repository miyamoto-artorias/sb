package com.demo.sb.service;


import com.demo.sb.entity.Course;
import com.demo.sb.entity.Teacher;
import com.demo.sb.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.demo.sb.dto.TeacherDto;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public TeacherDto createTeacher(TeacherDto teacherDto) {
        Teacher teacher = teacherDto.toEntity();  // Convert DTO to entity
        // Encrypt password
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        Teacher savedTeacher = teacherRepository.save(teacher);
        return TeacherDto.fromEntity(savedTeacher);  // Convert back to DTO
    }

    @Transactional
    public TeacherDto updateTeacher(TeacherDto teacherDto) {
        if (!teacherRepository.existsById(teacherDto.getId())) {
            throw new EntityNotFoundException("Teacher not found");
        }
        
        // Check if we're updating an existing teacher
        Teacher existingTeacher = teacherRepository.findById(teacherDto.getId()).orElse(null);
        Teacher teacher = teacherDto.toEntity();  // Convert DTO to entity
        
        // Only encrypt the password if it's different from the stored one
        if (existingTeacher != null && !teacherDto.getPassword().equals(existingTeacher.getPassword())) {
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        }
        
        Teacher updatedTeacher = teacherRepository.save(teacher);
        return TeacherDto.fromEntity(updatedTeacher);  // Convert back to DTO
    }

    public Optional<Teacher> findById(int id) {
        return teacherRepository.findById(id);
    }
    
    public List<TeacherDto> getAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();
        return teachers.stream()
                .map(TeacherDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<Course> getUploadedCourses(int teacherId) {
        Optional<Teacher> teacher = teacherRepository.findById(teacherId);
        if (teacher.isPresent()) {
            return teacher.get().getUploadedCourses();
        }
        throw new RuntimeException("Teacher not found");
    }
}