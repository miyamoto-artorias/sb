package com.demo.sb.repository;


import com.demo.sb.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByTeacherId(int teacherId);
    // New method to find courses by category ID
    @Query("SELECT c FROM Course c JOIN c.categories cat WHERE cat.id = :categoryId")
    List<Course> findCoursesByCategoryId(@Param("categoryId") int categoryId);
}