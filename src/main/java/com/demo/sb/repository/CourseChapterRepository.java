package com.demo.sb.repository;

import com.demo.sb.entity.CourseChapter;
import com.demo.sb.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseChapterRepository extends JpaRepository<CourseChapter, Integer> {

    List<CourseChapter> findByCourseId(int courseId);

}