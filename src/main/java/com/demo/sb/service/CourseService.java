package com.demo.sb.service;


import com.demo.sb.entity.Course;
import com.demo.sb.entity.Teacher;
import com.demo.sb.repository.CourseRepository;
import com.demo.sb.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Transactional
    public Course createCourse(Course course, int teacherId) {
        Optional<Teacher> teacher = teacherRepository.findById(teacherId);
        if (teacher.isPresent()) {
            course.setTeacher(teacher.get());
            return courseRepository.save(course);
        }
        throw new RuntimeException("Teacher not found");
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    public Optional<Course> findById(int id) {
        return courseRepository.findById(id);
    }
}