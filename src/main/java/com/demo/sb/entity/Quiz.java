package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;
    private String title;
    private String description;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizQuestion> questions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private CourseChapter chapter;

    private int timeLimit; // in minutes
    private double passingScore;
    private int maxAttempts;
    private String status; // ACTIVE, DRAFT, ARCHIVED
    private Date createdAt;
    private Date updatedAt;

}