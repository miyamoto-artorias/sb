package com.demo.sb.controllers;


import com.demo.sb.entity.Category;
import com.demo.sb.entity.Course;
import com.demo.sb.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category savedCategory = categoryService.createCategory(category); // Assume this method exists
        return ResponseEntity.ok(savedCategory);
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getCoursesByCategory(@PathVariable int id) {
        return ResponseEntity.ok(categoryService.getCoursesByCategory(id));
    }


    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories(); // Assume this method exists
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable int id) {
        Optional<Category> category = Optional.ofNullable(categoryService.findById(id)); // Assume this method exists
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}