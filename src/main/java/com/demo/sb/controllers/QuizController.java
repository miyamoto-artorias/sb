package com.demo.sb.controllers;

import com.demo.sb.dto.QuizDto;
import com.demo.sb.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters/{chapterId}/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

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
} 