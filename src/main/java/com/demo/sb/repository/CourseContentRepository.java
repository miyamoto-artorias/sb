package com.demo.sb.repository;

import com.demo.sb.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Integer> {
    
    // Find all content in a specific chapter
    List<CourseContent> findByChapterId(int chapterId);
    
    // Count total content for a specific course
    @Query("SELECT COUNT(cc) FROM CourseContent cc WHERE cc.chapter.course.id = ?1")
    int countByCourseId(int courseId);
    
    // Find content by type in a specific course
    @Query("SELECT cc FROM CourseContent cc WHERE cc.chapter.course.id = ?1 AND cc.type = ?2")
    List<CourseContent> findByCourseIdAndType(int courseId, String type);
}