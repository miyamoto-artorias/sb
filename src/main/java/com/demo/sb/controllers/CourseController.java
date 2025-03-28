package com.demo.sb.controllers;


import com.demo.sb.entity.Course;
import com.demo.sb.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;



    @PostMapping("/{teacherId}")
    public ResponseEntity<Course> createCourse(@RequestBody Course course, @PathVariable int teacherId) {
        return ResponseEntity.ok(courseService.createCourse(course, teacherId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Course>> getCoursesByTeacher(@PathVariable int teacherId) {
        List<Course> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(courses);
    }
}