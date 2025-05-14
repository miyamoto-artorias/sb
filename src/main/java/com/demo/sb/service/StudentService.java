package com.demo.sb.service;


import com.demo.sb.entity.Course;
import com.demo.sb.entity.Student;
import com.demo.sb.repository.CourseRepository;
import com.demo.sb.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.demo.sb.dto.StudentDto;
import jakarta.persistence.EntityNotFoundException;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public StudentDto createStudent(StudentDto studentDto) {
        Student student = studentDto.toEntity();  // Convert DTO to entity
        // Encrypt password
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        Student savedStudent = studentRepository.save(student);
        return StudentDto.fromEntity(savedStudent);  // Convert back to DTO
    }

    @Transactional
    public StudentDto updateStudent(StudentDto studentDto) {
        if (!studentRepository.existsById(studentDto.getId())) {
            throw new EntityNotFoundException("Student not found");
        }
        
        // Check if we're updating an existing student
        Student existingStudent = studentRepository.findById(studentDto.getId()).orElse(null);
        Student student = studentDto.toEntity();  // Convert DTO to entity
        
        // Only encrypt the password if it's different from the stored one
        if (existingStudent != null && !studentDto.getPassword().equals(existingStudent.getPassword())) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }
        
        Student updatedStudent = studentRepository.save(student);
        return StudentDto.fromEntity(updatedStudent);  // Convert back to DTO
    }

    public Optional<Student> findById(int id) {
        return studentRepository.findById(id);
    }
/*
    public List<Course> getEnrolledCourses(int studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (student.isPresent()) {
            return student.get().getEnrolledCourses();
        }
        throw new RuntimeException("Student not found");
    } */
    /*
    @Transactional
    public void enrollInCourse(int studentId, int courseId) {
        Optional<Student> student = studentRepository.findById(studentId);
        Optional<Course> course = courseRepository.findById(courseId);
        if (student.isPresent() && course.isPresent()) {
            student.get().getEnrolledCourses().add(course.get());
            studentRepository.save(student.get());
        } else {
            throw new RuntimeException("Student or Course not found");
        }
    } */
}