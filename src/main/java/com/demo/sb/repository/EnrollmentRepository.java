package com.demo.sb.repository;

import com.demo.sb.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    // Custom query methods if needed
    List<Enrollment> findByUserId(Integer userId);
    List<Enrollment> findByCourseId(Integer courseId);
    Optional<Enrollment> findByUserIdAndCourseId(Integer userId, Integer courseId);
}
