package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "course_request")
public class CourseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private float price;

    private String status; // e.g., "pending", "accepted", "rejected"
}