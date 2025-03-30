package com.demo.sb.controllers;

import com.demo.sb.entity.Course;
import com.demo.sb.entity.Student;
import com.demo.sb.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.createStudent(student); // Assume this method exists
        return ResponseEntity.ok(savedStudent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        Optional<Student> student = studentService.findById(id); // Assume this method exists
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    /*
    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getStudentCourses(@PathVariable int id) {
        List<Course> courses = studentService.getEnrolledCourses(id); // Assume this method exists
        return ResponseEntity.ok(courses);
    } */
    /*
    @PostMapping("/{id}/enroll/{courseId}")
    public ResponseEntity<Void> enrollInCourse(@PathVariable int id, @PathVariable int courseId) {
        studentService.enrollInCourse(id, courseId); // Assume this method exists
        return ResponseEntity.ok().build();
    }*/
}