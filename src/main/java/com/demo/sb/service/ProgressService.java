package com.demo.sb.service;

import com.demo.sb.entity.*;
import com.demo.sb.repository.CourseContentRepository;
import com.demo.sb.repository.EnrollmentRepository;
import com.demo.sb.repository.UserContentProgressRepository;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    @Autowired
    private UserContentProgressRepository progressRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseContentRepository contentRepository;
    
    /**
     * Mark a content item as completed for a user's enrollment
     */
    @Transactional
    public UserContentProgress markContentAsCompleted(int userId, int contentId, int enrollmentId) {
        // Get the entities from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
        CourseContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found with ID: " + contentId));
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + enrollmentId));
        
        // Verify that this content belongs to the enrolled course
        boolean contentBelongsToCourse = enrollment.getCourse().getChapters().stream()
                .flatMap(chapter -> chapter.getContents().stream())
                .anyMatch(c -> c.getId() == contentId);
                
        if (!contentBelongsToCourse) {
            throw new IllegalArgumentException("Content does not belong to the enrolled course");
        }
        
        // Check if progress entry already exists
        Optional<UserContentProgress> existingProgress = progressRepository.findByUserAndContentAndEnrollment(
                user, content, enrollment);
        
        UserContentProgress progress;
        if (existingProgress.isPresent()) {
            // Update existing progress
            progress = existingProgress.get();
            progress.setCompleted(true);
            progress.setCompletedDate(LocalDateTime.now());
        } else {
            // Create new progress entry
            progress = new UserContentProgress(user, content, enrollment);
            progress.setCompleted(true);
            progress.setCompletedDate(LocalDateTime.now());
            enrollment.getContentProgresses().add(progress);
        }
        
        // Save progress
        progress = progressRepository.save(progress);
        
        // Update enrollment progress
        enrollment.updateProgress();
        enrollmentRepository.save(enrollment);
        
        return progress;
    }
    
    /**
     * Mark a content item as not completed
     */
    @Transactional
    public void markContentAsNotCompleted(int userId, int contentId, int enrollmentId) {
        // Get the entities from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
        CourseContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found with ID: " + contentId));
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + enrollmentId));
        
        // Find the progress entry
        Optional<UserContentProgress> existingProgress = progressRepository.findByUserAndContentAndEnrollment(
                user, content, enrollment);
        
        if (existingProgress.isPresent()) {
            UserContentProgress progress = existingProgress.get();
            progress.setCompleted(false);
            progress.setCompletedDate(null);
            progressRepository.save(progress);
            
            // Update enrollment progress
            enrollment.updateProgress();
            enrollmentRepository.save(enrollment);
        }
    }
    
    /**
     * Get course progress for a specific enrollment
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEnrollmentProgress(int enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + enrollmentId));
        
        // Ensure progress is up to date
        enrollment.updateProgress();
        
        // Get list of completed content IDs
        List<Integer> completedContentIds = enrollment.getCompletedContentIds();
        
        // Create progress object
        return Map.of(
            "enrollmentId", enrollment.getId(),
            "userId", enrollment.getUserId(),
            "courseId", enrollment.getCourseId(),
            "progress", enrollment.getProgress(),
            "completedContentIds", completedContentIds
        );
    }
    
    /**
     * Calculate and update progress for all users' enrollments
     */
    @Transactional
    public void updateAllEnrollmentProgress() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        
        for (Enrollment enrollment : enrollments) {
            enrollment.updateProgress();
        }
        
        enrollmentRepository.saveAll(enrollments);
    }
    
    /**
     * Check if a specific content is completed for a user's enrollment
     * @param userId The user ID
     * @param contentId The content ID
     * @param enrollmentId The enrollment ID
     * @return A map containing the completion status
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getContentCompletionStatus(int userId, int contentId, int enrollmentId) {
        // Get the entities from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
        CourseContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found with ID: " + contentId));
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + enrollmentId));
        
        // Find if progress entry exists
        Optional<UserContentProgress> progressOpt = progressRepository.findByUserAndContentAndEnrollment(
                user, content, enrollment);
        
        boolean isCompleted = progressOpt.isPresent() && progressOpt.get().isCompleted();
        
        // Return the completion status
        return Map.of(
            "userId", userId,
            "contentId", contentId,
            "enrollmentId", enrollmentId,
            "completed", isCompleted,
            "completedDate", progressOpt.isPresent() && progressOpt.get().getCompletedDate() != null ? 
                progressOpt.get().getCompletedDate().toString() : null
        );
    }
} 