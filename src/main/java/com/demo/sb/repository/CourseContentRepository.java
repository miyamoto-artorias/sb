package com.demo.sb.repository;


import com.demo.sb.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Integer> {
    // Find all content by chapter ID
    List<CourseContent> findByChapterId(int chapterId);

    // Find content by type and chapter ID
    List<CourseContent> findByTypeAndChapterId(String type, int chapterId);
}