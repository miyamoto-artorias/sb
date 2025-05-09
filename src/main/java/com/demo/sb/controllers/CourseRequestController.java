package com.demo.sb.controllers;

import com.demo.sb.dto.CourseRequestDTO;
import com.demo.sb.service.CourseRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-requests")
public class CourseRequestController {
    @Autowired
    private CourseRequestService courseRequestService;

    @PostMapping
    public ResponseEntity<CourseRequestDTO> createCourseRequest(@Valid @RequestBody CourseRequestDTO requestDTO) {
        CourseRequestDTO savedRequest = courseRequestService.createCourseRequest(requestDTO);
        return ResponseEntity.ok(savedRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseRequestDTO> getCourseRequestById(@PathVariable int id) {
        try {
            CourseRequestDTO request = courseRequestService.findById(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<CourseRequestDTO>> getRequestsByTeacher(@PathVariable int teacherId) {
        List<CourseRequestDTO> requests = courseRequestService.getRequestsByTeacher(teacherId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseRequestDTO>> getRequestsByStudent(@PathVariable int studentId) {
        List<CourseRequestDTO> requests = courseRequestService.getRequestsByStudent(studentId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptRequest(@PathVariable int id) {
        try {
            courseRequestService.acceptRequest(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable int id) {
        try {
            courseRequestService.rejectRequest(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}