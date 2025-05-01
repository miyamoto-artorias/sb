package com.demo.sb.controllers;

import com.demo.sb.entity.Course;
import com.demo.sb.entity.Student;
import com.demo.sb.dto.StudentDto;
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
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        StudentDto createdStudentDto = studentService.createStudent(studentDto);
        return ResponseEntity.ok(createdStudentDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable int id, @Valid @RequestBody StudentDto studentDto) {
        studentDto.setId(id);  // Ensure ID is set
        StudentDto updatedStudentDto = studentService.updateStudent(studentDto);
        return ResponseEntity.ok(updatedStudentDto);
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