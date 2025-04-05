package com.demo.sb.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class CourseChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private String type;

    // Add the inverse relationship to Course
    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;  // This matches mappedBy = "course" in Course entity


    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CourseContent> contents;  // Changed to List and renamed to contents


}
