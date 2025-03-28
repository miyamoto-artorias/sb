package com.demo.sb.service;


import com.demo.sb.entity.Category;
import com.demo.sb.entity.Course;
import com.demo.sb.repository.CategoryRepository;
import com.demo.sb.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CourseRepository courseRepository;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Course> getCoursesByCategory(int categoryId) {
        return courseRepository.findCoursesByCategoryId(categoryId);
    }

    // Use findAll() instead of getAllCategories()
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    // Find category by ID
    public Category findById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + id + " not found"));
    }

}