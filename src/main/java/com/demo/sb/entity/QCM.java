package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class QCM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String picture; // URL or file path

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
}