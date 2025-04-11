package com.demo.sb.controllers;

import com.demo.sb.entity.Enrollment;
import com.demo.sb.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;



    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(
            @RequestParam Integer userId,
            @RequestParam Integer courseId) {
        Enrollment createdEnrollment = enrollmentService.createEnrollment(userId, courseId);
        return new ResponseEntity<>(createdEnrollment, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Integer id) {
        return enrollmentService.getEnrollmentById(id)
                .map(enrollment -> new ResponseEntity<>(enrollment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByUserId(@PathVariable Integer userId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByUserId(userId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourseId(@PathVariable Integer courseId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<Enrollment> updateProgress(
            @PathVariable Integer id,
            @RequestParam float progress) {
        Enrollment updatedEnrollment = enrollmentService.updateProgress(id, progress);
        return new ResponseEntity<>(updatedEnrollment, HttpStatus.OK);
    }

    @PutMapping("/{id}/points")
    public ResponseEntity<Enrollment> updatePoints(
            @PathVariable Integer id,
            @RequestParam int points) {
        Enrollment updatedEnrollment = enrollmentService.updatePoints(id, points);
        return new ResponseEntity<>(updatedEnrollment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Integer id) {
        enrollmentService.deleteEnrollment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }


    /*
    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(
            @RequestParam Integer userId,
            @RequestParam Integer courseId) {
        Enrollment enrollment = enrollmentService.createEnrollment(userId, courseId);
        return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Integer id) {
        return enrollmentService.getEnrollmentById(id)
                .map(enrollment -> new ResponseEntity<>(enrollment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByUserId(@PathVariable Integer userId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByUserId(userId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourseId(@PathVariable Integer courseId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<Enrollment> updateProgress(
            @PathVariable Integer id,
            @RequestParam float progress) {
        Enrollment updatedEnrollment = enrollmentService.updateProgress(id, progress);
        return new ResponseEntity<>(updatedEnrollment, HttpStatus.OK);
    }

    @PutMapping("/{id}/points")
    public ResponseEntity<Enrollment> updatePoints(
            @PathVariable Integer id,
            @RequestParam int points) {
        Enrollment updatedEnrollment = enrollmentService.updatePoints(id, points);
        return new ResponseEntity<>(updatedEnrollment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Integer id) {
        enrollmentService.deleteEnrollment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } */
}