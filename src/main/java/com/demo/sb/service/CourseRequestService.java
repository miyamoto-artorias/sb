package com.demo.sb.service;


import com.demo.sb.entity.CourseRequest;
import com.demo.sb.repository.CourseRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseRequestService {
    @Autowired
    private CourseRequestRepository courseRequestRepository;

    @Transactional
    public CourseRequest createCourseRequest(CourseRequest request) {
        return courseRequestRepository.save(request);
    }

    public Optional<CourseRequest> findById(int id) {
        return courseRequestRepository.findById(id);
    }

    public List<CourseRequest> getRequestsByTeacher(int teacherId) {
        return courseRequestRepository.findByTeacherId(teacherId);
    }

    @Transactional
    public void acceptRequest(int id) {
        Optional<CourseRequest> request = courseRequestRepository.findById(id);
        if (request.isPresent()) {
            request.get().setStatus("accepted");
            courseRequestRepository.save(request.get());
        } else {
            throw new RuntimeException("Course request not found");
        }
    }

    @Transactional
    public void rejectRequest(int id) {
        Optional<CourseRequest> request = courseRequestRepository.findById(id);
        if (request.isPresent()) {
            request.get().setStatus("rejected");
            courseRequestRepository.save(request.get());
        } else {
            throw new RuntimeException("Course request not found");
        }
    }
}