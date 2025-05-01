package com.demo.sb.service;

import com.demo.sb.dto.QuizAttemptDto;
import com.demo.sb.entity.Quiz;
import com.demo.sb.entity.QuizAttempt;
import com.demo.sb.entity.QuizQuestion;
import com.demo.sb.entity.User;
import com.demo.sb.repository.QuizAttemptRepository;
import com.demo.sb.repository.QuizRepository;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizAttemptService {
    
    @Autowired
    private QuizAttemptRepository attemptRepository;
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public QuizAttemptDto submitQuizAttempt(Long quizId, int userId, QuizAttemptDto attemptDto) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if user has exceeded max attempts
        List<QuizAttempt> previousAttempts = attemptRepository.findByUserIdAndQuizId(userId, quizId);
        if (previousAttempts.size() >= quiz.getMaxAttempts()) {
            throw new IllegalStateException("Maximum attempts reached for this quiz");
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setResponses(attemptDto.getResponses());
        attempt.setCompletedAt(new Date());
        
        // Calculate score
        calculateScore(attempt, quiz);
        
        QuizAttempt savedAttempt = attemptRepository.save(attempt);
        return mapToDto(savedAttempt);
    }

    private void calculateScore(QuizAttempt attempt, Quiz quiz) {
        if (attempt.getResponses() == null || attempt.getResponses().isEmpty()) {
            attempt.setScore(0);
            attempt.setStatus("FAILED");
            return;
        }

        double totalPoints = 0;
        double earnedPoints = 0;
        
        for (Map.Entry<Long, String> entry : attempt.getResponses().entrySet()) {
            Long questionId = entry.getKey();
            String response = entry.getValue();
            
            // Find the question in the quiz
            QuizQuestion question = quiz.getQuestions().stream()
                .filter(q -> q.getQuestionId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + questionId));
            
            totalPoints += question.getPoints();
            
            switch (question.getQuestionType()) {
                case MULTIPLE_CHOICE_SINGLE:
                case TRUE_FALSE:
                    if (response.equals(question.getCorrectAnswer())) {
                        earnedPoints += question.getPoints();
                    }
                    break;
                case MULTIPLE_CHOICE_MULTIPLE:
                    // Split the response string into individual answers
                    String[] answers = response.split(",");
                    for (String answer : answers) {
                        if (question.getCorrectAnswers().contains(answer.trim())) {
                            earnedPoints += question.getPoints() / question.getCorrectAnswers().size();
                        }
                    }
                    break;
                case SHORT_TEXT:
                    if (response.equalsIgnoreCase(question.getCorrectAnswer())) {
                        earnedPoints += question.getPoints();
                    }
                    break;
                // Add more cases for other question types as needed
            }
        }
        
        double finalScore = totalPoints > 0 ? (earnedPoints / totalPoints) * 100 : 0;
        attempt.setScore(finalScore);
        attempt.setStatus(finalScore >= attempt.getQuiz().getPassingScore() ? "PASSED" : "FAILED");
    }

    private QuizAttemptDto mapToDto(QuizAttempt attempt) {
        QuizAttemptDto dto = new QuizAttemptDto();
        dto.setAttemptId(attempt.getAttemptId());
        dto.setQuizId(attempt.getQuiz().getQuizId());
        dto.setUserId(attempt.getUser().getId());
        dto.setScore(attempt.getScore());
        dto.setStatus(attempt.getStatus());
        dto.setResponses(attempt.getResponses());
        return dto;
    }

    public List<QuizAttemptDto> getQuizAttempts(Long quizId) {
        List<QuizAttempt> attempts = attemptRepository.findByQuizId(quizId);
        return attempts.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }
} 