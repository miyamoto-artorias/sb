package com.demo.sb.controllers;

import com.demo.sb.entity.UserContentProgress;
import com.demo.sb.service.ProgressService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;
    
    /**
     * Get progress for a specific enrollment
     */
    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<?> getEnrollmentProgress(@PathVariable int enrollmentId) {
        try {
            Map<String, Object> progress = progressService.getEnrollmentProgress(enrollmentId);
            return ResponseEntity.ok(progress);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mark content as completed
     */
    @PostMapping("/user/{userId}/content/{contentId}/enrollment/{enrollmentId}/complete")
    public ResponseEntity<?> markContentAsCompleted(
            @PathVariable int userId,
            @PathVariable int contentId,
            @PathVariable int enrollmentId) {
        try {
            UserContentProgress progress = progressService.markContentAsCompleted(userId, contentId, enrollmentId);
            return ResponseEntity.ok(Map.of(
                "message", "Content marked as completed",
                "contentId", contentId,
                "completed", true,
                "enrollmentProgress", progressService.getEnrollmentProgress(enrollmentId)
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mark content as not completed
     */
    @PostMapping("/user/{userId}/content/{contentId}/enrollment/{enrollmentId}/uncomplete")
    public ResponseEntity<?> markContentAsNotCompleted(
            @PathVariable int userId,
            @PathVariable int contentId,
            @PathVariable int enrollmentId) {
        try {
            progressService.markContentAsNotCompleted(userId, contentId, enrollmentId);
            return ResponseEntity.ok(Map.of(
                "message", "Content marked as not completed",
                "contentId", contentId,
                "completed", false,
                "enrollmentProgress", progressService.getEnrollmentProgress(enrollmentId)
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Simplified endpoint to toggle content completion status
     */
    @PostMapping("/toggle-completion")
    public ResponseEntity<?> toggleContentCompletion(@RequestBody Map<String, Integer> requestBody) {
        try {
            Integer userId = requestBody.get("userId");
            Integer contentId = requestBody.get("contentId");
            Integer enrollmentId = requestBody.get("enrollmentId");
            Boolean markAsCompleted = requestBody.get("completed") == 1;
            
            if (userId == null || contentId == null || enrollmentId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Missing required fields: userId, contentId, enrollmentId"));
            }
            
            if (markAsCompleted) {
                progressService.markContentAsCompleted(userId, contentId, enrollmentId);
            } else {
                progressService.markContentAsNotCompleted(userId, contentId, enrollmentId);
            }
            
            return ResponseEntity.ok(Map.of(
                "message", "Content completion status updated",
                "contentId", contentId,
                "completed", markAsCompleted,
                "enrollmentProgress", progressService.getEnrollmentProgress(enrollmentId)
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get completion status for a specific content item
     */
    @GetMapping("/user/{userId}/content/{contentId}/enrollment/{enrollmentId}/status")
    public ResponseEntity<?> getContentCompletionStatus(
            @PathVariable int userId,
            @PathVariable int contentId,
            @PathVariable int enrollmentId) {
        try {
            Map<String, Object> status = progressService.getContentCompletionStatus(userId, contentId, enrollmentId);
            return ResponseEntity.ok(status);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
} 