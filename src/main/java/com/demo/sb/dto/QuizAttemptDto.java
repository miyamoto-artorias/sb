package com.demo.sb.dto;

import com.demo.sb.entity.QuizAttempt;
import com.demo.sb.entity.QuizAttemptResponse;
import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        
        // Convert responses to Map
        if (attempt.getResponses() != null) {
            Map<Long, String> responseMap = attempt.getResponses().stream()
                .collect(Collectors.toMap(
                    QuizAttemptResponse::getQuestionId,
                    QuizAttemptResponse::getResponse
                ));
            dto.setResponses(responseMap);
        }
        
        return dto;
    }
} 