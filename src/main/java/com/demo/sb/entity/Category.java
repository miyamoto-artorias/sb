package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<Course> courses; // Inverse relationship
//List<Course> courses: This field allows you to navigate from a Category to all the Course entities associated with it (e.g., category.getCourses()).
//Retrieve all courses in a specific category (e.g., "Show me all courses in the 'Programming' category").
@ManyToMany(mappedBy = "categories")
private List<CourseRequest> courseRequests; // Inverse relationship for CourseRequest
//Retrieve all CourseRequest in a specific category for a teacher
}