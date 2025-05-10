package com.demo.sb.service;


import com.demo.sb.dto.CourseDTO;
import com.demo.sb.entity.Category;
import com.demo.sb.entity.Course;
import com.demo.sb.entity.CourseRequest;
import com.demo.sb.entity.Teacher;
import com.demo.sb.repository.CategoryRepository;
import com.demo.sb.repository.CourseRepository;
import com.demo.sb.repository.CourseRequestRepository;
import com.demo.sb.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired private CourseRepository courseRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private CourseRequestRepository courseRequestRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Transactional
    public Course createCourse(Course course, int teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    /**
     * Get courses by teacher ID
     * @param teacherId ID of the teacher
     * @return List of courses taught by this teacher
     */
    @Transactional(readOnly = true)
    public List<Course> getCoursesByTeacher(int teacherId) {
        // First verify teacher exists
        teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher with ID " + teacherId + " not found"));
        
        // Get courses by teacher ID
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        
        // Eagerly load necessary data to avoid lazy loading issues
        courses.forEach(course -> {
            // Make sure teacher is loaded
            if (course.getTeacher() != null) {
                course.getTeacher().getFullName(); // Access property to ensure it's loaded
            }
            
            // Load chapters and their contents
            if (course.getChapters() != null) {
                course.getChapters().forEach(chapter -> {
                    // Access chapter properties to ensure it's loaded
                    chapter.getTitle();
                    chapter.getDescription();
                    
                    // Load chapter contents
                    if (chapter.getContents() != null) {
                        chapter.getContents().forEach(content -> {
                            // Access content properties to ensure it's loaded
                            content.getTitle();
                            content.getType();
                            content.getContent();
                        });
                    }
                    
                    // Load quizzes and questions
                    if (chapter.getQuizzes() != null) {
                        chapter.getQuizzes().forEach(quiz -> {
                            // Access quiz properties
                            quiz.getTitle();
                            quiz.getDescription();
                            
                            // Load quiz questions
                            if (quiz.getQuestions() != null) {
                                quiz.getQuestions().forEach(question -> {
                                    // Access question properties
                                    question.getQuestionText();
                                    question.getQuestionType();
                                    question.getOptions();
                                });
                            }
                        });
                    }
                });
            }
            
            // Load categories
            if (course.getCategories() != null) {
                course.getCategories().size(); // Access size to trigger loading
            }
            
            // Load tags
            if (course.getTags() != null) {
                course.getTags().size();
            }
        });
        
        return courses;
    }

    @Transactional
    public Course createCourseForRequest(int courseRequestId, int teacherId, CourseDTO courseDto) {
        // Validate and retrieve the course request
        CourseRequest courseRequest = courseRequestRepository.findById(courseRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Course request not found"));
                
        // Check if the course request status is "accepted"
        if (!"accepted".equalsIgnoreCase(courseRequest.getStatus())) {
            throw new IllegalStateException("Course request status must be 'accepted' to create a course");
        }

        // Create a new course
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setPicture(courseDto.getPicture());
        course.setPrice(courseDto.getPrice());
        course.setTags(courseDto.getTags());
        course.setPublic(false); // Set isPublic to false for requested courses
        
        // Link the course to the courseRequest
        course.setCourseRequest(courseRequest);

        // Map category IDs to actual Category entities
        List<Category> categories = categoryRepository.findAllById(courseDto.getCategoryIds());
        course.setCategories(categories);

        // Set the teacher
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
        course.setTeacher(teacher);
        
        Course savedCourse = courseRepository.save(course);
        
        // Update course request status to indicate a course has been created
        courseRequest.setCreatedCourse(savedCourse);
        courseRequest.setStatus("done");
        courseRequestRepository.save(courseRequest);

        return savedCourse;
    }    public Course getCourseById(int id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + id));
                
        return course;
    }
    
    /**
     * Get a course by ID, with authorization check
     * @param courseId The ID of the course to retrieve
     * @param userId The ID of the user trying to access the course
     * @return The course if the user is authorized to see it
     * @throws SecurityException if the user is not authorized to see this course
     */
    public Course getCourseByIdWithAuth(int courseId, int userId) {
        Course course = getCourseById(courseId);
        
        // If the course is public, anyone can see it
        if (course.isPublic()) {
            return course;
        }
        
        // If not public, check if user is the teacher of the course
        if (course.getTeacher().getId() == userId) {
            return course;
        }
        
        // If not public and user is not the teacher, check if user is the requester
        if (course.getCourseRequest() != null && 
            course.getCourseRequest().getStudent() != null &&
            course.getCourseRequest().getStudent().getId() == userId) {
            return course;
        }
        
        // If none of the above, user is not authorized to see this course
        throw new SecurityException("You are not authorized to access this course");
    }public List<Course> getAllCourses() {
        // Only return public courses by default
        return courseRepository.findByIsPublicTrue();
    }
      public List<Course> searchCourses(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCourses(); // This already filters for public courses
        }
        
        // Use PostgreSQL full-text search for complex queries with multiple keywords
        // Then filter to include only public courses
        return courseRepository.fullTextSearch(searchTerm.trim())
                .stream()
                .filter(Course::isPublic)
                .collect(Collectors.toList());
    }

    /**
     * Get courses that were created from a user's requests with "done" status
     * @param userId ID of the user who made the requests
     * @return List of courses created from the user's requests
     */
    @Transactional(readOnly = true)
    public List<Course> getCoursesFromCompletedRequests(int userId) {
        // Find all requests from this user with "done" status
        List<CourseRequest> completedRequests = courseRequestRepository.findByStudentIdAndStatus(userId, "done");
        
        // Extract the created courses from these requests
        List<Course> courses = completedRequests.stream()
                .map(CourseRequest::getCreatedCourse)
                .filter(course -> course != null) // Filter out any null courses
                .collect(Collectors.toList());
        
        // Eagerly load chapters and content for each course to avoid lazy loading issues
        courses.forEach(course -> {
            if (course.getChapters() != null) {
                course.getChapters().forEach(chapter -> {
                    if (chapter.getContents() != null) {
                        // Access contents to load them into memory
                        chapter.getContents().size();
                    }
                    
                    // Eager load quizzes and their questions
                    if (chapter.getQuizzes() != null) {
                        chapter.getQuizzes().forEach(quiz -> {
                            if (quiz.getQuestions() != null) {
                                // Access questions to load them into memory
                                quiz.getQuestions().size();
                            }
                        });
                    }
                });
            }
            if (course.getCategories() != null) {
                // Access categories to load them into memory
                course.getCategories().size();
            }
        });
        
        return courses;
    }
}