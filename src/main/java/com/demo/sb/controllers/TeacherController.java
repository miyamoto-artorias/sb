package com.demo.sb.controllers;



import com.demo.sb.entity.Course;
import com.demo.sb.entity.Teacher;
import com.demo.sb.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@Valid @RequestBody Teacher teacher) {
        Teacher savedTeacher = teacherService.createTeacher(teacher); // Assume this method exists
        return ResponseEntity.ok(savedTeacher);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable int id) {
        Optional<Teacher> teacher = teacherService.findById(id); // Assume this method exists
        return teacher.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getTeacherCourses(@PathVariable int id) {
        List<Course> courses = teacherService.getUploadedCourses(id); // Assume this method exists
        return ResponseEntity.ok(courses);
    }
}