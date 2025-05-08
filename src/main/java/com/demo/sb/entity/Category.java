package com.demo.sb.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonIgnore // ✅ Use this instead
    private List<Course> courses; // Inverse relationship
//List<Course> courses: This field allows you to navigate from a Category to all the Course entities associated with it (e.g., category.getCourses()).
//Retrieve all courses in a specific category (e.g., "Show me all courses in the 'Programming' category").

@ManyToMany(mappedBy = "categories")
@JsonIgnore
private List<CourseRequest> courseRequests; // Inverse relationship for CourseRequest
//Retrieve all CourseRequest in a specific category for a teacher
}