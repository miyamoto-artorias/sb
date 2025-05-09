package com.demo.sb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String picture; // URL or file path

    private float price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonIgnoreProperties({"uploadedCourses", "hibernateLazyInitializer", "handler"})
    private Teacher teacher;



    @ManyToMany
    @JoinTable(
        name = "course_category",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
        )
private List<Category> categories; // Changed from 'category' to 'categories'


    @ElementCollection
    @CollectionTable(
        name = "course_tags", 
        joinColumns = @JoinColumn(name = "course_id")
    )
    @Column(name = "tag")
    private List<String> tags;



    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonManagedReference("course-chapters")
    private List<CourseChapter> chapters;

    @Column(nullable = false)
    private boolean isPublic = true; // Default to true for regular courses



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_request_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CourseRequest courseRequest;

}