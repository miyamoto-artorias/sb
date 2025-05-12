package com.demo.sb.repository;

import com.demo.sb.entity.Enrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    // Use underscore notation to navigate relationships
    List<Enrollment> findByUser_Id(Integer userId);
    List<Enrollment> findByCourse_Id(Integer courseId);
    Optional<Enrollment> findByUser_IdAndCourse_Id(Integer userId, Integer courseId);

    // Keep your custom query
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.course")
    List<Enrollment> findAllWithUserAndCourseIds();

    boolean existsByUser_IdAndCourse_Id(Integer userId, Integer courseId);
    
    // Find all enrollments with progress greater than a certain percentage
    List<Enrollment> findByProgressGreaterThanEqual(float progressPercentage);
    
    // Find completed enrollments (100% progress)
    @Query("SELECT e FROM Enrollment e WHERE e.progress >= 100.0")
    List<Enrollment> findCompletedEnrollments();
}
