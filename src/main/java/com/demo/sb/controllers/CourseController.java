package com.demo.sb.controllers;


import com.demo.sb.dto.CourseDTO;
import com.demo.sb.entity.Category;
import com.demo.sb.entity.Course;
import com.demo.sb.repository.CategoryRepository;
import com.demo.sb.service.CourseService;
import com.demo.sb.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Operation(
        summary = "Create a new course with image upload",
        description = "Creates a new course with optional image upload using multipart form data"
    )
    @PostMapping(value = "/{teacherId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCourse(
            @Parameter(description = "Title of the course") @RequestPart("title") String title,
            @Parameter(description = "Description of the course") @RequestPart("description") String description,
            @Parameter(description = "Image file for the course") @RequestPart(value = "pictureFile", required = false) MultipartFile pictureFile,
            @Parameter(description = "Price of the course") @RequestPart("price") String priceStr,
            @Parameter(description = "IDs of course categories") @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            @Parameter(description = "Tags for the course") @RequestParam(value = "tags", required = false) List<String> tags,
            @Parameter(description = "ID of the teacher") @PathVariable int teacherId) {
        
        try {
            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description);
            course.setPublic(false); // Set isPublic to false by default
            
            // Process file upload if provided
            if (pictureFile != null && !pictureFile.isEmpty()) {
                String fileName = fileStorageService.storeFile(pictureFile);
                String fileUrl = "/uploads/" + fileName;
                course.setPicture(fileUrl);
            }
            
            // Parse and set the price
            float price = Float.parseFloat(priceStr);
            course.setPrice(price);
            
            // Map category IDs to actual Category entities
            List<Category> categories = categoryIds != null ? 
                categoryRepository.findAllById(categoryIds) : 
                new ArrayList<>();
            course.setCategories(categories);
            
            // Set tags
            course.setTags(tags != null ? tags : new ArrayList<>());
            
            Course createdCourse = courseService.createCourse(course, teacherId);
            return ResponseEntity.ok(createdCourse);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file: " + ex.getMessage()));
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid number format: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
    
    // JSON-based version of create course (for backward compatibility)
    @Operation(
        summary = "Create a new course with JSON data",
        description = "Creates a new course using JSON data (without file upload)"
    )
    @PostMapping(value = "/{teacherId}/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> createCourseJson(
            @Parameter(description = "Course data") @RequestBody CourseDTO courseDto, 
            @Parameter(description = "ID of the teacher") @PathVariable int teacherId) {
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setPicture(courseDto.getPicture());
        course.setPrice(courseDto.getPrice());
        course.setTags(courseDto.getTags());
        course.setPublic(false); // Set isPublic to false by default

        // Map category IDs to actual Category entities
        List<Category> categories = categoryRepository.findAllById(courseDto.getCategoryIds());
        course.setCategories(categories);

        Course createdCourse = courseService.createCourse(course, teacherId);
        return ResponseEntity.ok(createdCourse);
    }
    
    @Operation(
        summary = "Update course public status",
        description = "Updates whether a course is public or private"
    )
    @PutMapping("/{courseId}/public")
    public ResponseEntity<?> updateCoursePublicStatus(
            @Parameter(description = "ID of the course") @PathVariable int courseId,
            @Parameter(description = "Whether the course should be public") @RequestBody Map<String, Boolean> requestBody) {
        try {
            Boolean isPublic = requestBody.get("isPublic");
            if (isPublic == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing 'isPublic' field in request body"));
            }
            
            Course updatedCourse = courseService.updateCoursePublicStatus(courseId, isPublic);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedCourse.getId());
            response.put("isPublic", updatedCourse.isPublic());
            response.put("message", "Course public status updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
    
    @Operation(
        summary = "Create a course for a request with image upload",
        description = "Creates a course for a specific request with optional image upload using multipart form data"
    )
    @PostMapping(value = "/request/{courseRequestId}/teacher/{teacherId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCourseForRequest(
            @Parameter(description = "ID of the course request") @PathVariable int courseRequestId,
            @Parameter(description = "ID of the teacher") @PathVariable int teacherId,
            @Parameter(description = "Title of the course") @RequestPart("title") String title,
            @Parameter(description = "Description of the course") @RequestPart("description") String description,
            @Parameter(description = "Image file for the course") @RequestPart(value = "pictureFile", required = false) MultipartFile pictureFile,
            @Parameter(description = "Price of the course") @RequestPart("price") String priceStr,
            @Parameter(description = "IDs of course categories") @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            @Parameter(description = "Tags for the course") @RequestParam(value = "tags", required = false) List<String> tags) {
        
        try {
            CourseDTO courseDto = new CourseDTO();
            courseDto.setTitle(title);
            courseDto.setDescription(description);
            
            // Process file upload if provided
            if (pictureFile != null && !pictureFile.isEmpty()) {
                String fileName = fileStorageService.storeFile(pictureFile);
                String fileUrl = "/uploads/" + fileName;
                courseDto.setPicture(fileUrl);
            }
            
            // Parse and set the price
            float price = Float.parseFloat(priceStr);
            courseDto.setPrice(price);
            
            // Set category IDs directly
            courseDto.setCategoryIds(categoryIds != null ? categoryIds : new ArrayList<>());
            
            // Set tags
            courseDto.setTags(tags != null ? tags : new ArrayList<>());
            
            Course course = courseService.createCourseForRequest(courseRequestId, teacherId, courseDto);
            
            // Return response
            Map<String, Object> response = new HashMap<>();
            response.put("id", course.getId());
            response.put("title", course.getTitle());
            response.put("description", course.getDescription());
            response.put("picture", course.getPicture());
            response.put("price", course.getPrice());
            response.put("isPublic", course.isPublic());
            response.put("message", "Course successfully created for request");
            
            return ResponseEntity.ok(response);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file: " + ex.getMessage()));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid number format: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
    
    // JSON-based version for backward compatibility
    @PostMapping(value = "/request/{courseRequestId}/teacher/{teacherId}/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCourseForRequestJson(
            @PathVariable int courseRequestId,
            @PathVariable int teacherId,
            @RequestBody CourseDTO courseDto) {
        try {
            Course course = courseService.createCourseForRequest(courseRequestId, teacherId, courseDto);
            
            // Return response
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable int id) {
        try {
            Course course = courseService.getCourseById(id);
            
            // Return the course regardless of its public status
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
    public ResponseEntity<List<Map<String, Object>>> getCoursesByTeacher(@PathVariable int teacherId) {
        List<Course> courses = courseService.getCoursesByTeacher(teacherId);
        
        // Convert to detailed course maps without lazy-loaded relationships
        List<Map<String, Object>> responseList = courses.stream()
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
            
        return ResponseEntity.ok(responseList);
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
      /**
     * Get all courses created from a user's "done" requests
     * These are the private courses created specifically for a user
     */
    @GetMapping("/user-requests/{userId}")
    public ResponseEntity<?> getCoursesFromUserRequests(@PathVariable int userId) {
        try {
            List<Course> courses = courseService.getCoursesFromDoneRequests(userId);
            
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

    /**
     * Get all courses where the associated request is assigned to a specific teacher
     * @param teacherId The ID of the teacher who handled the requests
     * @return A list of courses created from requests assigned to this teacher
     */
    @GetMapping("/request-teacher/{teacherId}")
    public ResponseEntity<List<Map<String, Object>>> getCoursesByRequestTeacherId(@PathVariable int teacherId) {
        List<Course> courses = courseService.getCoursesByRequestTeacherId(teacherId);
        
        // Convert to detailed course maps without lazy-loaded relationships
        List<Map<String, Object>> responseList = courses.stream()
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
                
                // Add course request information if available
                if (course.getCourseRequest() != null) {
                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("id", course.getCourseRequest().getId());
                    requestMap.put("subject", course.getCourseRequest().getSubject());
                    requestMap.put("price", course.getCourseRequest().getPrice());
                    requestMap.put("status", course.getCourseRequest().getStatus());
                    
                    // Add student information
                    if (course.getCourseRequest().getStudent() != null) {
                        Map<String, Object> studentMap = new HashMap<>();
                        studentMap.put("id", course.getCourseRequest().getStudent().getId());
                        studentMap.put("fullName", course.getCourseRequest().getStudent().getFullName());
                        requestMap.put("student", studentMap);
                    }
                    
                    // Add categories if available
                    if (course.getCourseRequest().getCategories() != null) {
                        List<Map<String, Object>> categoriesMap = course.getCourseRequest().getCategories().stream()
                            .map(category -> {
                                Map<String, Object> categoryMap = new HashMap<>();
                                categoryMap.put("id", category.getId());
                                categoryMap.put("title", category.getTitle());
                                return categoryMap;
                            })
                            .collect(Collectors.toList());
                        requestMap.put("categories", categoriesMap);
                    }
                    
                    courseMap.put("courseRequest", requestMap);
                }
                
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
            
        return ResponseEntity.ok(responseList);
    }
}