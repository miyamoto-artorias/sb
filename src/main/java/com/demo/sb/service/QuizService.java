package com.demo.sb.service;

import com.demo.sb.dto.QuizDto;
import com.demo.sb.dto.QuizQuestionDto;
import com.demo.sb.entity.CourseChapter;
import com.demo.sb.entity.Quiz;
import com.demo.sb.entity.QuizQuestion;
import com.demo.sb.repository.CourseChapterRepository;
import com.demo.sb.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseChapterRepository chapterRepository;

    /**
     * Create a new Quiz (with questions) under the given chapter.
     */
    @Transactional
    public QuizDto createQuiz(int chapterId, QuizDto quizDto) {
        CourseChapter chapter = chapterRepository.findById(chapterId)
            .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id " + chapterId));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setTimeLimit(quizDto.getTimeLimit());
        quiz.setPassingScore(quizDto.getPassingScore());
        quiz.setMaxAttempts(quizDto.getMaxAttempts());
        quiz.setStatus("DRAFT");
        quiz.setCreatedAt(new Date());
        quiz.setUpdatedAt(new Date());
        quiz.setChapter(chapter);

        if (quizDto.getQuestions() != null) {
            List<QuizQuestion> questions = new ArrayList<>();
            for (QuizQuestionDto qDto : quizDto.getQuestions()) {
                QuizQuestion question = new QuizQuestion();
                question.setQuestionText(qDto.getQuestionText());
                question.setQuestionType(qDto.getQuestionType());
                question.setOptions(qDto.getOptions());
                question.setCorrectAnswer(qDto.getCorrectAnswer());
                question.setCorrectAnswers(qDto.getCorrectAnswers());
                question.setPoints(qDto.getPoints());
                question.setMatchingPairs(qDto.getMatchingPairs());
                question.setOrderingSequence(qDto.getOrderingSequence());
                question.setQuiz(quiz);
                questions.add(question);
            }
            quiz.setQuestions(questions);
        }

        Quiz saved = quizRepository.save(quiz);
        return mapToDto(saved);
    }

    /**
     * List all quizzes under a chapter.
     */
    public List<QuizDto> getQuizzesByChapter(int chapterId) {
        return quizRepository.findByChapterId(chapterId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private QuizDto mapToDto(Quiz quiz) {
        QuizDto dto = new QuizDto();
        dto.setQuizId(quiz.getQuizId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setTimeLimit(quiz.getTimeLimit());
        dto.setPassingScore(quiz.getPassingScore());
        dto.setMaxAttempts(quiz.getMaxAttempts());

        if (quiz.getQuestions() != null) {
            dto.setQuestions(
                quiz.getQuestions().stream()
                    .map(q -> {
                        QuizQuestionDto qDto = new QuizQuestionDto();
                        qDto.setQuestionId(q.getQuestionId());
                        qDto.setQuestionText(q.getQuestionText());
                        qDto.setQuestionType(q.getQuestionType());
                        qDto.setOptions(q.getOptions());
                        qDto.setCorrectAnswer(q.getCorrectAnswer());
                        qDto.setCorrectAnswers(q.getCorrectAnswers());
                        qDto.setPoints(q.getPoints());
                        qDto.setMatchingPairs(q.getMatchingPairs());
                        qDto.setOrderingSequence(q.getOrderingSequence());
                        return qDto;
                    })
                    .collect(Collectors.toList())
            );
        }

        return dto;
    }
} 