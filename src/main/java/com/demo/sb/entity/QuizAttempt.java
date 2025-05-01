package com.demo.sb.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "quiz_attempt")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAttemptResponse> responses = new ArrayList<>();

    @Column(name = "score")
    private double score;
    
    @Column(name = "completed_at")
    private Date completedAt;
    
    @Column(name = "status")
    private String status; // PASSED, FAILED

    public void addResponse(Long questionId, String response) {
        QuizAttemptResponse attemptResponse = new QuizAttemptResponse(this, questionId, response);
        this.responses.add(attemptResponse);
    }
}
