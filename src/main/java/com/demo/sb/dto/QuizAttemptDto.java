package com.demo.sb.dto;

import lombok.Data;
import java.util.Map;

@Data
public class QuizAttemptDto {
    private Long attemptId;
    private Long quizId;
    private int userId;
    private Map<Long, String> responses; // questionId -> response
    private double score;
    private String status; // PASSED, FAILED
} 