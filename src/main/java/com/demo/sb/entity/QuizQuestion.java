package com.demo.sb.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;
import java.util.Map;

@Data
@Entity
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    private String questionText;
    
    @Enumerated(EnumType.STRING)
    private QuizQuestionType questionType;

    @ElementCollection
    private List<String> options; // For multiple choice questions

    private String correctAnswer;

    @ElementCollection
    private List<String> correctAnswers; // For multiple correct answers

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    @JsonBackReference("quiz-questions")
    private Quiz quiz;

    private double points;
    
    @ElementCollection
    private Map<String, String> matchingPairs; // For matching type questions
    
    @ElementCollection
    private List<String> orderingSequence; // For ordering type questions
}
