package com.demo.sb.dto;

import com.demo.sb.entity.QuizQuestionType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QuizQuestionDto {
    private Long questionId;
    private String questionText;
    private QuizQuestionType questionType;
    private List<String> options;
    private String correctAnswer;
    private List<String> correctAnswers;
    private double points;
    private Map<String, String> matchingPairs;
    private List<String> orderingSequence;
} 