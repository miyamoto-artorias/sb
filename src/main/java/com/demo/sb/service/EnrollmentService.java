package com.demo.sb.service;

import com.demo.sb.entity.Course;
import com.demo.sb.entity.Enrollment;
import com.demo.sb.entity.User;
import com.demo.sb.repository.CourseRepository;
import com.demo.sb.repository.EnrollmentRepository;
import com.demo.sb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository; // Assume this exists
    private final CourseRepository courseRepository; // Assume this exists

    @Autowired
    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Enrollment createEnrollment(Integer userId, Integer courseId) {
        try {
            // Check for existing enrollment first - if it exists, return it
            Optional<Enrollment> existingEnrollment = enrollmentRepository.findByUser_IdAndCourse_Id(userId, courseId);
            if (existingEnrollment.isPresent()) {
                return existingEnrollment.get();
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

            Enrollment enrollment = new Enrollment();
            enrollment.setUser(user);
            enrollment.setCourse(course);
            enrollment.setProgress(0.0f);
            enrollment.setPoints(0);

            return enrollmentRepository.save(enrollment);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Error creating enrollment: " + e.getMessage(), e);
        }
    }
    /*public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    } */

    // Update method names to match repository
    public List<Enrollment> getEnrollmentsByUserId(Integer userId) {
        return enrollmentRepository.findByUser_Id(userId);
    }

    public List<Enrollment> getEnrollmentsByCourseId(Integer courseId) {
        return enrollmentRepository.findByCourse_Id(courseId);
    }


    @Transactional(readOnly = true)
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAllWithUserAndCourseIds();
    }




    public Optional<Enrollment> getEnrollmentById(Integer id) {
        return enrollmentRepository.findById(id);
    }



    @Transactional
    public Enrollment updateProgress(Integer enrollmentId, float progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }

        enrollment.setProgress(progress);
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment updatePoints(Integer enrollmentId, int points) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }

        enrollment.setPoints(points);
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void deleteEnrollment(Integer id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new RuntimeException("Enrollment not found");
        }
        enrollmentRepository.deleteById(id);
    }
}