package com.demo.sb.service;


import com.demo.sb.entity.Course;
import com.demo.sb.entity.Teacher;
import com.demo.sb.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.demo.sb.dto.TeacherDto;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Transactional
    public TeacherDto createTeacher(TeacherDto teacherDto) {
        Teacher teacher = teacherDto.toEntity();  // Convert DTO to entity
        Teacher savedTeacher = teacherRepository.save(teacher);
        return TeacherDto.fromEntity(savedTeacher);  // Convert back to DTO
    }

    @Transactional
    public TeacherDto updateTeacher(TeacherDto teacherDto) {
        if (!teacherRepository.existsById(teacherDto.getId())) {
            throw new EntityNotFoundException("Teacher not found");
        }
        Teacher teacher = teacherDto.toEntity();  // Convert DTO to entity
        Teacher updatedTeacher = teacherRepository.save(teacher);
        return TeacherDto.fromEntity(updatedTeacher);  // Convert back to DTO
    }

    public Optional<Teacher> findById(int id) {
        return teacherRepository.findById(id);
    }

    public List<Course> getUploadedCourses(int teacherId) {
        Optional<Teacher> teacher = teacherRepository.findById(teacherId);
        if (teacher.isPresent()) {
            return teacher.get().getUploadedCourses();
        }
        throw new RuntimeException("Teacher not found");
    }
}