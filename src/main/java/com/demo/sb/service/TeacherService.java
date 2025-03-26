package com.demo.sb.service;


import com.demo.sb.entity.Course;
import com.demo.sb.entity.Teacher;
import com.demo.sb.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Transactional
    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Optional<Teacher> findById(int id) {
        return teacherRepository.findById(id);
    }

    public List<Course> getUploadedCourses(int teacherId) {
        Optional<Teacher> teacher = teacherRepository.findById(teacherId);
        if (teacher.isPresent()) {
            return teacher.get().getUploadedCourses();
        }
        throw new RuntimeException("Teacher not found");
    }
}