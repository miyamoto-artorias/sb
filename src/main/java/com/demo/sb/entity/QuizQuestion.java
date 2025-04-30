package com.demo.sb.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Data
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;
    @Column(name = "question_order")
    private int questionOrder; // Renamed from 'order' to 'questionOrder' to avoid SQL reserved keyword conflict
    private String text;
    private String type; // MULTIPLE_CHOICE, TRUE_FALSE, etc.
    private String hint;

    @ElementCollection
    private List<String> options;

    @ElementCollection
    private List<String> correctAnswers;

    private double points;
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
