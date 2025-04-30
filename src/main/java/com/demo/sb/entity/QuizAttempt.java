package com.demo.sb.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Entity
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ElementCollection
    private Map<QuizQuestion, String> responses;

    private double score;
    private Date completedAt;
    private String status; // PASSED, FAILED

}
