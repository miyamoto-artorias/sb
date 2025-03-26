package com.demo.sb.service;


import com.demo.sb.entity.Course;
import com.demo.sb.entity.Student;
import com.demo.sb.repository.CourseRepository;
import com.demo.sb.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Transactional
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> findById(int id) {
        return studentRepository.findById(id);
    }

    public List<Course> getEnrolledCourses(int studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (student.isPresent()) {
            return student.get().getEnrolledCourses();
        }
        throw new RuntimeException("Student not found");
    }

    @Transactional
    public void enrollInCourse(int studentId, int courseId) {
        Optional<Student> student = studentRepository.findById(studentId);
        Optional<Course> course = courseRepository.findById(courseId);
        if (student.isPresent() && course.isPresent()) {
            student.get().getEnrolledCourses().add(course.get());
            studentRepository.save(student.get());
        } else {
            throw new RuntimeException("Student or Course not found");
        }
    }
}