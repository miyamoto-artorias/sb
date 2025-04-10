package com.demo.sb.service;

import com.demo.sb.entity.Course;
import com.demo.sb.entity.CourseChapter;
import com.demo.sb.entity.CourseContent;
import com.demo.sb.repository.CourseChapterRepository;
import com.demo.sb.repository.CourseContentRepository;
import com.demo.sb.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private CourseRepository courseRepository;

    private static final List<String> VALID_TYPES = Arrays.asList("video", "pdf");
    private static final String UPLOAD_DIR = "C:\\Users\\altya\\Desktop\\proj_integ\\SBDB\\"; // Directory to store uploaded files

    @Transactional
    public CourseContent createContent(CourseContent content, int courseId, int chapterId, MultipartFile file) throws IOException {
        validateContentType(content.getType());

        // Validate course and chapter
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found");
        }

        Optional<CourseChapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty() || chapter.get().getCourse().getId() != courseId) {
            throw new IllegalArgumentException("Invalid chapter for course");
        }

        // Handle file upload if present
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            content.setContent(filePath.toString()); // Store file path in content
        }

        content.setChapter(chapter.get());
        return contentRepository.save(content);
    }

    public File getFile(int courseId, int chapterId, int contentId) {
        CourseContent content = getContentById(contentId);
        validateCourseAndChapter(content, courseId, chapterId);
        return new File(content.getContent());
    }

    public List<CourseContent> getContentByCourseIdAndChapterId(int courseId, int chapterId) {
        validateCourseAndChapterExistence(courseId, chapterId);
        return contentRepository.findByChapterId(chapterId);
    }

    // Other existing methods remain mostly unchanged, just add validation where needed
    private void validateCourseAndChapterExistence(int courseId, int chapterId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found");
        }
        Optional<CourseChapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty() || chapter.get().getCourse().getId() != courseId) {
            throw new IllegalArgumentException("Invalid chapter for course");
        }
    }

    private void validateCourseAndChapter(CourseContent content, int courseId, int chapterId) {
        validateCourseAndChapterExistence(courseId, chapterId);
        if (content.getChapter().getId() != chapterId) {
            throw new IllegalArgumentException("Content does not belong to specified chapter");
        }
    }

    private void validateContentType(String type) {
        if (type == null || (!VALID_TYPES.contains(type.toLowerCase()) && !type.trim().isEmpty())) {
            throw new IllegalArgumentException("Invalid content type. Must be 'video', 'pdf', or a valid future type");
        }
    }

    public CourseContent getContentById(int id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content with ID " + id + " not found"));
    }
}