package com.demo.sb.controllers;


import com.demo.sb.dto.CourseDTO;
import com.demo.sb.entity.Category;
import com.demo.sb.entity.Course;
import com.demo.sb.repository.CategoryRepository;
import com.demo.sb.service.CourseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CategoryRepository categoryRepository;


    @PostMapping("/{teacherId}")
    public ResponseEntity<Course> createCourse(@RequestBody CourseDTO courseDto, @PathVariable int teacherId) {
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setPicture(courseDto.getPicture());
        course.setPrice(courseDto.getPrice());
        course.setTags(courseDto.getTags());

        // Map category IDs to actual Category entities
        List<Category> categories = categoryRepository.findAllById(courseDto.getCategoryIds());
        course.setCategories(categories);

        Course createdCourse = courseService.createCourse(course, teacherId);
        return ResponseEntity.ok(createdCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable int id) {
        try {
            Course course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Course>> getCoursesByTeacher(@PathVariable int teacherId) {
        List<Course> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(@RequestParam String query) {
        List<Course> courses = courseService.searchCourses(query);
        List<CourseDTO> courseDTOs = convertToCourseDTOs(courses);
        return ResponseEntity.ok(courseDTOs);
    }
    
    private List<CourseDTO> convertToCourseDTOs(List<Course> courses) {
        return courses.stream().map(course -> {
            CourseDTO dto = new CourseDTO();
            dto.setTitle(course.getTitle());
            dto.setDescription(course.getDescription());
            dto.setPicture(course.getPicture());
            dto.setPrice(course.getPrice());
            dto.setTags(course.getTags());
            
            // Extract category IDs
            List<Integer> categoryIds = course.getCategories() != null ?
                course.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList()) :
                new ArrayList<>();
            dto.setCategoryIds(categoryIds);
            
            return dto;
        }).collect(Collectors.toList());
    }
}