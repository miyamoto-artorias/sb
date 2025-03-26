package com.demo.sb.controllers;


import com.demo.sb.entity.Course;
import com.demo.sb.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course, @RequestParam int teacherId) {
        Course savedCourse = courseService.createCourse(course, teacherId);
        return ResponseEntity.ok(savedCourse);
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Course>> getCoursesByTeacher(@PathVariable int teacherId) {
        List<Course> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(courses);
    }
}