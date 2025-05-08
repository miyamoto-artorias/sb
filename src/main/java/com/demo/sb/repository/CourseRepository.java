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
    
    // Search by title
    List<Course> findByTitleContainingIgnoreCase(String searchTerm);
    
    // Search by description
    List<Course> findByDescriptionContainingIgnoreCase(String searchTerm);
    
    // Search by category name - Using native query to avoid JPQL validation issues
    @Query(value = "SELECT DISTINCT c.* FROM course c " +
           "JOIN course_category cc ON c.id = cc.course_id " +
           "JOIN category cat ON cc.category_id = cat.id " +
           "WHERE LOWER(cat.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))", 
           nativeQuery = true)
    List<Course> findByCategoryName(@Param("searchTerm") String searchTerm);
    
    // Search by tags (using native query for element collection)
    @Query(value = "SELECT DISTINCT c.* FROM course c " +
           "JOIN course_tags ct ON c.id = ct.course_id " +
           "WHERE LOWER(ct.tag) LIKE LOWER(CONCAT('%', :searchTerm, '%'))", 
           nativeQuery = true)
    List<Course> findByTagsContaining(@Param("searchTerm") String searchTerm);
}