package com.demo.sb.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "enrollment")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private float progress; // Progress specific to this course for this user
    private int points;    // Points specific to this course (optional)
}