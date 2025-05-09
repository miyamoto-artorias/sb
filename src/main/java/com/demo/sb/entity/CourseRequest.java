package com.demo.sb.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "course_request")
public class CourseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    private String subject;
/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; */
    @ManyToMany
    @JoinTable(name = "course_request_category",
               joinColumns = @JoinColumn(name = "course_request_id"),
               inverseJoinColumns = @JoinColumn(name = "category_id")
                )
private List<Category> categories; // Changed from 'category' to 'categories'


    private float price;

    private String status; // e.g., "accepted", "rejected" , "done"


        @OneToOne(mappedBy = "courseRequest", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"courseRequest", "hibernateLazyInitializer", "handler"})
    private Course createdCourse;

}