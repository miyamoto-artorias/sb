package com.demo.sb.service;


import com.demo.sb.dto.CourseChapterDTO;
import com.demo.sb.entity.Course;
import com.demo.sb.entity.CourseChapter;
import com.demo.sb.repository.CourseChapterRepository;
import com.demo.sb.repository.CourseRepository; // Assume this exists
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CourseChapterService {

    @Autowired
    private CourseChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository; // For linking to Course

    // Create new chapter

    @Transactional
    public CourseChapter createChapter(CourseChapterDTO chapterDTO, int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        CourseChapter chapter = new CourseChapter();
        chapter.setTitle(chapterDTO.getTitle());
        chapter.setDescription(chapterDTO.getDescription());
        chapter.setType(chapterDTO.getType());
        chapter.setCourse(course);

        return chapterRepository.save(chapter);
    }


    // Get chapter by ID
    public CourseChapter getChapterById(int id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter with ID " + id + " not found"));
    }

    // Get all chapters for a course
    public List<CourseChapter> getChaptersByCourseId(int courseId) {
        return chapterRepository.findByCourseId(courseId);
    }

    // Update chapter
    @Transactional
    public CourseChapter updateChapter(int id, CourseChapter updatedChapter) {
        CourseChapter existingChapter = getChapterById(id);

        existingChapter.setTitle(updatedChapter.getTitle());
        existingChapter.setDescription(updatedChapter.getDescription());
        existingChapter.setType(updatedChapter.getType());

        return chapterRepository.save(existingChapter);
    }

    // Delete chapter
    @Transactional
    public void deleteChapter(int id) {
        CourseChapter chapter = getChapterById(id);
        chapterRepository.delete(chapter);
    }
}