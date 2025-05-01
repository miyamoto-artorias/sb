package com.demo.sb.service;

import com.demo.sb.dto.QuizAttemptDto;
import com.demo.sb.entity.*;
import com.demo.sb.repository.QuizAttemptRepository;
import com.demo.sb.repository.QuizRepository;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
public class QuizAttemptService {
    
    @Autowired
    private QuizAttemptRepository attemptRepository;
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public QuizAttemptDto submitQuizAttempt(Long quizId, int userId, QuizAttemptDto attemptDto) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check max attempts
        List<QuizAttempt> previous = attemptRepository.findByUserIdAndQuizId(userId, quizId);
        if (previous.size() >= quiz.getMaxAttempts()) {
            throw new IllegalStateException("Maximum attempts reached for this quiz");
        }

        // Create and save attempt to get ID
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setCompletedAt(new Date());
        attempt.setScore(0);
        attempt.setStatus("PENDING");
        QuizAttempt saved = attemptRepository.save(attempt);

        // Insert responses manually
        Map<Long, String> responses = attemptDto.getResponses();
        if (responses != null && !responses.isEmpty()) {
            responses.forEach((qid, ans) -> 
                jdbcTemplate.update(
                    "INSERT INTO quiz_attempt_responses (quiz_attempt_attempt_id, responses_key, response) VALUES (?, ?, ?)",
                    saved.getAttemptId(), qid, ans
                )
            );
        }

        // Calculate score based on DTO map
        double total = 0, earned = 0;
        if (responses != null) {
            for (Map.Entry<Long, String> entry : responses.entrySet()) {
                Long qid = entry.getKey();
                String answer = entry.getValue();
                QuizQuestion question = quiz.getQuestions().stream()
                    .filter(q -> q.getQuestionId().equals(qid))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Question not found: " + qid));
                total += question.getPoints();
                switch (question.getQuestionType()) {
                    case MULTIPLE_CHOICE_SINGLE:
                    case TRUE_FALSE:
                        if (answer.equals(question.getCorrectAnswer())) {
                            earned += question.getPoints();
                        }
                        break;
                    case MULTIPLE_CHOICE_MULTIPLE:
                        for (String a : answer.split(",")) {
                            if (question.getCorrectAnswers().contains(a.trim())) {
                                earned += question.getPoints() / question.getCorrectAnswers().size();
                            }
                        }
                        break;
                    case SHORT_TEXT:
                        if (answer.equalsIgnoreCase(question.getCorrectAnswer())) {
                            earned += question.getPoints();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        double finalScore = total > 0 ? (earned / total) * 100 : 0;
        saved.setScore(finalScore);
        saved.setStatus(finalScore >= quiz.getPassingScore() ? "PASSED" : "FAILED");
        attemptRepository.save(saved);

        // Build and return DTO
        QuizAttemptDto dto = new QuizAttemptDto();
        dto.setAttemptId(saved.getAttemptId());
        dto.setQuizId(quiz.getQuizId());
        dto.setUserId(user.getId());
        dto.setScore(saved.getScore());
        dto.setStatus(saved.getStatus());
        dto.setResponses(responses != null ? new HashMap<>(responses) : null);
        return dto;
    }

    public List<QuizAttemptDto> getQuizAttempts(Long quizId) {
        List<QuizAttempt> attempts = attemptRepository.findByQuizId(quizId);
        return attempts.stream()
            .map(QuizAttemptDto::fromEntity)
            .collect(Collectors.toList());
    }
} 