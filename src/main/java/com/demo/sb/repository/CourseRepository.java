package com.demo.sb.repository;


import com.demo.sb.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByTeacherId(int teacherId);
    
    // Find only public courses
    List<Course> findByIsPublicTrue();
    
    // Find courses by teacher that are either public or private
    List<Course> findByTeacherIdAndIsPublicTrue(int teacherId);
    
    // Find courses by category ID
    @Query("SELECT c FROM Course c JOIN c.categories cat WHERE cat.id = :categoryId")
    List<Course> findCoursesByCategoryId(@Param("categoryId") int categoryId);
    
    // Basic search methods (kept for backward compatibility)
    List<Course> findByTitleContainingIgnoreCase(String searchTerm);
    List<Course> findByDescriptionContainingIgnoreCase(String searchTerm);
    
    // Advanced PostgreSQL full-text search across all relevant fields with special handling for tags
    @Query(value = 
        "WITH search_terms AS (SELECT regexp_split_to_table(:searchTerms, E'\\\\s+') as term) " +
        "SELECT DISTINCT c.* FROM course c " +
        "LEFT JOIN course_category cc ON c.id = cc.course_id " +
        "LEFT JOIN category cat ON cc.category_id = cat.id " +
        "LEFT JOIN course_tags ct ON c.id = ct.course_id " +
        "WHERE " +
        "to_tsvector('english', COALESCE(c.title, '') || ' ' || " +
        "COALESCE(c.description, '') || ' ' || " +
        "COALESCE(cat.title, '')) @@ plainto_tsquery('english', :searchTerms) " +
        "OR " +
        "EXISTS (SELECT 1 FROM search_terms st " +
        "        WHERE EXISTS (SELECT 1 FROM course_tags t " +
        "                     WHERE t.course_id = c.id " +
        "                     AND LOWER(t.tag) LIKE CONCAT('%', LOWER(st.term), '%')))", 
        nativeQuery = true)
    List<Course> fullTextSearch(@Param("searchTerms") String searchTerms);
}