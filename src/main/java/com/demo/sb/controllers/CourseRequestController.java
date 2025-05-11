package com.demo.sb.controllers;

import com.demo.sb.dto.CourseRequestDTO;
import com.demo.sb.service.CourseRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    
    /**
     * Update the status of a course request
     * @param id The ID of the course request to update
     * @param requestBody Map containing the new status value
     * @return ResponseEntity with success or error message
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateRequestStatus(@PathVariable int id, @RequestBody Map<String, String> requestBody) {
        try {
            String status = requestBody.get("status");
            if (status == null || status.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Status cannot be empty"));
            }
            
            courseRequestService.updateRequestStatus(id, status);
            return ResponseEntity.ok(Map.of("message", "Course request status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Course request not found"));
        }
    }
}