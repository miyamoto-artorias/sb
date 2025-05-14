package com.demo.sb.controllers;

import com.demo.sb.dto.QuizDto;
import com.demo.sb.dto.QuizAttemptDto;
import com.demo.sb.service.QuizService;
import com.demo.sb.service.QuizAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters/{chapterId}/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizAttemptService attemptService;

    /**
     * POST /api/chapters/{chapterId}/quizzes
     * Body = QuizDto { title, description, timeLimit, passingScore, maxAttempts, questions[] }
     */
    @PostMapping
    public ResponseEntity<QuizDto> createQuiz(
            @PathVariable int chapterId,
            @RequestBody QuizDto quizDto) {
        QuizDto created = quizService.createQuiz(chapterId, quizDto);
        return ResponseEntity.ok(created);
    }

    /**
     * GET /api/chapters/{chapterId}/quizzes
     * Lists all quizzes under the given chapter.
     */
    @GetMapping
    public ResponseEntity<List<QuizDto>> listQuizzes(
            @PathVariable int chapterId) {
        List<QuizDto> quizzes = quizService.getQuizzesByChapter(chapterId);
        return ResponseEntity.ok(quizzes);
    }

    /**
     * POST /api/chapters/{chapterId}/quizzes/{quizId}/attempt
     * Submit an attempt for a specific quiz in a chapter
     */
    @PostMapping("/{quizId}/attempt")
    public ResponseEntity<QuizAttemptDto> submitQuizAttempt(
            @PathVariable int chapterId,
            @PathVariable Long quizId,
            @RequestParam int userId,
            @RequestBody QuizAttemptDto attemptDto) {
        QuizAttemptDto result = attemptService.submitQuizAttempt(quizId, userId, attemptDto);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/chapters/{chapterId}/quizzes/{quizId}/attempts
     * Get all attempts for a specific quiz
     */
    @GetMapping("/{quizId}/attempts")
    public ResponseEntity<List<QuizAttemptDto>> getQuizAttempts(
            @PathVariable int chapterId,
            @PathVariable Long quizId) {
        List<QuizAttemptDto> attempts = attemptService.getQuizAttempts(quizId);
        return ResponseEntity.ok(attempts);
    }
    
    /**
     * GET /api/chapters/{chapterId}/quizzes/{quizId}/attempts/user/{userId}
     * Get all attempts by a specific user for a specific quiz
     */
    @GetMapping("/{quizId}/attempts/user/{userId}")
    public ResponseEntity<List<QuizAttemptDto>> getUserQuizAttempts(
            @PathVariable int chapterId,
            @PathVariable Long quizId,
            @PathVariable int userId) {
        List<QuizAttemptDto> attempts = attemptService.getUserQuizAttempts(quizId, userId);
        return ResponseEntity.ok(attempts);
    }
} 