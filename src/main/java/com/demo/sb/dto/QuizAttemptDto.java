package com.demo.sb.dto;

import com.demo.sb.entity.QuizAttempt;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class QuizAttemptDto {
    private Long attemptId;
    private Long quizId;
    private int userId;
    private Map<Long, String> responses; // questionId -> response
    private double score;
    private String status; // PASSED, FAILED

    public static QuizAttemptDto fromEntity(QuizAttempt attempt) {
        QuizAttemptDto dto = new QuizAttemptDto();
        dto.setAttemptId(attempt.getAttemptId());
        dto.setQuizId(attempt.getQuiz().getQuizId());
        dto.setUserId(attempt.getUser().getId());
        dto.setScore(attempt.getScore());
        dto.setStatus(attempt.getStatus());
        
        // Copy the responses map
        if (attempt.getResponses() != null) {
            dto.setResponses(new HashMap<>(attempt.getResponses()));
        }
        
        return dto;
    }
} 