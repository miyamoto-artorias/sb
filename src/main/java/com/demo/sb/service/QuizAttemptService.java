package com.demo.sb.service;

import com.demo.sb.dto.QuizAttemptDto;
import com.demo.sb.entity.Quiz;
import com.demo.sb.entity.QuizAttempt;
import com.demo.sb.entity.User;
import com.demo.sb.entity.QuizQuestion;
import com.demo.sb.repository.QuizAttemptRepository;
import com.demo.sb.repository.QuizRepository;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

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
        List<QuizAttempt> prev = attemptRepository.findByUserIdAndQuizId(userId, quizId);
        if (prev.size() >= quiz.getMaxAttempts()) {
            throw new IllegalStateException("Maximum attempts reached for this quiz");
        }

        // Create and save the attempt first
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setCompletedAt(new Date());
        attempt.setScore(0);
        attempt.setStatus("PENDING");
        QuizAttempt saved = attemptRepository.save(attempt);

        // Manually insert responses using correct column names
        Map<Long, String> responses = attemptDto.getResponses();
        if (responses != null && !responses.isEmpty()) {
            responses.forEach((qid, ans) ->
                jdbcTemplate.update(
                    "INSERT INTO quiz_attempt_responses (quiz_attempt_attempt_id, attempt_id, question_id, responses_key, response) VALUES (?, ?, ?, ?, ?)",
                    saved.getAttemptId(), saved.getAttemptId(), qid, qid, ans
                )
            );
        }

        // Calculate score based on the DTO map
        double totalPoints = 0;
        double earnedPoints = 0;
        if (responses != null) {
            for (Map.Entry<Long, String> entry : responses.entrySet()) {
                Long questionId = entry.getKey();
                String answer = entry.getValue();
                QuizQuestion question = quiz.getQuestions().stream()
                    .filter(q -> q.getQuestionId().equals(questionId))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Question not found: " + questionId));
                totalPoints += question.getPoints();
                switch (question.getQuestionType()) {
                    case MULTIPLE_CHOICE_SINGLE:
                    case TRUE_FALSE:
                        if ((question.getCorrectAnswer() != null && answer.equals(question.getCorrectAnswer())) ||
                            (question.getCorrectAnswers() != null && question.getCorrectAnswers().contains(answer))) {
                            earnedPoints += question.getPoints();
                        }
                        break;
                    case MULTIPLE_CHOICE_MULTIPLE:
                        for (String a : answer.split(",")) {
                            if (question.getCorrectAnswers().contains(a.trim())) {
                                earnedPoints += question.getPoints() / question.getCorrectAnswers().size();
                            }
                        }
                        break;
                    case SHORT_TEXT:
                        if (answer.equalsIgnoreCase(question.getCorrectAnswer())) {
                            earnedPoints += question.getPoints();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        double finalScore = totalPoints > 0 ? (earnedPoints / totalPoints) * 100 : 0;
        saved.setScore(finalScore);
        saved.setStatus(finalScore >= quiz.getPassingScore() ? "PASSED" : "FAILED");
        attemptRepository.save(saved);

        // Build and return DTO manually
        QuizAttemptDto dto = new QuizAttemptDto();
        dto.setAttemptId(saved.getAttemptId());
        dto.setQuizId(quiz.getQuizId());
        dto.setUserId(user.getId());
        dto.setResponses(responses);
        dto.setScore(saved.getScore());
        dto.setStatus(saved.getStatus());
        return dto;
    }

    public List<QuizAttemptDto> getQuizAttempts(Long quizId) {
        return attemptRepository.findByQuizId(quizId)
            .stream()
            .map(QuizAttemptDto::fromEntity)
            .collect(Collectors.toList());
    }
} 