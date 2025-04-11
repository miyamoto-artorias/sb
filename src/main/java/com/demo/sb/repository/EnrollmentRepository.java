package com.demo.sb.repository;

import com.demo.sb.entity.Enrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    // Use underscore notation to navigate relationships
    List<Enrollment> findByUser_Id(Integer userId);
    List<Enrollment> findByCourse_Id(Integer courseId);
    Optional<Enrollment> findByUser_IdAndCourse_Id(Integer userId, Integer courseId);

    // Keep your custom query
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.course")
    List<Enrollment> findAllWithUserAndCourseIds();


}
