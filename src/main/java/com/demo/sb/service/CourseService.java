package com.demo.sb.service;


import com.demo.sb.dto.CourseDTO;
import com.demo.sb.entity.Category;
import com.demo.sb.entity.Course;
import com.demo.sb.entity.CourseRequest;
import com.demo.sb.entity.Teacher;
import com.demo.sb.repository.CategoryRepository;
import com.demo.sb.repository.CourseRepository;
import com.demo.sb.repository.CourseRequestRepository;
import com.demo.sb.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {
    @Autowired private CourseRepository courseRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private CourseRequestRepository courseRequestRepository;
    @Autowired private CategoryRepository categoryRepository;

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
    }    @Transactional
    public Course createCourseForRequest(int courseRequestId, int teacherId, CourseDTO courseDto) {
        // Validate and retrieve the course request
        CourseRequest courseRequest = courseRequestRepository.findById(courseRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Course request not found"));
                
        // Check if the course request status is "accepted"
        if (!"accepted".equalsIgnoreCase(courseRequest.getStatus())) {
            throw new IllegalStateException("Course request status must be 'accepted' to create a course");
        }

        // Create a new course
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setPicture(courseDto.getPicture());
        course.setPrice(courseDto.getPrice());
        course.setTags(courseDto.getTags());
        course.setPublic(false); // Set isPublic to false for requested courses
        
        // Link the course to the courseRequest
        course.setCourseRequest(courseRequest);

        // Map category IDs to actual Category entities
        List<Category> categories = categoryRepository.findAllById(courseDto.getCategoryIds());
        course.setCategories(categories);

        // Set the teacher
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
        course.setTeacher(teacher);
        
        Course savedCourse = courseRepository.save(course);
        
        // Update course request status to indicate a course has been created
        courseRequest.setCreatedCourse(savedCourse);
        courseRequest.setStatus("done");
        courseRequestRepository.save(courseRequest);

        return savedCourse;
    }

    public Course getCourseById(int id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + id));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    public List<Course> searchCourses(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCourses();
        }
        
        // Use PostgreSQL full-text search for complex queries with multiple keywords
        return courseRepository.fullTextSearch(searchTerm.trim());
    }
}