package com.demo.sb.service;


import com.demo.sb.entity.Course;
import com.demo.sb.entity.Teacher;
import com.demo.sb.repository.CourseRepository;
import com.demo.sb.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class CourseService {
    @Autowired private CourseRepository courseRepository;
    @Autowired private TeacherRepository teacherRepository;

    @Transactional
    public Course createCourse(Course course, int teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        // Optional: Verify teacher exists (not strictly necessary since an empty list is fine)
        teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher with ID " + teacherId + " not found"));
        return courseRepository.findByTeacherId(teacherId);
    }

    public Course getCourseById(int id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + id));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}