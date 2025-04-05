package com.demo.sb.service;


import com.demo.sb.entity.Course;
import com.demo.sb.entity.CourseChapter;
import com.demo.sb.entity.CourseContent;
import com.demo.sb.repository.CourseChapterRepository;
import com.demo.sb.repository.CourseContentRepository;
import com.demo.sb.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CourseContentService {

    @Autowired
    private CourseContentRepository contentRepository;

    @Autowired
    private CourseChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository; // Added CourseRepository

    private static final List<String> VALID_TYPES = Arrays.asList("video", "pdf");

    // Modified createContent to include courseId
    @Transactional
    public CourseContent createContent(CourseContent content, int courseId, int chapterId) {
        validateContentType(content.getType());

        // Validate course exists
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found");
        }

        // Validate chapter exists and belongs to the course
        Optional<CourseChapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty()) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " not found");
        }
        if (chapter.get().getCourse().getId() != courseId) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " does not belong to course " + courseId);
        }

        content.setChapter(chapter.get());
        return contentRepository.save(content);
    }

    // New method to get content by courseId and chapterId
    public List<CourseContent> getContentByCourseIdAndChapterId(int courseId, int chapterId) {
        // Validate course exists
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found");
        }

        // Validate chapter exists and belongs to course
        Optional<CourseChapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty()) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " not found");
        }
        if (chapter.get().getCourse().getId() != courseId) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " does not belong to course " + courseId);
        }

        return contentRepository.findByChapterId(chapterId);
    }


    // Modified method to include courseId validation
    public List<CourseContent> getContentByTypeCourseAndChapter(String type, int courseId, int chapterId) {
        validateContentType(type);

        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found");
        }

        Optional<CourseChapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty()) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " not found");
        }
        if (chapter.get().getCourse().getId() != courseId) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " does not belong to course " + courseId);
        }

        return contentRepository.findByTypeAndChapterId(type, chapterId);
    }



/*    public List<CourseContent> getContentByCourseIdAndChapterId(int courseId, int chapterId) {
        // Validate course exists
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found");
        }

        // Validate chapter exists and belongs to course
        Optional<CourseChapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty()) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " not found");
        }
        if (chapter.get().getCourse().getId() != courseId) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " does not belong to course " + courseId);
        }

        return contentRepository.findByChapterId(chapterId);
    }
*/

    // Rest of the existing methods remain unchanged
    public CourseContent getContentById(int id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content with ID " + id + " not found"));
    }

    public List<CourseContent> getContentByChapterId(int chapterId) {
        return contentRepository.findByChapterId(chapterId);
    }

    public List<CourseContent> getContentByTypeAndChapter(String type, int chapterId) {
        validateContentType(type);
        return contentRepository.findByTypeAndChapterId(type, chapterId);
    }

    // New method to get all content by courseId and chapterId
    public List<CourseContent> getAllContentByCourseIdAndChapterId(int courseId, int chapterId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found");
        }

        Optional<CourseChapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty()) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " not found");
        }
        if (chapter.get().getCourse().getId() != courseId) {
            throw new IllegalArgumentException("Chapter with ID " + chapterId + " does not belong to course " + courseId);
        }

        return contentRepository.findByChapterId(chapterId);
    }


    @Transactional
    public CourseContent updateContent(int id, CourseContent updatedContent) {
        CourseContent existingContent = getContentById(id);
        validateContentType(updatedContent.getType());

        existingContent.setTitle(updatedContent.getTitle());
        existingContent.setContent(updatedContent.getContent());
        existingContent.setType(updatedContent.getType());

        return contentRepository.save(existingContent);
    }

    @Transactional
    public void deleteContent(int id) {
        CourseContent content = getContentById(id);
        contentRepository.delete(content);
    }

    private void validateContentType(String type) {
        if (type == null || (!VALID_TYPES.contains(type.toLowerCase()) && !type.trim().isEmpty())) {
            throw new IllegalArgumentException("Invalid content type. Must be 'video', 'pdf', or a valid future type");
        }
    }
}