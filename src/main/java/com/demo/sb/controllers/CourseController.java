package com.demo.sb.controllers;


import com.demo.sb.dto.CourseDTO;
import com.demo.sb.entity.Course;
import com.demo.sb.service.CourseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;


    @PostMapping("/{teacherId}")
    public ResponseEntity<Course> createCourse(@RequestBody CourseDTO courseDto, @PathVariable int teacherId) {
        // Convert DTO to entity
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setPicture(courseDto.getPicture());
        course.setPrice(courseDto.getPrice());

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
}