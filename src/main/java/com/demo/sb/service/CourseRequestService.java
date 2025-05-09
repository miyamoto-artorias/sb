package com.demo.sb.service;


import com.demo.sb.dto.CourseRequestDTO;
import com.demo.sb.entity.Category;
import com.demo.sb.entity.CourseRequest;
import com.demo.sb.entity.Teacher;
import com.demo.sb.entity.User;
import com.demo.sb.repository.CategoryRepository;
import com.demo.sb.repository.CourseRequestRepository;
import com.demo.sb.repository.TeacherRepository;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseRequestService {
    @Autowired
    private CourseRequestRepository courseRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public CourseRequestDTO createCourseRequest(CourseRequestDTO requestDTO) {
        // Convert DTO to entity
        CourseRequest entity = convertToEntity(requestDTO);
        
        // Save entity
        CourseRequest savedEntity = courseRequestRepository.save(entity);
        
        // Convert back to DTO and return
        return convertToDTO(savedEntity);
    }

    public CourseRequestDTO findById(int id) {
        Optional<CourseRequest> requestOpt = courseRequestRepository.findById(id);
        return requestOpt.map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Course request not found with ID: " + id));
    }

    public List<CourseRequestDTO> getRequestsByTeacher(int teacherId) {
        List<CourseRequest> requests = courseRequestRepository.findByTeacherId(teacherId);
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CourseRequestDTO> getRequestsByStudent(int studentId) {
        List<CourseRequest> requests = courseRequestRepository.findByStudentId(studentId);
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptRequest(int id) {
        Optional<CourseRequest> request = courseRequestRepository.findById(id);
        if (request.isPresent()) {
            request.get().setStatus("accepted");
            courseRequestRepository.save(request.get());
        } else {
            throw new EntityNotFoundException("Course request not found with ID: " + id);
        }
    }

    @Transactional
    public void rejectRequest(int id) {
        Optional<CourseRequest> request = courseRequestRepository.findById(id);
        if (request.isPresent()) {
            request.get().setStatus("rejected");
            courseRequestRepository.save(request.get());
        } else {
            throw new EntityNotFoundException("Course request not found with ID: " + id);
        }
    }
    
    // Helper methods for DTO conversion
    private CourseRequest convertToEntity(CourseRequestDTO dto) {
        CourseRequest entity = new CourseRequest();
        
        // Set fields from DTO to entity
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        
        // Get and set Student from repo
        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + dto.getStudentId()));
        entity.setStudent(student);
        
        // Get and set Teacher from repo
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + dto.getTeacherId()));
        entity.setTeacher(teacher);
        
        // Convert category IDs to Category entities
        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("No categories found for the given IDs");
        }
        entity.setCategories(categories);
        
        entity.setSubject(dto.getSubject());
        entity.setPrice(dto.getPrice());
        entity.setStatus(dto.getStatus());
        
        return entity;
    }
    
    private CourseRequestDTO convertToDTO(CourseRequest entity) {
        CourseRequestDTO dto = new CourseRequestDTO();
        
        dto.setId(entity.getId());
        dto.setStudentId(entity.getStudent().getId());
        dto.setTeacherId(entity.getTeacher().getId());
        dto.setSubject(entity.getSubject());
        
        // Convert Category entities to IDs
        List<Integer> categoryIds = entity.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        dto.setCategoryIds(categoryIds);
        
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus());
        
        return dto;
    }
}