package com.demo.sb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.ArrayList;
import com.demo.sb.entity.Quiz;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CourseChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonBackReference("course-chapters")
    private Course course;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    @JsonManagedReference("chapter-contents")
    private List<CourseContent> contents;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    @JsonManagedReference("chapter-quizzes")
    private List<Quiz> quizzes = new ArrayList<>();
}
