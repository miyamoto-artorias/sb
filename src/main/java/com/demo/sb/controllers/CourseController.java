package com.demo.sb.controllers;


import com.demo.sb.dto.CourseDTO;
import com.demo.sb.entity.Category;
import com.demo.sb.entity.Course;
import com.demo.sb.repository.CategoryRepository;
import com.demo.sb.service.CourseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CategoryRepository categoryRepository;


    @PostMapping("/{teacherId}")
    public ResponseEntity<Course> createCourse(@RequestBody CourseDTO courseDto, @PathVariable int teacherId) {
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setPicture(courseDto.getPicture());
        course.setPrice(courseDto.getPrice());
        course.setTags(courseDto.getTags());

        // Map category IDs to actual Category entities
        List<Category> categories = categoryRepository.findAllById(courseDto.getCategoryIds());
        course.setCategories(categories);

        Course createdCourse = courseService.createCourse(course, teacherId);
        return ResponseEntity.ok(createdCourse);
    }    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable int id) {
        try {
            Course course = courseService.getCourseById(id);
            
            // If the course is not public, we need to check if it should be visible in a separate endpoint
            // that includes user authentication
            if (!course.isPublic()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "This course is private. Use authenticated endpoints to access it."));
            }
            
            return ResponseEntity.ok(course);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
    
    /**
     * Get a course by ID with authentication
     * This endpoint should be called with authentication and will check if the user has access
     * to the requested course (either public, or private but user is teacher/requester)
     */
    @GetMapping("/authenticated/{courseId}/user/{userId}")
    public ResponseEntity<?> getAuthenticatedCourseById(@PathVariable int courseId, @PathVariable int userId) {
        try {
            Course course = courseService.getCourseByIdWithAuth(courseId, userId);
            
            // Convert to a DTO to avoid serialization issues
            Map<String, Object> courseMap = new HashMap<>();
            courseMap.put("id", course.getId());
            courseMap.put("title", course.getTitle());
            courseMap.put("description", course.getDescription());
            courseMap.put("picture", course.getPicture());
            courseMap.put("price", course.getPrice());
            courseMap.put("isPublic", course.isPublic());
            
            // Add teacher information
            Map<String, Object> teacherMap = new HashMap<>();
            teacherMap.put("id", course.getTeacher().getId());
            teacherMap.put("fullName", course.getTeacher().getFullName());
            courseMap.put("teacher", teacherMap);
            
            return ResponseEntity.ok(courseMap);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Course>> getCoursesByTeacher(@PathVariable int teacherId) {
        List<Course> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(@RequestParam String query) {
        List<Course> courses = courseService.searchCourses(query);
        List<CourseDTO> courseDTOs = convertToCourseDTOs(courses);
        return ResponseEntity.ok(courseDTOs);
    }
    
    private List<CourseDTO> convertToCourseDTOs(List<Course> courses) {
        return courses.stream().map(course -> {
            CourseDTO dto = new CourseDTO();
            dto.setTitle(course.getTitle());
            dto.setDescription(course.getDescription());
            dto.setPicture(course.getPicture());
            dto.setPrice(course.getPrice());
            dto.setTags(course.getTags());
            
            // Extract category IDs
            List<Integer> categoryIds = course.getCategories() != null ?
                course.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList()) :
                new ArrayList<>();
            dto.setCategoryIds(categoryIds);
            
            return dto;
        }).collect(Collectors.toList());
    }
      @PostMapping("/request/{courseRequestId}/teacher/{teacherId}")
    public ResponseEntity<?> createCourseForRequest(@PathVariable int courseRequestId,
                                                    @PathVariable int teacherId,
                                                    @RequestBody CourseDTO courseDto) {
        try {
            Course course = courseService.createCourseForRequest(courseRequestId, teacherId, courseDto);
            
            // Convert to DTO to avoid serialization issues with lazy-loaded associations
            CourseDTO responseDto = new CourseDTO();
            responseDto.setTitle(course.getTitle());
            responseDto.setDescription(course.getDescription());
            responseDto.setPicture(course.getPicture());
            responseDto.setPrice(course.getPrice());
            responseDto.setTags(course.getTags());
            
            // Add course ID to the response
            Map<String, Object> response = new HashMap<>();
            response.put("id", course.getId());
            response.put("title", course.getTitle());
            response.put("description", course.getDescription());
            response.put("picture", course.getPicture());
            response.put("price", course.getPrice());
            response.put("isPublic", course.isPublic());
            response.put("message", "Course successfully created for request");
            
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
      /**
     * Get all courses created from a user's "done" requests
     * These are the private courses created specifically for a user
     */
    @GetMapping("/user-requests/{userId}")
    public ResponseEntity<?> getCoursesFromUserRequests(@PathVariable int userId) {
        try {
            List<Course> courses = courseService.getCoursesFromCompletedRequests(userId);
            
            // Convert to detailed course maps with chapters and content
            List<Map<String, Object>> responseCourses = courses.stream()
                .map(course -> {
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("id", course.getId());
                    courseMap.put("title", course.getTitle());
                    courseMap.put("description", course.getDescription());
                    courseMap.put("picture", course.getPicture());
                    courseMap.put("price", course.getPrice());
                    courseMap.put("isPublic", course.isPublic());
                    courseMap.put("tags", course.getTags());
                    
                    // Add teacher information
                    Map<String, Object> teacherMap = new HashMap<>();
                    teacherMap.put("id", course.getTeacher().getId());
                    teacherMap.put("fullName", course.getTeacher().getFullName());
                    courseMap.put("teacher", teacherMap);
                    
                    // Add chapters information if available
                    if (course.getChapters() != null) {
                        List<Map<String, Object>> chaptersMap = course.getChapters().stream()
                            .map(chapter -> {
                                Map<String, Object> chapterMap = new HashMap<>();
                                chapterMap.put("id", chapter.getId());
                                chapterMap.put("title", chapter.getTitle());
                                chapterMap.put("description", chapter.getDescription());
                                chapterMap.put("type", chapter.getType());
                                
                                // Add content information if available
                                if (chapter.getContents() != null) {
                                    List<Map<String, Object>> contentsMap = chapter.getContents().stream()
                                        .map(content -> {
                                            Map<String, Object> contentMap = new HashMap<>();
                                            contentMap.put("id", content.getId());
                                            contentMap.put("title", content.getTitle());
                                            contentMap.put("type", content.getType());
                                            contentMap.put("content", content.getContent());
                                            return contentMap;
                                        })
                                        .collect(Collectors.toList());
                                    chapterMap.put("contents", contentsMap);
                                }
                                
                                // Add quiz information if available
                                if (chapter.getQuizzes() != null) {
                                    List<Map<String, Object>> quizzesMap = chapter.getQuizzes().stream()
                                        .map(quiz -> {
                                            Map<String, Object> quizMap = new HashMap<>();
                                            quizMap.put("id", quiz.getQuizId());
                                            quizMap.put("title", quiz.getTitle());
                                            quizMap.put("description", quiz.getDescription());
                                            quizMap.put("timeLimit", quiz.getTimeLimit());
                                            quizMap.put("passingScore", quiz.getPassingScore());
                                            quizMap.put("maxAttempts", quiz.getMaxAttempts());
                                            quizMap.put("status", quiz.getStatus());
                                            quizMap.put("createdAt", quiz.getCreatedAt());
                                            quizMap.put("updatedAt", quiz.getUpdatedAt());
                                            
                                            // Include quiz questions if available
                                            if (quiz.getQuestions() != null) {
                                                List<Map<String, Object>> questionsMap = quiz.getQuestions().stream()
                                                    .map(question -> {
                                                        Map<String, Object> questionMap = new HashMap<>();
                                                        questionMap.put("id", question.getQuestionId());
                                                        questionMap.put("questionText", question.getQuestionText());
                                                        questionMap.put("questionType", question.getQuestionType());
                                                        questionMap.put("options", question.getOptions());
                                                        questionMap.put("correctAnswer", question.getCorrectAnswer());
                                                        questionMap.put("correctAnswers", question.getCorrectAnswers());
                                                        questionMap.put("points", question.getPoints());
                                                        questionMap.put("matchingPairs", question.getMatchingPairs());
                                                        questionMap.put("orderingSequence", question.getOrderingSequence());
                                                        return questionMap;
                                                    })
                                                    .collect(Collectors.toList());
                                                quizMap.put("questions", questionsMap);
                                            }
                                            
                                            return quizMap;
                                        })
                                        .collect(Collectors.toList());
                                    chapterMap.put("quizzes", quizzesMap);
                                }
                                
                                return chapterMap;
                            })
                            .collect(Collectors.toList());
                        courseMap.put("chapters", chaptersMap);
                    }
                    
                    // Add categories if available
                    if (course.getCategories() != null) {
                        List<Map<String, Object>> categoriesMap = course.getCategories().stream()
                            .map(category -> {
                                Map<String, Object> categoryMap = new HashMap<>();
                                categoryMap.put("id", category.getId());
                                categoryMap.put("title", category.getTitle());
                                categoryMap.put("description", category.getDescription());
                                return categoryMap;
                            })
                            .collect(Collectors.toList());
                        courseMap.put("categories", categoriesMap);
                    }
                    
                    return courseMap;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(responseCourses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}