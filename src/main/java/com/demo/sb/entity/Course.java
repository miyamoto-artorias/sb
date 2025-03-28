package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
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
    private Teacher teacher;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<MaterialSupport> docs;
/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; */
    @ManyToMany
    @JoinTable(
        name = "course_category",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
        )
private List<Category> categories; // Changed from 'category' to 'categories'


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<QCM> quizzes;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;

}