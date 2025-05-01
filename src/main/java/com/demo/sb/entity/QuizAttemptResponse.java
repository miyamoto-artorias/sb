package com.demo.sb.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "quiz_attempt_responses")
public class QuizAttemptResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false, referencedColumnName = "attempt_id")
    private QuizAttempt attempt;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "response", nullable = false)
    private String response;

    public QuizAttemptResponse(QuizAttempt attempt, Long questionId, String response) {
        this.attempt = attempt;
        this.questionId = questionId;
        this.response = response;
    }
} 