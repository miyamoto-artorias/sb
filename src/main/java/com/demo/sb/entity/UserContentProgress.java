package com.demo.sb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_content_progress",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "content_id"}))
public class UserContentProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    @JsonIgnore
    private CourseContent content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    @JsonIgnore
    private Enrollment enrollment;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    // Constructor for quick creation
    public UserContentProgress(User user, CourseContent content, Enrollment enrollment) {
        this.user = user;
        this.content = content;
        this.enrollment = enrollment;
        this.completed = false;
    }
} 