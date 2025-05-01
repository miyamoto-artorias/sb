package com.demo.sb.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizDto {
    private Long quizId;
    private String title;
    private String description;
    private int timeLimit;       // in minutes
    private double passingScore;
    private int maxAttempts;
    private List<QuizQuestionDto> questions;
} 