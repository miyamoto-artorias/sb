package com.demo.sb.repository;


import com.demo.sb.entity.CourseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRequestRepository extends JpaRepository<CourseRequest, Integer> {
    List<CourseRequest> findByStudentId(int studentId);
    List<CourseRequest> findByTeacherId(int teacherId);
    List<CourseRequest> findByStudentIdAndStatus(int studentId, String status);
    List<CourseRequest> findByStudentIdAndStatusIn(int studentId, List<String> statuses);
    //Added custom query methods where useful (e.g., findByTeacherId, findByUsername).
}