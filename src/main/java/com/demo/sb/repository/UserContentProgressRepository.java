package com.demo.sb.repository;

import com.demo.sb.entity.CourseContent;
import com.demo.sb.entity.Enrollment;
import com.demo.sb.entity.User;
import com.demo.sb.entity.UserContentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserContentProgressRepository extends JpaRepository<UserContentProgress, Integer> {
    
    // Find progress by user, content, and enrollment
    Optional<UserContentProgress> findByUserAndContentAndEnrollment(User user, CourseContent content, Enrollment enrollment);
    
    // Find all progress for a specific enrollment
    List<UserContentProgress> findByEnrollment_Id(int enrollmentId);
    
    // Find all progress for a user across all enrollments
    List<UserContentProgress> findByUser_Id(int userId);
    
    // Find all completed content for a specific enrollment
    List<UserContentProgress> findByEnrollment_IdAndCompletedTrue(int enrollmentId);
    
    // Count total completed content for an enrollment
    @Query("SELECT COUNT(ucp) FROM UserContentProgress ucp WHERE ucp.enrollment.id = ?1 AND ucp.completed = true")
    int countCompletedContentByEnrollmentId(int enrollmentId);
} 