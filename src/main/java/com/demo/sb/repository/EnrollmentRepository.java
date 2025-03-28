package com.demo.sb.repository;

import com.demo.sb.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByUserId(int userId);
    List<Enrollment> findByCourseId(int courseId);
}
