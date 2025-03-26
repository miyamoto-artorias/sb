package com.demo.sb.controllers;


import com.demo.sb.entity.CourseRequest;
import com.demo.sb.service.CourseRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/course-requests")
public class CourseRequestController {
    @Autowired
    private CourseRequestService courseRequestService;

    @PostMapping
    public ResponseEntity<CourseRequest> createCourseRequest(@Valid @RequestBody CourseRequest request) {
        CourseRequest savedRequest = courseRequestService.createCourseRequest(request); // Assume this method exists
        return ResponseEntity.ok(savedRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseRequest> getCourseRequestById(@PathVariable int id) {
        Optional<CourseRequest> request = courseRequestService.findById(id); // Assume this method exists
        return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<CourseRequest>> getRequestsByTeacher(@PathVariable int teacherId) {
        List<CourseRequest> requests = courseRequestService.getRequestsByTeacher(teacherId); // Assume this method exists
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptRequest(@PathVariable int id) {
        courseRequestService.acceptRequest(id); // Assume this method exists
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable int id) {
        courseRequestService.rejectRequest(id); // Assume this method exists
        return ResponseEntity.ok().build();
    }
}