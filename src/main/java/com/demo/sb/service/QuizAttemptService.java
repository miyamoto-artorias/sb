package com.demo.sb.service;

import com.demo.sb.dto.QuizAttemptDto;
import com.demo.sb.entity.*;
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
import java.util.HashMap;

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

        // Create the attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setCompletedAt(new Date());
        attempt.setScore(0);
        attempt.setStatus("PENDING");
        
        // Save the attempt first to get an ID
        QuizAttempt savedAttempt = attemptRepository.save(attempt);
        
        // Add responses
        if (attemptDto.getResponses() != null && !attemptDto.getResponses().isEmpty()) {
            final QuizAttempt finalAttempt = savedAttempt;
            attemptDto.getResponses().forEach((questionId, response) -> 
                finalAttempt.addResponse(questionId, response)
            );
        }
        
        // Calculate score
        calculateScore(savedAttempt, quiz);
        
        // Save again with responses and score
        savedAttempt = attemptRepository.save(savedAttempt);
        
        return QuizAttemptDto.fromEntity(savedAttempt);
    }

    private void calculateScore(QuizAttempt attempt, Quiz quiz) {
        if (attempt.getResponses() == null || attempt.getResponses().isEmpty()) {
            attempt.setScore(0);
            attempt.setStatus("FAILED");
            return;
        }

        double totalPoints = 0;
        double earnedPoints = 0;
        
        for (QuizAttemptResponse response : attempt.getResponses()) {
            Long questionId = response.getQuestionId();
            String answer = response.getResponse();
            
            // Find the question in the quiz
            QuizQuestion question = quiz.getQuestions().stream()
                .filter(q -> q.getQuestionId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + questionId));
            
            totalPoints += question.getPoints();
            
            switch (question.getQuestionType()) {
                case MULTIPLE_CHOICE_SINGLE:
                case TRUE_FALSE:
                    if (answer.equals(question.getCorrectAnswer())) {
                        earnedPoints += question.getPoints();
                    }
                    break;
                case MULTIPLE_CHOICE_MULTIPLE:
                    // Split the response string into individual answers
                    String[] answers = answer.split(",");
                    for (String ans : answers) {
                        if (question.getCorrectAnswers().contains(ans.trim())) {
                            earnedPoints += question.getPoints() / question.getCorrectAnswers().size();
                        }
                    }
                    break;
                case SHORT_TEXT:
                    if (answer.equalsIgnoreCase(question.getCorrectAnswer())) {
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

    public List<QuizAttemptDto> getQuizAttempts(Long quizId) {
        List<QuizAttempt> attempts = attemptRepository.findByQuizId(quizId);
        return attempts.stream()
            .map(QuizAttemptDto::fromEntity)
            .collect(Collectors.toList());
    }
} 